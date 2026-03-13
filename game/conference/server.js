// Conference-mode Java Version Quiz Server
// Run: npm install && ADMIN_SECRET=your_secret npm start

const express = require('express');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const WebSocket = require('ws');
const http = require('http');
const { v4: uuidv4 } = require('uuid');
const sqlite3 = require('sqlite3').verbose();
const QRCode = require('qrcode');

const app = express();
const PORT = process.env.PORT || 3000;
const ADMIN_SECRET = process.env.ADMIN_SECRET;
// BASE_PATH: subpath prefix when deployed behind a reverse proxy (e.g. '/conference-game')
// Must start with '/' (or be empty). No trailing slash.
const BASE_PATH = (process.env.BASE_PATH || '').replace(/\/+$/, '');
const MAX_PLAYERS_PER_SESSION = parseInt(process.env.MAX_PLAYERS_PER_SESSION || '1000', 10);
const MAX_SESSIONS = parseInt(process.env.MAX_SESSIONS || '1000', 10);
const MAX_QR_SIZE = 2048; // Max QR PNG width/height to prevent memory exhaustion
const DEFAULT_FRAME_ANCESTORS = "'self' http://localhost:* http://127.0.0.1:* https://localhost:* https://127.0.0.1:*";
const FRAME_ANCESTORS = (process.env.FRAME_ANCESTORS || DEFAULT_FRAME_ANCESTORS).trim();

if (!ADMIN_SECRET) {
    console.error('Error: ADMIN_SECRET environment variable must be set');
    process.exit(1);
}

// Constant-time comparison to prevent timing attacks on admin secret
function secretMatch(input) {
    if (typeof input !== 'string' || input.length === 0) return false;
    const a = Buffer.from(input);
    const b = Buffer.from(ADMIN_SECRET);
    if (a.length !== b.length) return false;
    return crypto.timingSafeEqual(a, b);
}

// Trust proxy when behind reverse proxy (conference/cloud deployments)
// This ensures req.ip, req.protocol, etc. use X-Forwarded-* headers
if (process.env.TRUST_PROXY) {
    app.set('trust proxy', process.env.TRUST_PROXY === 'true' ? true : process.env.TRUST_PROXY);
}

function stripBasePathFromRequestPath(requestPath) {
    if (!BASE_PATH) return requestPath;
    if (requestPath === BASE_PATH) return '/';
    if (requestPath.startsWith(BASE_PATH + '/')) return requestPath.slice(BASE_PATH.length);
    return requestPath;
}

function isEmbeddablePagePath(requestPath) {
    const normalizedPath = stripBasePathFromRequestPath(requestPath);
    return /^\/(?:$|presenter(?:\/view(?:\/[^/]+)?)?)$/.test(normalizedPath);
}

// Security headers — prevent MIME-sniffing and clickjacking
app.use((req, res, next) => {
    res.setHeader('X-Content-Type-Options', 'nosniff');
    // Allow iframe embedding only for player/presenter HTML pages.
    // For those routes, CSP frame-ancestors is used to explicitly permit localhost.
    // Keep strict DENY for all other endpoints (API/admin/static/ws-upgrade paths).
    if (isEmbeddablePagePath(req.path)) {
        res.setHeader('Content-Security-Policy', `frame-ancestors ${FRAME_ANCESTORS}`);
    } else {
        res.setHeader('X-Frame-Options', 'DENY');
    }
    // Prevent caching of API responses — critical on conference WiFi where
    // aggressive caching proxies and mobile browsers cache JSON responses
    res.setHeader('Cache-Control', 'no-store, no-cache, must-revalidate');
    res.setHeader('Pragma', 'no-cache');
    next();
});

// Strip BASE_PATH prefix from incoming requests so all routes stay clean
if (BASE_PATH) {
    app.use((req, res, next) => {
        if (req.path === BASE_PATH || req.path.startsWith(BASE_PATH + '/')) {
            req.url = req.url.slice(BASE_PATH.length) || '/';
            next();
        } else {
            res.status(404).send('Not Found');
        }
    });
}

// Helper: send an HTML file with BASE_PATH injected as a global.
// Caches processed HTML in memory — avoids synchronous fs.readFileSync
// on every request, which blocks the event loop when 500 phones hit '/' at once.
const _htmlCache = new Map();
function sendHtmlWithBasePath(res, filePath) {
    let html = _htmlCache.get(filePath);
    if (!html) {
        html = fs.readFileSync(filePath, 'utf8');
        // Inject <base href> so relative asset paths (dist/bundle.js) resolve correctly
        // even when the browser URL is a deep subpath like /conference-game/presenter/view/XYZ
        const baseHref = `<base href="${BASE_PATH}/">`;
        const script = `<script>window.__BASE_PATH=${JSON.stringify(BASE_PATH)};</script>`;
        html = html.replace('<head>', `<head>\n${baseHref}`);
        html = html.replace('</head>', `${script}\n</head>`);
        _htmlCache.set(filePath, html);
    }
    res.type('html').send(html);
}

// Public static files — cache for 1 hour so that when 500 phones reconnect
// after a WiFi drop, their browsers serve player.bundle.js (~500KB) from
// disk cache instead of all hitting the server simultaneously.
app.use(express.static('public', { maxAge: '1h' }));
app.use(express.json({ limit: '16kb' }));

// Resolve quiz asset file paths once at startup instead of calling
// fs.existsSync on every request (sync I/O blocks the event loop
// under conference load with hundreds of simultaneous requests).
function resolveFirstExisting(filePaths) {
    for (const fp of filePaths) {
        if (fs.existsSync(fp)) return fp;
    }
    return null;
}

const _quizAssetPaths = {
    code: resolveFirstExisting([
        path.join(__dirname, 'public', 'code.json'),
        path.join(__dirname, '..', 'dist', 'code.json'),
    ]),
    objectSizes: resolveFirstExisting([
        path.join(__dirname, 'public', 'object-sizes.json'),
        path.join(__dirname, '..', 'dist', 'object-sizes.json'),
    ]),
    descriptions: resolveFirstExisting([
        path.join(__dirname, 'public', 'descriptions.json'),
        path.join(__dirname, '..', 'dist', 'descriptions.json'),
    ]),
};

// Serve quiz assets with fallback paths.
// These files don't change during a running session, so allow short-term
// browser caching. This is critical at conferences: when WiFi recovers and
// 500 phones reconnect simultaneously, the browser can serve quiz data from
// its cache instead of all hammering the server at once.
const QUIZ_ASSET_CACHE = 'public, max-age=300'; // 5 minutes

app.get('/code.json', (req, res) => {
    res.set('Cache-Control', QUIZ_ASSET_CACHE);
    if (_quizAssetPaths.code) {
        res.sendFile(_quizAssetPaths.code);
    } else {
        res.status(404).json({ error: 'code.json not found' });
    }
});

app.get('/object-sizes.json', (req, res) => {
    res.set('Cache-Control', QUIZ_ASSET_CACHE);
    if (_quizAssetPaths.objectSizes) {
        res.sendFile(_quizAssetPaths.objectSizes);
    } else {
        res.status(404).json({ error: 'object-sizes.json not found' });
    }
});

