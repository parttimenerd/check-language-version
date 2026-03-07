// Conference-mode Java Version Quiz Server
// Run: npm install && ADMIN_SECRET=your_secret npm start

const express = require('express');
const fs = require('fs');
const path = require('path');
const WebSocket = require('ws');
const http = require('http');
const { v4: uuidv4 } = require('uuid');
const sqlite3 = require('sqlite3').verbose();
const QRCode = require('qrcode');

const app = express();
const PORT = process.env.PORT || 3000;
const ADMIN_SECRET = process.env.ADMIN_SECRET;

if (!ADMIN_SECRET) {
    console.error('Error: ADMIN_SECRET environment variable must be set');
    process.exit(1);
}

// Public static files
app.use(express.static('public'));
app.use(express.json());

function sendFirstExistingFile(res, filePaths) {
    for (const filePath of filePaths) {
        if (fs.existsSync(filePath)) {
            res.sendFile(filePath);
            return true;
        }
    }
    return false;
}

// Serve quiz assets with fallback paths
app.get('/code.json', (req, res) => {
    const sent = sendFirstExistingFile(res, [
        path.join(__dirname, 'public', 'code.json'),
        path.join(__dirname, '..', 'dist', 'code.json'),
    ]);
    if (!sent) res.status(404).json({ error: 'code.json not found' });
});

app.get('/object-sizes.json', (req, res) => {
    const sent = sendFirstExistingFile(res, [
        path.join(__dirname, 'public', 'object-sizes.json'),
        path.join(__dirname, '..', 'dist', 'object-sizes.json'),
    ]);
    if (!sent) res.status(404).json({ error: 'object-sizes.json not found' });
});

app.get('/descriptions.json', (req, res) => {
    const sent = sendFirstExistingFile(res, [
        path.join(__dirname, 'public', 'descriptions.json'),
        path.join(__dirname, '..', 'dist', 'descriptions.json'),
    ]);
    if (!sent) res.status(404).json({ error: 'descriptions.json not found' });
});

// In-memory session storage (persisted with SQLite for crash protection)
let sessions = new Map(); // sessionId -> { state, currentQuestion, timerEnds, players: Set<uuid> }
let players = new Map(); // uuid -> { sessionId, displayName, score }
let wsConnections = new Map(); // uuid -> WebSocket

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
            if (!colNames.has('name')) migrations.push("ALTER TABLE sessions ADD COLUMN name TEXT DEFAULT 'Untitled Session'");
            if (!colNames.has('currentQuestionIndex')) migrations.push('ALTER TABLE sessions ADD COLUMN currentQuestionIndex INTEGER DEFAULT 0');
            if (!colNames.has('quizMode')) migrations.push("ALTER TABLE sessions ADD COLUMN quizMode TEXT DEFAULT 'java'");

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
        const adjCount = Math.random() < 0.6 ? 1 : Math.random() < 0.5 ? 2 : 3;
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
    } while (existingNames.has(name) && attempts < 100);

    if (attempts >= 100) {
        // Fallback: append UUID suffix
        name = name + Math.random().toString(36).substr(2, 5);
    }

    return name;
}

// Compute answer options deterministically based on question code
function computeAnswerOptions(question, quizMode, allQuizData) {
    if (quizMode === 'sizes') {
        // Generate plausible wrong answers for sizes quiz
        const correct = question.correct;
        if (typeof correct !== 'number') return [];

        // Build pool from all quiz data correct sizes
        let pool = [];
        if (Array.isArray(allQuizData)) {
            pool = allQuizData
                .map(q => q && typeof q.correct === 'number' ? q.correct : null)
                .filter(v => typeof v === 'number');
            pool = [...new Set(pool)].filter(v => v !== correct);
        }

        // Deterministic shuffle using code hash
        const codeHash = (question.code || '').split('').reduce((h, c) => (h << 5) - h + c.charCodeAt(0), 0);
        const seed = Math.abs(codeHash);
        pool.sort((a, b) => {
            const ha = ((seed ^ (a * 2654435761)) >>> 0) % 1000000;
            const hb = ((seed ^ (b * 2654435761)) >>> 0) % 1000000;
            return ha - hb;
        });

        const wrong = pool.slice(0, 4);

        // Fill with nearby multiples of 8 if not enough
        let step = 8;
        let candidate = correct;
        while (wrong.length < 4) {
            candidate += step;
            if (candidate !== correct && !wrong.includes(candidate)) wrong.push(candidate);
            if (wrong.length >= 4) break;
            const low = correct - (wrong.length * step);
            if (low > 0 && low !== correct && !wrong.includes(low)) wrong.push(low);
        }

        return [...new Set([correct, ...wrong.slice(0, 4)])].sort((a, b) => a - b);
    }

    // For Java quiz, generate deterministic options using question code as seed
    const correct = question.correct;
    const codeHash = question.code.split('').reduce((h, c) => (h << 5) - h + c.charCodeAt(0), 0);
    const seed = Math.abs(codeHash) % 1000;

    const wrong = [];
    for (let i = 0; i < 4; i++) {
        // Deterministic generation: use seed + offset to always generate same set
        const val = (((seed + i * 123) % 25) + i) % 25;
        if (val !== correct) {
            wrong.push(val);
        }
    }

    return [...new Set([correct, ...wrong])].sort((a, b) => a - b).slice(0, 5);
}

// WebSocket handler
const wss = new WebSocket.Server({ noServer: true });

wss.on('connection', (ws, request) => {
    const uuid = request.url.split('?uuid=')[1];

    if (!uuid) {
        ws.close(1008, 'Missing uuid parameter');
        return;
    }

    wsConnections.set(uuid, ws);
    console.log(`WebSocket connected: ${uuid}`);

    ws.on('message', (message) => {
        try {
            const data = JSON.parse(message);
            handleMessage(uuid, data, ws);
        } catch (e) {
            console.error('Error parsing message:', e);
            ws.send(JSON.stringify({ type: 'error', message: 'Invalid message format' }));
        }
    });

    ws.on('close', () => {
        wsConnections.delete(uuid);
        console.log(`WebSocket disconnected: ${uuid}`);
        // Remove from session so playerCount only reflects connected players
        const disconnectedPlayer = players.get(uuid);
        if (disconnectedPlayer) {
            const session = sessions.get(disconnectedPlayer.sessionId);
            if (session) {
                session.players.delete(uuid);
            }
        }
    });

    ws.on('error', (err) => {
        console.error(`WebSocket error for ${uuid}:`, err);
    });
});

function handleMessage(uuid, data, ws) {
    const { type, sessionId, answer } = data;

    if (type === 'join-session-presenter') {
        // Presenter joining to monitor session
        const session = sessions.get(sessionId);
        if (session) {
            // Store/update presenter connection (always update for reconnects)
            session.presenterWs = ws;
            // Send initial session state
            ws.send(
                JSON.stringify({
                    type: 'session-state',
                    sessionId,
                    state: session.state,
                    currentQuestion: session.currentQuestion,
                    playerCount: session.players.size,
                })
            );
            // Start sending periodic stats updates
            const statsInterval = setInterval(() => {
                if (ws.readyState === WebSocket.OPEN && session) {
                    let answeredCount = 0;
                    if (session.currentQuestion && session.playerAnswers) {
                        for (const uuid of session.players) {
                            if (session.playerAnswers.has(uuid)) {
                                answeredCount++;
                            }
                        }
                    }
                    ws.send(
                        JSON.stringify({
                            type: 'stats-update',
                            playerCount: session.players.size,
                            answeredCount: answeredCount,
                        })
                    );
                } else {
                    clearInterval(statsInterval);
                }
            }, 1000); // Send stats every 1 second
        }
    } else if (type === 'join') {
        const player = players.get(uuid);
        const session = sessions.get(sessionId);
        if (player && session) {
            // (Re-)add player to session on connect/reconnect
            session.players.add(uuid);
            const hasTimer = session.timerEnds && new Date(session.timerEnds) > new Date();
            const secondsLeft = hasTimer
                ? Math.max(0, Math.round((new Date(session.timerEnds) - Date.now()) / 1000))
                : 0;
            ws.send(
                JSON.stringify({
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
                    quizMode: session.quizMode || 'java',
                })
            );
        } else {
            ws.send(JSON.stringify({ type: 'not_found', message: 'Player or session not found' }));
        }
    } else if (type === 'answer') {
        const player = players.get(uuid);
        const session = sessions.get(sessionId);

        if (!player || !session) {
            ws.send(JSON.stringify({ type: 'error', message: 'Invalid session or player' }));
            return;
        }

        // Don't allow answering if question is no longer active
        if (session.state !== 'active') {
            ws.send(JSON.stringify({ type: 'error', message: 'Question is no longer active' }));
            return;
        }

        // Don't allow duplicate answers
        if (session.playerAnswers && session.playerAnswers.has(uuid)) {
            ws.send(JSON.stringify({ type: 'error', message: 'Already answered' }));
            return;
        }

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
                    ws.send(JSON.stringify({ type: 'error', message: 'Failed to record answer' }));
                } else {
                    if (!session.playerAnswers) {
                        session.playerAnswers = new Set();
                    }
                    session.playerAnswers.add(uuid);

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

                    ws.send(JSON.stringify({ type: 'answer_received', correct: isCorrect, score: player.score || 0, bonus }));
                    broadcastToSession(sessionId, { type: 'player_answered', uuid });
                }
            }
        );
    }
}