app.get('/descriptions.json', (req, res) => {
    res.set('Cache-Control', QUIZ_ASSET_CACHE);
    if (_quizAssetPaths.descriptions) {
        res.sendFile(_quizAssetPaths.descriptions);
    } else {
        res.status(404).json({ error: 'descriptions.json not found' });
    }
});

/**
 * Safe WebSocket send — wraps ws.send() in try/catch to prevent
 * crashes when a socket transitions to CLOSING between readyState
 * check and the actual send. Critical under conference load where
 * hundreds of connections churn simultaneously on spotty WiFi.
 */
function safeSend(ws, data) {
    try {
        if (ws && ws.readyState === WebSocket.OPEN) {
            ws.send(typeof data === 'string' ? data : JSON.stringify(data));
        }
    } catch (err) {
        // Socket raced to CLOSING/CLOSED — harmless, the close handler
        // will clean up. Log only at debug level to avoid log spam.
        console.debug('safeSend failed (socket closing):', err.message);
    }
}

// In-memory session storage (persisted with SQLite for crash protection)
let sessions = new Map(); // sessionId -> { state, currentQuestion, timerEnds, players: Set<uuid> }
let players = new Map(); // uuid -> { sessionId, displayName, score }
let wsConnections = new Map(); // uuid -> WebSocket
let playerLastSeen = new Map(); // uuid -> timestamp (ms)

const HEARTBEAT_TIMEOUT_MS = parseInt(process.env.HEARTBEAT_TIMEOUT_MS || '60000', 10); // 60s grace for mobile reconnects
const STALE_SWEEP_INTERVAL_MS = 5 * 1000;
const WS_PING_INTERVAL_MS = 20 * 1000;  // Protocol-level ping to survive NAT/firewall timeouts (reduced from 25s; many mobile NATs timeout at 30s)
const WS_RATE_LIMIT_WINDOW_MS = 1000;   // Rate-limit window
const WS_RATE_LIMIT_MAX = 20;           // Max messages per window per connection (raised for burst reconnect scenarios)

// SQLite database for persistence
const DATA_DIR = path.join(__dirname, 'data');
if (!fs.existsSync(DATA_DIR)) {
    fs.mkdirSync(DATA_DIR, { recursive: true });
}
const DB_PATH = path.join(DATA_DIR, 'quiz.sqlite');
let db = new sqlite3.Database(DB_PATH, (err) => {
    if (err) {
        console.error('Database error:', err);
        process.exit(1);
    }
    // Enable WAL mode for much better concurrent write performance
    // (hundreds of players answering simultaneously at a conference)
    db.run('PRAGMA journal_mode=WAL', (walErr) => {
        if (walErr) console.error('Failed to enable WAL mode:', walErr);
    });
    initDb();
});

function initDb() {
    db.serialize(() => {
        db.run(`CREATE TABLE IF NOT EXISTS sessions (
            sessionId TEXT PRIMARY KEY,
            createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
            state TEXT DEFAULT 'waiting',
            currentQuestion INTEGER DEFAULT 0,
            timerEnds DATETIME
        )`);

        db.run(`CREATE TABLE IF NOT EXISTS players (
            uuid TEXT PRIMARY KEY,
            sessionId TEXT,
            displayName TEXT,
            score INTEGER DEFAULT 0,
            joinedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (sessionId) REFERENCES sessions(sessionId)
        )`);

        db.run(`CREATE TABLE IF NOT EXISTS answers (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            sessionId TEXT,
            uuid TEXT,
            questionId INTEGER,
            answer INTEGER,
            correct INTEGER,
            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (sessionId) REFERENCES sessions(sessionId),
            FOREIGN KEY (uuid) REFERENCES players(uuid)
        )`);

        // Schema migrations for sessions table
        db.all('PRAGMA table_info(sessions)', (err, cols) => {
            if (err) {
                console.error('Failed to read sessions schema:', err);
                return;
            }
            const colNames = new Set((cols || []).map((c) => c.name));
            const migrations = [];
            if (!colNames.has('name')) migrations.push('ALTER TABLE sessions ADD COLUMN name TEXT DEFAULT \'Untitled Session\'');
            if (!colNames.has('currentQuestionIndex')) migrations.push('ALTER TABLE sessions ADD COLUMN currentQuestionIndex INTEGER DEFAULT 0');
            if (!colNames.has('quizMode')) migrations.push('ALTER TABLE sessions ADD COLUMN quizMode TEXT DEFAULT \'java\'');

            migrations.forEach((sql) => {
                db.run(sql, (mErr) => {
                    if (mErr) console.error('Migration failed:', sql, mErr);
                });
            });

            // Load persisted sessions into memory and reset volatile runtime state
            db.all('SELECT * FROM sessions', (loadErr, rows) => {
                if (loadErr) {
                    console.error('Failed to load sessions:', loadErr);
                    return;
                }

                sessions.clear();
                (rows || []).forEach((row) => {
                    let state = row.state || 'waiting';
                    let timerEnds = row.timerEnds ? new Date(row.timerEnds) : null;
                    if (timerEnds && timerEnds.getTime() < Date.now()) {
                        state = 'waiting';
                        timerEnds = null;
                    }

                    sessions.set(row.sessionId, {
                        name: row.name || 'Untitled Session',
                        state: state,
                        currentQuestion: row.currentQuestion ?? null,
                        currentQuestionIndex: row.currentQuestionIndex ?? 0,
                        timerEnds: timerEnds,
                        players: new Set(),
                        quizMode: row.quizMode || 'java',
                        playerAnswers: new Set(),
                    });
                });

                // Reload players that still belong to an active session
                db.all('SELECT * FROM players', (pErr, playerRows) => {
                    if (pErr) {
                        console.error('Failed to load players:', pErr);
                    } else {
                        (playerRows || []).forEach((row) => {
                            if (sessions.has(row.sessionId)) {
                                players.set(row.uuid, {
                                    sessionId: row.sessionId,
                                    displayName: row.displayName,
                                    score: row.score ?? 0,
                                });
                                // Don't add to session.players here — that Set tracks
                                // connected WebSocket clients only. Players re-join
                                // when they reconnect via the 'join' WS message.
                            } else {
                                // Orphaned player – session no longer exists, clean up
                                db.run('DELETE FROM players WHERE uuid = ?', [row.uuid]);
                            }
                        });
                    }

                    // Reload per-session playerAnswers for the current active question
                    // so players cannot re-answer after a server restart
                    db.all(
                        'SELECT DISTINCT sessionId, uuid FROM answers WHERE questionId = (SELECT currentQuestion FROM sessions s2 WHERE s2.sessionId = answers.sessionId)',
                        (aErr, answerRows) => {
                            if (!aErr && answerRows) {
                                answerRows.forEach((row) => {
                                    const s = sessions.get(row.sessionId);
                                    if (s) s.playerAnswers.add(row.uuid);
                                });
                            }

                            console.log(
                                `Database initialised – sessions: ${sessions.size}, players: ${players.size}`
                            );
                        }
                    );
                });
            });
        });
    });
}

// Name generation with guaranteed uniqueness
const ADJECTIVES = [
    'Happy',
    'Clever',
    'Quick',
    'Brave',
    'Calm',
    'Eager',
    'Fierce',
    'Gentle',
    'Humble',
    'Jolly',
    'Kind',
    'Lively',
    'Mighty',
    'Noble',
    'Proud',
    'Quiet',
    'Rapid',
    'Strong',
    'Tiny',
    'Unique',
    'Vivid',
    'Wild',
    'Zesty',
    'Bold',
    'Curious',
    'Daring',
    'Energetic',
    'Friendly',
    'Glorious',
    'Inspiring',
    'Joyful',
];

const ANIMALS = [
    'Panda',
    'Eagle',
    'Wolf',
    'Bear',
    'Fox',
    'Lion',
    'Tiger',
    'Penguin',
    'Dolphin',
    'Elephant',
    'Giraffe',
    'Koala',
    'Owl',
    'Otter',
    'Rabbit',
    'Shark',
    'Turtle',
    'Whale',
    'Antelope',
    'Badger',
    'Cheetah',
    'Deer',
    'Emu',
    'Flamingo',
    'Goose',
    'Hedgehog',
    'Capybara',
    'Cat',
    'Dog',
    'Horse',
    'Squirrel',
    'Zebra',
];

// Optional blacklist of adjective|animal combinations that should never be generated
// Example line: 'tiny|elephant' (case-insensitive). You can populate the
// `BANNED_NAME_COMBINATIONS` environment variable with comma-separated
// `adjective|animal` entries to add to the set at runtime.
const BANNED_COMBINATIONS = new Set();
if (process.env.BANNED_NAME_COMBINATIONS) {
    process.env.BANNED_NAME_COMBINATIONS.split(',').map(s => s.trim()).filter(Boolean).forEach(pair => {
        BANNED_COMBINATIONS.add(pair.toLowerCase());
    });
}

function generateUniqueName(sessionId) {
    const existingNames = new Set();

    // Get names from in-memory map
    for (const [, player] of players.entries()) {
        if (player.sessionId === sessionId) {
            existingNames.add(player.displayName);
        }
    }

    let name;
    let attempts = 0;
    do {
        const adjCount = Math.random() < 0.95 ? 1 : Math.random() < 0.9 ? 2 : 3;
        // Sample adjectives without replacement to avoid duplicates like "DaringDaring"
        const shuffled = ADJECTIVES.slice().sort(() => Math.random() - 0.5);
        const adjs = shuffled.slice(0, adjCount);
        const animal = ANIMALS[Math.floor(Math.random() * ANIMALS.length)];
        name = adjs.join('') + animal;

        // Reject names that match banned adjective|animal pairs. Check each
        // adjective against the chosen animal (case-insensitive).
        const animalLower = animal.toLowerCase();
        let banned = false;
        for (const a of adjs) {
            const key = `${a.toLowerCase()}|${animalLower}`;
            if (BANNED_COMBINATIONS.has(key)) { banned = true; break; }
        }
        if (banned) {
            attempts++;
            continue; // generate another combination
        }
        attempts++;
    } while (existingNames.has(name) && attempts < 1000);

    if (attempts >= 1000) {
        // Fallback: append UUID suffix
        name = name + Math.random().toString(36).substr(2, 5);
    }

    return name;
}

// WebSocket handler
const wss = new WebSocket.Server({ noServer: true, maxPayload: 4096 });

wss.on('connection', (ws, request) => {
    // Parse UUID properly using URL API instead of fragile string split.
    // Prevents map-key pollution if extra query params are appended.
    let uuid;
    try {
        const params = new URL(request.url, 'http://localhost').searchParams;
        uuid = params.get('uuid');
    } catch {
        // Malformed URL
    }

    if (!uuid) {
        ws.close(1008, 'Missing uuid parameter');
        return;
    }

    // Close any existing socket for this UUID to prevent zombie connections
    const existingWs = wsConnections.get(uuid);
    if (existingWs && existingWs !== ws && existingWs.readyState === WebSocket.OPEN) {
        existingWs.close(4001, 'Replaced by new connection');
    }
    wsConnections.set(uuid, ws);
    ws.isAlive = true;
    console.log(`WebSocket connected: ${uuid}`);

    // Protocol-level pong handler (keeps NAT/firewall mapping alive on mobile)
    ws.on('pong', () => { ws.isAlive = true; });

    // Per-connection rate limiter state
    let rateLimitCount = 0;
    let rateLimitReset = Date.now() + WS_RATE_LIMIT_WINDOW_MS;

    ws.on('message', (message) => {
        // Rate limiting – prevent a single buggy/malicious client from flooding the server
        const now = Date.now();
        if (now > rateLimitReset) {
            rateLimitCount = 0;
            rateLimitReset = now + WS_RATE_LIMIT_WINDOW_MS;
        }
        rateLimitCount++;
        if (rateLimitCount > WS_RATE_LIMIT_MAX) {
            safeSend(ws, { type: 'error', message: 'Rate limit exceeded' });
            return;
        }

        try {
            const data = JSON.parse(message);
            handleMessage(uuid, data, ws);
        } catch (e) {
            console.error('Error parsing message:', e);
            safeSend(ws, { type: 'error', message: 'Invalid message format' });
        }
    });

    ws.on('close', () => {
        // Only delete the ws mapping if this ws is still the registered connection
        // (a new connection may have replaced it already via reconnect)
        if (wsConnections.get(uuid) === ws) {
            wsConnections.delete(uuid);
            // IMPORTANT: Do NOT delete playerLastSeen here!
            // The stale sweep checks playerLastSeen to decide when to evict.
            // If we delete it, the sweep treats the player as "never seen" and
            // kicks them within 5 seconds — destroying the 45s grace period.
            // Instead, keep the last-seen timestamp so the player gets the
            // full HEARTBEAT_TIMEOUT_MS window to reconnect on spotty WiFi.
        }
        console.log(`WebSocket disconnected: ${uuid}`);
        // Don't remove from session.players here — the heartbeat sweep
        // handles stale players after HEARTBEAT_TIMEOUT_MS, giving them
        // a grace period to reconnect on spotty WiFi.
    });

    ws.on('error', (err) => {
        console.error(`WebSocket error for ${uuid}:`, err);
    });
});