function broadcastToSession(sessionId, message) {
    const session = sessions.get(sessionId);
    if (!session) return;

    const jsonMessage = JSON.stringify(message);
    for (const uuid of session.players) {
        const ws = wsConnections.get(uuid);
        if (ws && ws.readyState === WebSocket.OPEN) {
            ws.send(jsonMessage);
        }
    }
    // Also send to presenter
    if (session.presenterWs && session.presenterWs.readyState === WebSocket.OPEN) {
        session.presenterWs.send(jsonMessage);
    }
}

// REST endpoints

// Admin authentication
app.post('/admin/auth', (req, res) => {
    const { secret } = req.body;
    if (secret === ADMIN_SECRET) {
        res.json({ authorized: true, token: 'admin-token' });
    } else {
        res.status(401).json({ error: 'Unauthorized' });
    }
});

// Create new session (admin)
app.post('/admin/session/create', (req, res) => {
    const { secret } = req.body;

    if (secret !== ADMIN_SECRET) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const sessionId = uuidv4();
    const session = {
        state: 'waiting', // waiting, active, finished
        currentQuestion: 0,
        timerEnds: null,
        players: new Set(),
    };

    sessions.set(sessionId, session);
    db.run(
        'INSERT INTO sessions (sessionId, state, currentQuestion) VALUES (?, ?, ?)',
        [sessionId, 'waiting', 0],
        (err) => {
            if (err) {
                console.error('Error creating session:', err);
                return res.status(500).json({ error: 'Failed to create session' });
            }
            res.json({ sessionId });
        }
    );
});

// Create a new session (admin)
app.post('/session/create', (req, res) => {
    const secret = req.headers['x-admin-secret'];
    const { name, quizMode: reqQuizMode } = req.body;

    if (secret !== ADMIN_SECRET) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const sessionId = Math.random().toString(36).substring(2, 8).toUpperCase();
    const quizMode = reqQuizMode === 'sizes' ? 'sizes' : 'java';
    const session = {
        name: name || sessionId,
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

    players.delete(uuid);

    const session = sessions.get(sessionId);
    if (session) {
        session.players.delete(uuid);
    }

    db.run('DELETE FROM players WHERE uuid = ?', [uuid], (err) => {
        if (err) {
            console.error('Error removing player:', err);
        }
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
    if (sessionId) {
        const session = sessions.get(sessionId);
        if (session) {
            session.players.delete(uuid);
        }
    }
    // Close WebSocket if open
    const existingWs = wsConnections.get(uuid);
    if (existingWs) {
        try { existingWs.close(); } catch (_) { /* ignore */ }
        wsConnections.delete(uuid);
    }

    // Delete from database: answers first, then player record
    db.serialize(() => {
        db.run('DELETE FROM answers WHERE uuid = ?', [uuid], (err) => {
            if (err) console.error('Error deleting answers for', uuid, err);
        });
        db.run('DELETE FROM players WHERE uuid = ?', [uuid], (err) => {
            if (err) {
                console.error('Error deleting player', uuid, err);
                return res.status(500).json({ error: 'Failed to delete player data' });
            }
            res.json({ success: true });
        });
    });
});

// Start question (admin)
app.post('/admin/start_question', (req, res) => {
    const { secret, sessionId, questionId, correctAnswer, answerOptions } = req.body;
    const headerSecret = req.headers['x-admin-secret'];

    if (secret !== ADMIN_SECRET && headerSecret !== ADMIN_SECRET) {
        return res.status(401).json({ error: 'Unauthorized' });
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

    if (headerSecret !== ADMIN_SECRET) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session || session.state !== 'active') {
        return res.status(404).json({ error: 'No active question' });
    }

    session.countdownStartedAt = Date.now();
    session.timerEnds = new Date(Date.now() + seconds * 1000);

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
                seconds,
            });

            res.json({ success: true });
        }
    );
});