function handleMessage(uuid, data, ws) {
    const { type, sessionId, answer } = data;
    if (players.has(uuid)) {
        playerLastSeen.set(uuid, Date.now());
    }

    if (type === 'join-session-presenter') {
        // Presenter joining to monitor session
        const session = sessions.get(sessionId);
        if (session) {
            // Store/update presenter connection (always update for reconnects)
            session.presenterWs = ws;
            // Clear previous presenter stats interval (e.g. after reconnect)
            if (session._presenterStatsInterval) {
                clearInterval(session._presenterStatsInterval);
            }
            // Send initial session state
            safeSend(ws, {
                type: 'session-state',
                sessionId,
                state: session.state,
                currentQuestion: session.currentQuestion,
                playerCount: session.players.size,
            });
            // Start sending periodic stats updates
            session._presenterStatsInterval = setInterval(() => {
                if (ws.readyState === WebSocket.OPEN && session) {
                    let answeredCount = 0;
                    if (session.currentQuestion && session.playerAnswers) {
                        for (const uuid of session.players) {
                            if (session.playerAnswers.has(uuid)) {
                                answeredCount++;
                            }
                        }
                    }
                    safeSend(ws, {
                        type: 'stats-update',
                        playerCount: session.players.size,
                        answeredCount: answeredCount,
                    });
                } else {
                    clearInterval(session._presenterStatsInterval);
                    session._presenterStatsInterval = null;
                }
            }, 1000); // Send stats every 1 second
        }
    } else if (type === 'join') {
        const player = players.get(uuid);
        const session = sessions.get(sessionId);
        if (player && session && player.sessionId === sessionId) {
            // (Re-)add player to session on connect/reconnect
            session.players.add(uuid);
            playerLastSeen.set(uuid, Date.now());
            const hasTimer = session.timerEnds && new Date(session.timerEnds) > new Date();
            const secondsLeft = hasTimer
                ? Math.max(0, Math.round((new Date(session.timerEnds) - Date.now()) / 1000))
                : 0;
            safeSend(ws, {
                type: 'joined',
                uuid,
                displayName: player.displayName,
                sessionId,
                currentQuestion: session.currentQuestion,
                state: session.state,
                durationSeconds: secondsLeft,
                timerActive: hasTimer,
                score: player.score || 0,
                answerOptions: session.answerOptions || [],
                hasAnswered: !!(session.playerAnswers && session.playerAnswers.has(uuid)),
                quizMode: session.quizMode || 'java',
                serverTime: Date.now(), // Absolute server timestamp for timer sync
                timerEndsAt: hasTimer ? session.timerEnds.getTime() : null, // Absolute end time
            });
        } else {
            safeSend(ws, { type: 'not_found', message: 'Player or session not found' });
        }
    } else if (type === 'heartbeat') {
        const player = players.get(uuid);
        const session = sessions.get(sessionId);
        if (!player || !session) {
            safeSend(ws, { type: 'not_found', message: 'Player or session not found' });
            return;
        }
        // Prevent cross-session presence injection
        if (player.sessionId !== sessionId) {
            safeSend(ws, { type: 'error', message: 'Player does not belong to this session' });
            return;
        }
        session.players.add(uuid);
        playerLastSeen.set(uuid, Date.now());
        // Send heartbeat_ack so the client knows the server is still alive
        // (critical for detecting silent connection failures on mobile)
        safeSend(ws, { type: 'heartbeat_ack' });
    } else if (type === 'answer') {
        const player = players.get(uuid);
        const session = sessions.get(sessionId);

        if (!player || !session) {
            safeSend(ws, { type: 'error', message: 'Invalid session or player' });
            return;
        }

        // Prevent cross-session answer injection
        if (player.sessionId !== sessionId) {
            safeSend(ws, { type: 'error', message: 'Player does not belong to this session' });
            return;
        }

        // Don't allow answering if question is no longer active
        if (session.state !== 'active') {
            safeSend(ws, { type: 'error', message: 'Question is no longer active' });
            return;
        }

        // Validate answer type
        if (typeof answer !== 'number' || !Number.isFinite(answer)) {
            safeSend(ws, { type: 'error', message: 'Invalid answer format' });
            return;
        }

        // Validate answer is one of the offered options (prevent junk stats)
        if (session.answerOptions && session.answerOptions.length > 0 && !session.answerOptions.includes(answer)) {
            safeSend(ws, { type: 'error', message: 'Answer is not one of the available options' });
            return;
        }

        // Don't allow duplicate answers
        if (session.playerAnswers && session.playerAnswers.has(uuid)) {
            safeSend(ws, { type: 'error', message: 'Already answered' });
            return;
        }

        // Mark as answered immediately to prevent TOCTOU race
        if (!session.playerAnswers) session.playerAnswers = new Set();
        session.playerAnswers.add(uuid);

        // Check correctness against the stored correct answer
        const isCorrect = session.correctAnswer !== undefined && answer === session.correctAnswer;

        // Record answer
        db.run(
            `INSERT INTO answers (sessionId, uuid, questionId, answer, correct, timestamp) 
             VALUES (?, ?, ?, ?, ?, datetime('now'))`,
            [sessionId, uuid, session.currentQuestion, answer, isCorrect ? 1 : 0],
            (err) => {
                if (err) {
                    console.error('Error recording answer:', err);
                    // Rollback optimistic lock on DB failure
                    session.playerAnswers.delete(uuid);
                    safeSend(ws, { type: 'error', message: 'Failed to record answer' });
                } else {

                    // Update score if correct
                    let bonus = 0;
                    if (isCorrect) {
                        player.score = (player.score || 0) + 1;
                        // Speed bonus: +1 extra point if answered within 5 seconds of countdown start
                        if (session.countdownStartedAt && (Date.now() - session.countdownStartedAt) <= 5000) {
                            bonus = 1;
                            player.score += bonus;
                        }
                        db.run('UPDATE players SET score = ? WHERE uuid = ?', [player.score, uuid]);
                    }

                    safeSend(ws, { type: 'answer_received', correct: isCorrect, score: player.score || 0, bonus });
                    // Only notify the presenter, not all players.
                    // Broadcasting player_answered to every player creates O(n²)
                    // messages per question (500 answers × 500 players = 250k messages)
                    // which melts conference WiFi.
                    safeSend(session.presenterWs, { type: 'player_answered', uuid });
                }
            }
        );
    } else {
        // Unknown message type — tell the client so they can debug
        safeSend(ws, { type: 'error', message: `Unknown message type: ${String(type).slice(0, 50)}` });
    }
}

function broadcastToSession(sessionId, message) {
    const session = sessions.get(sessionId);
    if (!session) return;

    const jsonMessage = JSON.stringify(message);
    for (const uuid of session.players) {
        const ws = wsConnections.get(uuid);
        if (ws) safeSend(ws, jsonMessage);
    }
    // Also send to presenter
    safeSend(session.presenterWs, jsonMessage);
}

/**
 * Push a full stats snapshot (leaderboard, answer distribution, player count)
 * to the presenter. Called when a player leaves/quits/is evicted so the
 * presenter's displayed data stays accurate in real time.
 */
function sendPresenterFullUpdate(sessionId) {
    const session = sessions.get(sessionId);
    if (!session || !session.presenterWs) return;

    const onlineUuids = [...session.players];

    // Fetch answer distribution for current question
    db.all(
        `SELECT answer, COUNT(*) as count FROM answers
         WHERE sessionId = ? AND questionId = ?
         GROUP BY answer ORDER BY count DESC`,
        [sessionId, session.currentQuestion],
        (err, answerRows) => {
            if (err) {
                console.error('sendPresenterFullUpdate: error fetching answers:', err);
                answerRows = [];
            }
            const answerDistribution = {};
            (answerRows || []).forEach(r => { answerDistribution[r.answer] = r.count; });

            // Fetch leaderboard (only online players)
            if (onlineUuids.length === 0) {
                safeSend(session.presenterWs, {
                    type: 'player_left',
                    playerCount: 0,
                    answeredCount: 0,
                    leaderboard: [],
                    answerDistribution,
                });
                return;
            }
            const placeholders = onlineUuids.map(() => '?').join(',');
            db.all(
                `SELECT displayName, score FROM players WHERE sessionId = ? AND uuid IN (${placeholders}) ORDER BY score DESC`,
                [sessionId, ...onlineUuids],
                (err2, leaderboard) => {
                    if (err2) {
                        console.error('sendPresenterFullUpdate: error fetching leaderboard:', err2);
                        leaderboard = [];
                    }

                    let answeredCount = 0;
                    if (session.playerAnswers) {
                        for (const uid of session.players) {
                            if (session.playerAnswers.has(uid)) answeredCount++;
                        }
                    }

                    safeSend(session.presenterWs, {
                        type: 'player_left',
                        playerCount: session.players.size,
                        answeredCount,
                        leaderboard: leaderboard || [],
                        answerDistribution,
                    });
                }
            );
        }
    );
}

// REST endpoints

// Admin authentication
app.post('/admin/auth', (req, res) => {
    const { secret } = req.body;
    if (secretMatch(secret)) {
        res.json({ authorized: true, token: 'admin-token' });
    } else {
        res.status(401).json({ error: 'Unauthorized' });
    }
});