// Stop question (admin)
app.post('/admin/stop_question', (req, res) => {
    const { secret, sessionId } = req.body;
    const headerSecret = req.headers['x-admin-secret'];

    if (secret !== ADMIN_SECRET && headerSecret !== ADMIN_SECRET) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    session.state = 'waiting';
    session.timerEnds = null;

    db.run(
        'UPDATE sessions SET state = ?, timerEnds = NULL WHERE sessionId = ?',
        ['waiting', sessionId],
        (err) => {
            if (err) {
                console.error('Error stopping question:', err);
                return res.status(500).json({ error: 'Failed to stop question' });
            }

            broadcastToSession(sessionId, { type: 'question_stopped' });
            res.json({ success: true });
        }
    );
});

// Get stats (admin)
app.post('/admin/stats', (req, res) => {
    const { secret, sessionId } = req.body;
    const headerSecret = req.headers['x-admin-secret'];

    if (secret !== ADMIN_SECRET && headerSecret !== ADMIN_SECRET) {
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

    if (secret !== ADMIN_SECRET) {
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

    if (secret !== ADMIN_SECRET) {
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

    if (secret !== ADMIN_SECRET) {
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
    const playerUrl = `${protocol}://${host}/?session=${encodeURIComponent(sessionId)}`;

    try {
        const qrCode = await QRCode.toDataURL(playerUrl, { width: 512, margin: 1 });
        res.json({ qrCode, url: playerUrl });
    } catch (err) {
        console.error('Error generating public QR code:', err);
        res.status(500).json({ error: 'Failed to generate QR code' });
    }
});

// Get QR code for session (with player count)
app.get('/session/:sessionId/qr', async (req, res) => {
    const { sessionId } = req.params;
    const secret = req.headers['x-admin-secret'];

    if (secret !== ADMIN_SECRET) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (!session) {
        return res.status(404).json({ error: 'Session not found' });
    }

    const host = req.headers.host || 'localhost:3000';
    const protocol = req.headers['x-forwarded-proto'] || req.protocol;
    const playerUrl = `${protocol}://${host}/?session=${encodeURIComponent(sessionId)}`;

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

    if (secret !== ADMIN_SECRET) {
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

    if (secret !== ADMIN_SECRET) {
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
    session.playerAnswers = new Set();

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
                broadcastToSession(sessionId, { type: 'session_restarted' });
                res.json({ success: true });
            });
        }
    );
});

// Delete session (admin)
app.post('/admin/session/delete', (req, res) => {
    const { secret, sessionId } = req.body;

    if (secret !== ADMIN_SECRET) {
        return res.status(401).json({ error: 'Unauthorized' });
    }

    const session = sessions.get(sessionId);
    if (session) {
        // Close all WebSocket connections for this session
        for (const uuid of session.players) {
            const ws = wsConnections.get(uuid);
            if (ws) {
                ws.close(1000, 'Session deleted');
            }
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
    res.sendFile(path.join(__dirname, 'public', 'player.html'));
});

app.get('/presenter', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'presenter.html'));
});

app.get('/presenter/view', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'presenter.html'));
});

app.get('/presenter/view/:sessionId', (req, res) => {
    res.sendFile(path.join(__dirname, 'public', 'presenter.html'));
});

// HTTP upgrade to WebSocket
const server = http.createServer(app);
server.on('upgrade', (request, socket, head) => {
    if (request.url.startsWith('/ws')) {
        wss.handleUpgrade(request, socket, head, (ws) => {
            wss.emit('connection', ws, request);
        });
    } else {
        socket.destroy();
    }
});

server.listen(PORT, () => {
    console.log('\n╔═══════════════════════════════════════════════════════════╗');
    console.log(`║   Conference Quiz Server running on http://localhost:${PORT}`);
    console.log('╚═══════════════════════════════════════════════════════════╝');
    console.log(`\nPlayer:    http://localhost:${PORT}`);
    console.log(`Presenter: http://localhost:${PORT}/presenter`);
    console.log(`\nADMIN_SECRET is set: ${ADMIN_SECRET ? 'Yes ✓' : 'No ✗'}\n`);
});