// Create a new session (admin)
app.post('/session/create', (req, res) => {
    const secret = req.headers['x-admin-secret'];
    const { name, quizMode: reqQuizMode } = req.body;

    if (!secretMatch(secret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    // Validate session name
    const sessionName = typeof name === 'string' ? name.trim() : '';
    if (sessionName.length > 100) {
        return res.status(400).json({ error: 'Session name too long (max 100 characters)' });
    }

    // Prevent unbounded session creation (memory exhaustion)
    if (sessions.size >= MAX_SESSIONS) {
        res.set('Retry-After', '60');
        return res.status(429).json({ error: `Maximum number of sessions reached (${MAX_SESSIONS})` });
    }

    // 12-char cryptographically random session ID
    const sessionId = crypto.randomBytes(6).toString('hex').toUpperCase();
    const quizMode = reqQuizMode === 'sizes' ? 'sizes' : 'java';
    const session = {
        name: sessionName || sessionId,
        state: 'waiting',
        currentQuestion: null,
        currentQuestionIndex: 0,
        timerEnds: null,
        players: new Set(),
        quizMode,
    };

    sessions.set(sessionId, session);

    db.run(
        'INSERT INTO sessions (sessionId, name, state, currentQuestion, currentQuestionIndex, quizMode) VALUES (?, ?, ?, ?, ?, ?)',
        [sessionId, session.name, 'waiting', null, session.currentQuestionIndex, session.quizMode],
        (err) => {
            if (err) {
                console.error('Error creating session:', err);
                // Rollback in-memory state on DB failure
                sessions.delete(sessionId);
                return res.status(500).json({ error: 'Failed to create session' });
            }
            res.json({ sessionId });
        }
    );
});

// Player join
app.post('/player/join', (req, res) => {
    const { sessionId } = req.body;

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    if (session.players.size >= MAX_PLAYERS_PER_SESSION) {
        res.set('Retry-After', '30');
        return res.status(429).json({ error: 'Session is full' });
    }

    const uuid = uuidv4();
    const displayName = generateUniqueName(sessionId);

    const player = { sessionId, displayName, score: 0 };
    players.set(uuid, player);
    session.players.add(uuid);

    db.run(
        'INSERT INTO players (uuid, sessionId, displayName, score) VALUES (?, ?, ?, ?)',
        [uuid, sessionId, displayName, 0],
        (err) => {
            if (err) {
                console.error('Error adding player:', err);
                // Rollback in-memory state on DB failure
                players.delete(uuid);
                session.players.delete(uuid);
                return res.status(500).json({ error: 'Failed to join session' });
            }
            res.json({ uuid, displayName });
        }
    );
});

// Player leave
app.post('/player/leave', (req, res) => {
    const { uuid, sessionId } = req.body;

    const player = players.get(uuid);
    if (!player) {
        return res.status(404).json({ error: 'Player not found' });
    }

    // Validate the player actually belongs to this session
    if (player.sessionId !== sessionId) {
        return res.status(400).json({ error: 'Player does not belong to this session' });
    }

    players.delete(uuid);
    playerLastSeen.delete(uuid);

    const session = sessions.get(sessionId);
    if (session) {
        session.players.delete(uuid);
    }

    // Close WebSocket if open
    const existingWs = wsConnections.get(uuid);
    if (existingWs) {
        try { existingWs.close(1000, 'Player left'); } catch { /* ignore */ }
        wsConnections.delete(uuid);
    }

    db.run('DELETE FROM players WHERE uuid = ?', [uuid], (err) => {
        if (err) {
            console.error('Error removing player:', err);
        }
        // Notify presenter of the updated stats/leaderboard
        sendPresenterFullUpdate(sessionId);
        res.json({ success: true });
    });
});

// Delete all data for a player (GDPR-style self-service)
app.post('/player/delete-data', (req, res) => {
    const { uuid } = req.body;
    if (!uuid) {
        return res.status(400).json({ error: 'Missing uuid' });
    }

    const player = players.get(uuid);
    const sessionId = player ? player.sessionId : null;

    // Remove from in-memory structures
    players.delete(uuid);
    playerLastSeen.delete(uuid);
    if (sessionId) {
        const session = sessions.get(sessionId);
        if (session) {
            session.players.delete(uuid);
        }
    }
    // Close WebSocket if open
    const existingWs = wsConnections.get(uuid);
    if (existingWs) {
        try { existingWs.close(); } catch { /* ignore */ }
        wsConnections.delete(uuid);
    }

    // Atomic delete: use a transaction so answers + player are removed together
    db.run('BEGIN TRANSACTION', (beginErr) => {
        if (beginErr) {
            console.error('Error starting delete-data transaction:', beginErr);
            return res.status(500).json({ error: 'Failed to delete player data' });
        }
        db.run('DELETE FROM answers WHERE uuid = ?', [uuid], (err) => {
            if (err) {
                console.error('Error deleting answers for', uuid, err);
                db.run('ROLLBACK');
                return res.status(500).json({ error: 'Failed to delete player data' });
            }
            db.run('DELETE FROM players WHERE uuid = ?', [uuid], (err2) => {
                if (err2) {
                    console.error('Error deleting player', uuid, err2);
                    db.run('ROLLBACK');
                    return res.status(500).json({ error: 'Failed to delete player data' });
                }
                db.run('COMMIT', (commitErr) => {
                    if (commitErr) {
                        console.error('Error committing delete-data:', commitErr);
                        return res.status(500).json({ error: 'Failed to delete player data' });
                    }
                    // Notify presenter of the updated stats/leaderboard
                    if (sessionId) sendPresenterFullUpdate(sessionId);
                    res.json({ success: true });
                });
            });
        });
    });
});

// Start question (admin)
app.post('/admin/start_question', (req, res) => {
    const { secret, sessionId, questionId, correctAnswer, answerOptions } = req.body;
    const headerSecret = req.headers['x-admin-secret'];

    if (!secretMatch(secret) && !secretMatch(headerSecret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    // Validate questionId
    if (typeof questionId !== 'number' || !Number.isFinite(questionId)) {
        return res.status(400).json({ error: 'Invalid questionId' });
    }

    // Validate correctAnswer (must be a finite number for scoring to work)
    if (correctAnswer !== undefined && (typeof correctAnswer !== 'number' || !Number.isFinite(correctAnswer))) {
        return res.status(400).json({ error: 'Invalid correctAnswer (must be a number)' });
    }

    // Validate answerOptions (must be an array of numbers, max 20 entries)
    if (answerOptions !== undefined && answerOptions !== null) {
        if (!Array.isArray(answerOptions) || answerOptions.length > 20) {
            return res.status(400).json({ error: 'answerOptions must be an array of at most 20 entries' });
        }
        for (const opt of answerOptions) {
            if (typeof opt !== 'number' || !Number.isFinite(opt)) {
                return res.status(400).json({ error: 'All answerOptions must be finite numbers' });
            }
        }
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    session.state = 'active';
    session.currentQuestion = questionId;
    session.correctAnswer = correctAnswer; // Store correct answer for scoring
    session.answerOptions = answerOptions || []; // Store answer options for all players
    session.timerEnds = null; // No timer until presenter starts countdown
    session.countdownStartedAt = null; // Reset from previous question
    session.playerAnswers = new Set();

    db.run(
        'UPDATE sessions SET state = ?, currentQuestion = ?, timerEnds = NULL WHERE sessionId = ?',
        ['active', questionId, sessionId],
        (err) => {
            if (err) {
                console.error('Error starting question:', err);
                return res.status(500).json({ error: 'Failed to start question' });
            }

            broadcastToSession(sessionId, {
                type: 'question_started',
                questionId,
                answerOptions: session.answerOptions,
            });

            res.json({ success: true });
        }
    );
});

// Start countdown for current question (admin)
app.post('/admin/start_countdown', (req, res) => {
    const { sessionId, seconds } = req.body;
    const headerSecret = req.headers['x-admin-secret'];

    if (!secretMatch(headerSecret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    // Validate seconds
    const secs = Number(seconds);
    if (!Number.isFinite(secs) || secs < 1 || secs > 600) {
        return res.status(400).json({ error: 'Invalid countdown duration (must be 1-600 seconds)' });
    }

    const session = sessions.get(sessionId);
    if (!session || session.state !== 'active') {
        return res.status(404).json({ error: 'No active question' });
    }

    session.countdownStartedAt = Date.now();
    session.timerEnds = new Date(Date.now() + secs * 1000);

    db.run(
        'UPDATE sessions SET timerEnds = ? WHERE sessionId = ?',
        [session.timerEnds.toISOString(), sessionId],
        (err) => {
            if (err) {
                console.error('Error setting countdown:', err);
                return res.status(500).json({ error: 'Failed to set countdown' });
            }

            broadcastToSession(sessionId, {
                type: 'countdown_started',
                seconds: secs,
                serverTime: Date.now(),
                timerEndsAt: session.timerEnds.getTime(),
            });

            res.json({ success: true });
        }
    );
});

// Cancel countdown for current question (admin)
app.post('/admin/cancel_countdown', (req, res) => {
    const { sessionId } = req.body;
    const headerSecret = req.headers['x-admin-secret'];

    if (!secretMatch(headerSecret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session || session.state !== 'active') {
        return res.status(404).json({ error: 'No active question' });
    }

    session.timerEnds = null;

    db.run(
        'UPDATE sessions SET timerEnds = NULL WHERE sessionId = ?',
        [sessionId],
        (err) => {
            if (err) {
                console.error('Error cancelling countdown:', err);
                return res.status(500).json({ error: 'Failed to cancel countdown' });
            }

            broadcastToSession(sessionId, {
                type: 'countdown_canceled',
            });

            res.json({ success: true });
        }
    );
});
const staleSweepInterval = setInterval(() => {
    const now = Date.now();
    for (const [uuid, player] of players.entries()) {
        const session = sessions.get(player.sessionId);
        if (!session) continue;
        if (!session.players.has(uuid)) continue;

        const lastSeen = playerLastSeen.get(uuid);

        // If the player has an active WebSocket, they're fine — skip eviction.
        // This prevents false eviction when a reconnect races with the sweep.
        const activeWs = wsConnections.get(uuid);
        if (activeWs && activeWs.readyState === WebSocket.OPEN) continue;

        if (!lastSeen || now - lastSeen > HEARTBEAT_TIMEOUT_MS) {
            session.players.delete(uuid);
            if (activeWs) {
                try {
                    activeWs.close(4000, 'Heartbeat timeout');
                } catch {
                    // ignore
                }
            }
            wsConnections.delete(uuid);
            playerLastSeen.delete(uuid);
            // Notify presenter that player count / leaderboard changed
            sendPresenterFullUpdate(player.sessionId);
        }
    }

    // Clean up orphaned wsConnections — connections for UUIDs that are
    // no longer tracked in any session's player set (e.g. old presenter
    // reconnections with new IDs, or sessions that were deleted).
    for (const [uuid, ws] of wsConnections.entries()) {
        // Skip if the player is legitimately tracked
        if (players.has(uuid)) continue;
        // Check if it's a presenter connection (uuid starts with 'presenter-')
        // that belongs to an active session
        let isActivePresenter = false;
        if (uuid.startsWith('presenter-')) {
            for (const [, session] of sessions) {
                if (session.presenterWs === ws) {
                    isActivePresenter = true;
                    break;
                }
            }
        }
        if (isActivePresenter) continue;
        // Orphaned — clean up
        if (ws.readyState === WebSocket.OPEN || ws.readyState === WebSocket.CONNECTING) {
            try { ws.close(4002, 'Orphaned connection'); } catch { /* ignore */ }
        }
        wsConnections.delete(uuid);
    }
}, STALE_SWEEP_INTERVAL_MS);

// Protocol-level WebSocket ping/pong — keeps TCP connections alive through
// NAT gateways, mobile data proxies, and aggressive conference-WiFi firewalls.
// Without this, idle connections get silently dropped after 30-60s on mobile.
const wsPingInterval = setInterval(() => {
    for (const ws of wss.clients) {
        if (ws.isAlive === false) {
            ws.terminate();
            continue;
        }
        ws.isAlive = false;
        ws.ping();
    }
}, WS_PING_INTERVAL_MS);

// Stop question (admin)
app.post('/admin/stop_question', (req, res) => {
    const { secret, sessionId } = req.body;
    const headerSecret = req.headers['x-admin-secret'];

    if (!secretMatch(secret) && !secretMatch(headerSecret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    session.state = 'waiting';
    session.timerEnds = null;
    session.countdownStartedAt = null;
    session.correctAnswer = undefined;
    session.answerOptions = [];

    db.run(
        'UPDATE sessions SET state = ?, timerEnds = NULL WHERE sessionId = ?',
        ['waiting', sessionId],
        (err) => {
            if (err) {
                console.error('Error stopping question:', err);
                return res.status(500).json({ error: 'Failed to stop question' });
            }

            broadcastToSession(sessionId, {
                type: 'question_stopped',
                questionId: session.currentQuestion,
            });
            res.json({ success: true });
        }
    );
});

// Get stats (admin)
app.post('/admin/stats', (req, res) => {
    const { secret, sessionId } = req.body;
    const headerSecret = req.headers['x-admin-secret'];

    if (!secretMatch(secret) && !secretMatch(headerSecret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    // Get all answers for current question
    db.all(
        `SELECT uuid, answer, COUNT(*) as count FROM answers 
         WHERE sessionId = ? AND questionId = ? 
         GROUP BY answer ORDER BY count DESC`,
        [sessionId, session.currentQuestion],
        (err, rows) => {
            if (err) {
                console.error('Error getting stats:', err);
                return res.status(500).json({ error: 'Failed to get stats' });
            }

            // Get leaderboard (only online players)
            const onlineUuids = [...session.players];
            if (onlineUuids.length === 0) {
                const answerDistribution = {};
                rows.forEach((row) => {
                    answerDistribution[row.answer] = row.count;
                });
                return res.json({
                    answerDistribution,
                    leaderboard: [],
                    playerCount: session.players.size,
                });
            }
            const placeholders = onlineUuids.map(() => '?').join(',');
            db.all(
                `SELECT displayName, score FROM players WHERE sessionId = ? AND uuid IN (${placeholders}) ORDER BY score DESC`,
                [sessionId, ...onlineUuids],
                (err2, leaderboard) => {
                    if (err2) {
                        console.error('Error getting leaderboard:', err2);
                        return res.status(500).json({ error: 'Failed to get leaderboard' });
                    }

                    const answerDistribution = {};
                    rows.forEach((row) => {
                        answerDistribution[row.answer] = row.count;
                    });

                    res.json({
                        answerDistribution,
                        leaderboard,
                        playerCount: session.players.size,
                    });
                }
            );
        }
    );
});

// Get full session details (for presenter)
app.get('/session/:sessionId', (req, res) => {
    const { sessionId } = req.params;
    const secret = req.headers['x-admin-secret'];

    if (!secretMatch(secret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    res.json({
        sessionId,
        playerCount: session.players.size,
        state: session.state,
        currentQuestion: session.currentQuestion,
        currentQuestionIndex: session.currentQuestionIndex,
        quizMode: session.quizMode || 'java',
    });
});

// Get session info (for presenter) with player count
app.get('/session/:sessionId/info', (req, res) => {
    const { sessionId } = req.params;
    const secret = req.headers['x-admin-secret'];

    if (!secretMatch(secret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    res.json({
        sessionId,
        playerCount: session.players.size,
        state: session.state,
        currentQuestion: session.currentQuestion,
    });
});

// Get session stats (for polling player count and answered count)
app.get('/session/:sessionId/stats', (req, res) => {
    const { sessionId } = req.params;
    const secret = req.headers['x-admin-secret'];

    if (!secretMatch(secret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    // Count how many players have answered the current question
    let answeredCount = 0;
    if (session.currentQuestion) {
        for (const uuid of session.players) {
            if (session.playerAnswers && session.playerAnswers.has(uuid)) {
                answeredCount++;
            }
        }
    }

    res.json({
        playerCount: session.players.size,
        answeredCount: answeredCount,
        state: session.state,
    });
});

// Get QR code for session (public, for player sharing)
app.get('/session/:sessionId/qr-public', async (req, res) => {
    const { sessionId } = req.params;
    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    const host = req.headers.host || 'localhost:3000';
    const protocol = req.headers['x-forwarded-proto'] || req.protocol;
    const playerUrl = `${protocol}://${host}${BASE_PATH}/?session=${encodeURIComponent(sessionId)}`;

    try {
        const qrCode = await QRCode.toDataURL(playerUrl, { width: 512, margin: 1 });
        res.json({ qrCode, url: playerUrl });
    } catch (err) {
        console.error('Error generating public QR code:', err);
        res.status(500).json({ error: 'Failed to generate QR code' });
    }
});

// Get QR code as a PNG image (public, embeddable)
// Usage: <img src="/qr/SESSION_ID"> or open in browser
app.get('/qr/:sessionId', async (req, res) => {
    const { sessionId } = req.params;
    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    const host = req.headers.host || `localhost:${PORT}`;
    const protocol = req.headers['x-forwarded-proto'] || req.protocol;
    const playerUrl = `${protocol}://${host}${BASE_PATH}/?session=${encodeURIComponent(sessionId)}`;

    try {
        const pngBuffer = await QRCode.toBuffer(playerUrl, {
            type: 'png',
            width: Math.min(parseInt(req.query.size, 10) || 512, MAX_QR_SIZE),
            margin: Math.min(Math.max(parseInt(req.query.margin, 10) || 1, 0), 10),
        });
        res.type('png').send(pngBuffer);
    } catch (err) {
        console.error('Error generating QR PNG:', err);
        res.status(500).json({ error: 'Failed to generate QR code' });
    }
});

// Get QR code for session (with player count)
app.get('/session/:sessionId/qr', async (req, res) => {
    const { sessionId } = req.params;
    const secret = req.headers['x-admin-secret'];

    if (!secretMatch(secret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    const host = req.headers.host || 'localhost:3000';
    const protocol = req.headers['x-forwarded-proto'] || req.protocol;
    const playerUrl = `${protocol}://${host}${BASE_PATH}/?session=${encodeURIComponent(sessionId)}`;

    try {
        const qrCode = await QRCode.toDataURL(playerUrl, { width: 512, margin: 1 });
        res.json({
            qrCode,
            url: playerUrl,
            playerCount: session.players.size,
        });
    } catch (err) {
        console.error('Error generating QR code:', err);
        res.status(500).json({ error: 'Failed to generate QR code' });
    }
});

// List all sessions (admin)
app.get('/admin/sessions', (req, res) => {
    const secret = req.headers['x-admin-secret'];

    if (!secretMatch(secret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const sessionList = Array.from(sessions.entries()).map(([sessionId, session]) => ({
        sessionId,
        name: session.name || 'Untitled Session',
        state: session.state,
        playerCount: session.players.size,
        currentQuestion: session.currentQuestion,
        quizMode: session.quizMode || 'java',
    }));

    res.json({ sessions: sessionList });
});

// Restart session (admin)
app.post('/admin/session/restart', (req, res) => {
    const secret = req.headers['x-admin-secret'];
    const { sessionId } = req.body;

    if (!secretMatch(secret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    session.state = 'waiting';
    session.currentQuestion = null;
    session.currentQuestionIndex = 0;
    session.timerEnds = null;
    session.countdownStartedAt = null;
    session.correctAnswer = undefined;
    session.answerOptions = [];
    session.playerAnswers = new Set();

    // Reset scores for ALL players in this session (not just connected ones)
    // Disconnected players who rejoin later must also see score=0
    for (const [, p] of players.entries()) {
        if (p.sessionId === sessionId) {
            p.score = 0;
        }
    }

    db.run(
        'UPDATE sessions SET state = ?, currentQuestion = NULL, currentQuestionIndex = 0, timerEnds = NULL WHERE sessionId = ?',
        ['waiting', sessionId],
        (err) => {
            if (err) {
                console.error('Error restarting session:', err);
                return res.status(500).json({ error: 'Failed to restart session' });
            }
            db.run('DELETE FROM answers WHERE sessionId = ?', [sessionId], (deleteErr) => {
                if (deleteErr) {
                    console.error('Error clearing answers on restart:', deleteErr);
                }
                db.run('UPDATE players SET score = 0 WHERE sessionId = ?', [sessionId], (scoreErr) => {
                    if (scoreErr) {
                        console.error('Error resetting scores on restart:', scoreErr);
                    }
                    broadcastToSession(sessionId, { type: 'session_restarted' });
                    res.json({ success: true });
                });
            });
        }
    );
});

// Delete session (admin)
app.post('/admin/session/delete', (req, res) => {
    const { secret, sessionId } = req.body;

    if (!secretMatch(secret)) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (session) {
        // Clear presenter stats interval
        if (session._presenterStatsInterval) {
            clearInterval(session._presenterStatsInterval);
            session._presenterStatsInterval = null;
        }
        // Close presenter WS
        if (session.presenterWs) {
            try { session.presenterWs.close(1000, 'Session deleted'); } catch { /* ignore */ }
        }
        // Close all player WebSocket connections and clean up maps
        for (const uuid of session.players) {
            const ws = wsConnections.get(uuid);
            if (ws) {
                try { ws.close(1000, 'Session deleted'); } catch { /* ignore */ }
            }
            wsConnections.delete(uuid);
            players.delete(uuid);
            playerLastSeen.delete(uuid);
        }
    }

    sessions.delete(sessionId);

    db.serialize(() => {
        db.run('DELETE FROM answers WHERE sessionId = ?', [sessionId]);
        db.run('DELETE FROM players WHERE sessionId = ?', [sessionId]);
        db.run('DELETE FROM sessions WHERE sessionId = ?', [sessionId], (err) => {
            if (err) {
                console.error('Error deleting session:', err);
                return res.status(500).json({ error: 'Failed to delete session' });
            }
            res.json({ success: true });
        });
    });
});

// Get leaderboard (only online players)
app.get('/leaderboard/:sessionId', (req, res) => {
    const { sessionId } = req.params;
    const session = sessions.get(sessionId);
    const onlineUuids = session ? [...session.players] : [];

    if (onlineUuids.length === 0) {
        return res.json({ leaderboard: [] });
    }
    const placeholders = onlineUuids.map(() => '?').join(',');
    db.all(
        `SELECT displayName, score FROM players WHERE sessionId = ? AND uuid IN (${placeholders}) ORDER BY score DESC LIMIT 10`,
        [sessionId, ...onlineUuids],
        (err, rows) => {
            if (err) {
                console.error('Error getting leaderboard:', err);
                return res.status(500).json({ error: 'Failed to get leaderboard' });
            }
            res.json({ leaderboard: rows });
        }
    );
});

// Root redirect
app.get('/', (req, res) => {
    sendHtmlWithBasePath(res, path.join(__dirname, 'public', 'player.html'));
});

app.get('/presenter', (req, res) => {
    sendHtmlWithBasePath(res, path.join(__dirname, 'public', 'presenter.html'));
});

app.get('/presenter/view', (req, res) => {
    sendHtmlWithBasePath(res, path.join(__dirname, 'public', 'presenter.html'));
});

app.get('/presenter/view/:sessionId', (req, res) => {
    sendHtmlWithBasePath(res, path.join(__dirname, 'public', 'presenter.html'));
});

// Global error handler — prevent unhandled Express errors from crashing the server
app.use((err, req, res, _next) => {
    // Preserve status from Express body-parser errors (e.g. 413 Payload Too Large)
    const status = err.status || err.statusCode || 500;
    console.error('Unhandled Express error:', err.message || err);
    res.status(status).json({ error: status === 500 ? 'Internal server error' : err.message });
});

// HTTP upgrade to WebSocket
const server = http.createServer(app);
const wsPrefix = BASE_PATH + '/ws';
server.on('upgrade', (request, socket, head) => {
    if (request.url.startsWith(wsPrefix)) {
        // Strip BASE_PATH from the upgrade URL so the WS handler sees /ws?...
        if (BASE_PATH) request.url = request.url.slice(BASE_PATH.length);
        wss.handleUpgrade(request, socket, head, (ws) => {
            wss.emit('connection', ws, request);
        });
    } else {
        socket.destroy();
    }
});

// Graceful shutdown helper
function gracefulShutdown() {
    clearInterval(staleSweepInterval);
    clearInterval(wsPingInterval);
    for (const [, session] of sessions) {
        if (session._presenterStatsInterval) {
            clearInterval(session._presenterStatsInterval);
        }
    }
    wss.close();
    server.close();
    db.close();
}

// Handle SIGTERM for container/cloud deployments (Docker, K8s, etc.)
// Without this, the process is killed hard and DB writes may be lost.
process.on('SIGTERM', () => {
    console.log('Received SIGTERM, shutting down gracefully...');
    gracefulShutdown();
});

// Only start server when run directly (not when required by tests)
if (require.main === module) {
    server.listen(PORT, () => {
        const bp = BASE_PATH || '';
        console.log('\n╔═══════════════════════════════════════════════════════════╗');
        console.log(`║   Conference Quiz Server running on http://localhost:${PORT}${bp}`);
        console.log('╚═══════════════════════════════════════════════════════════╝');
        console.log(`\nPlayer:    http://localhost:${PORT}${bp}/`);
        console.log(`Presenter: http://localhost:${PORT}${bp}/presenter`);
        if (bp) console.log(`BASE_PATH: ${bp}`);
        console.log(`\nADMIN_SECRET is set: ${ADMIN_SECRET ? 'Yes ✓' : 'No ✗'}\n`);
    });
}

module.exports = { app, server, wss, db, sessions, players, wsConnections, playerLastSeen, gracefulShutdown, WS_RATE_LIMIT_MAX, WS_RATE_LIMIT_WINDOW_MS, MAX_SESSIONS, HEARTBEAT_TIMEOUT_MS, safeSend };
