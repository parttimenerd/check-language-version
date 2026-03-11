#!/usr/bin/env node
// WebSocket Stress Tester for the Java Version Quiz Game
// Usage: node stress-test.js [options]
//
// Examples:
//   node stress-test.js --url http://localhost:3003 --session my-session --users 200
//   node stress-test.js -u http://localhost:3003 -s test -n 50 --answer-delay 2000

const WebSocket = require('ws');

// ── CLI argument parsing ──────────────────────────────────────────────────────
function parseArgs() {
    const args = process.argv.slice(2);
    const opts = {
        url: 'http://localhost:3003',
        session: '',
        users: 200,
        rampUp: 50,       // ms between each user joining
        answerDelay: 1500, // ms delay before answering (simulates thinking)
        answerRate: 0.85,  // fraction of users that answer (rest stay silent)
        heartbeatInterval: 10000,
        dropHeartbeatRate: 0.1,
        heartbeatDropAfter: 30000,
        reloadRate: 0.15,
        reloadDelay: 2500,
        verbose: false,
    };

    for (let i = 0; i < args.length; i++) {
        switch (args[i]) {
        case '--url':
        case '-u':
            opts.url = args[++i];
            break;
        case '--session':
        case '-s':
            opts.session = args[++i];
            break;
        case '--users':
        case '-n':
            opts.users = parseInt(args[++i], 10);
            break;
        case '--ramp-up':
        case '-r':
            opts.rampUp = parseInt(args[++i], 10);
            break;
        case '--answer-delay':
        case '-d':
            opts.answerDelay = parseInt(args[++i], 10);
            break;
        case '--answer-rate':
            opts.answerRate = parseFloat(args[++i]);
            break;
        case '--heartbeat-interval':
            opts.heartbeatInterval = parseInt(args[++i], 10);
            break;
        case '--drop-heartbeat-rate':
            opts.dropHeartbeatRate = parseFloat(args[++i]);
            break;
        case '--heartbeat-drop-after':
            opts.heartbeatDropAfter = parseInt(args[++i], 10);
            break;
        case '--reload-rate':
            opts.reloadRate = parseFloat(args[++i]);
            break;
        case '--reload-delay':
            opts.reloadDelay = parseInt(args[++i], 10);
            break;
        case '--verbose':
        case '-v':
            opts.verbose = true;
            break;
        case '--help':
        case '-h':
            console.log(`
WebSocket Stress Tester for Java Version Quiz

Usage: node stress-test.js [options]

Options:
  --url, -u <url>           Server URL (default: http://localhost:3003)
  --session, -s <name>      Session ID to join (required)
  --users, -n <count>       Number of simulated users (default: 200)
  --ramp-up, -r <ms>        Delay between each user joining (default: 50ms)
  --answer-delay, -d <ms>   Thinking time before answering (default: 1500ms)
  --answer-rate <0-1>       Fraction of users that answer questions (default: 0.85)
    --heartbeat-interval <ms> Heartbeat interval for players (default: 10000ms)
    --drop-heartbeat-rate <0-1> Fraction of players that stop heartbeats (default: 0.1)
    --heartbeat-drop-after <ms> Delay before selected players stop heartbeats (default: 30000ms)
    --reload-rate <0-1>       Fraction of players that simulate page reload (default: 0.15)
    --reload-delay <ms>       Delay before reconnect after simulated reload (default: 2500ms)
  --verbose, -v             Show per-user messages
  --help, -h                Show this help
`);
            process.exit(0);
        // falls through
        default:
            console.error(`Unknown option: ${args[i]}`);
            process.exit(1);
        }
    }

    if (!opts.session) {
        console.error('Error: --session / -s is required');
        process.exit(1);
    }

    return opts;
}

// ── Stats tracker ─────────────────────────────────────────────────────────────
const stats = {
    joined: 0,
    joinFailed: 0,
    wsConnected: 0,
    wsFailed: 0,
    wsJoined: 0,
    questionsReceived: 0,
    answersSent: 0,
    answersCorrect: 0,
    answersWrong: 0,
    heartbeatsSent: 0,
    countdownCanceled: 0,
    resumedActiveQuestion: 0,
    reconnected: 0,
    errors: 0,
    disconnected: 0,
};

let startTime;

function printStats() {
    const elapsed = ((Date.now() - startTime) / 1000).toFixed(1);
    const line = [
        `[${elapsed}s]`,
        `HTTP joined: ${stats.joined}/${opts.users}`,
        `WS: ${stats.wsConnected} conn / ${stats.wsJoined} joined`,
        `Q: ${stats.questionsReceived}`,
        `A: ${stats.answersSent} (✓${stats.answersCorrect} ✗${stats.answersWrong})`,
        `HB: ${stats.heartbeatsSent}`,
        `cancel: ${stats.countdownCanceled}`,
        `rejoin: ${stats.reconnected}`,
        `err: ${stats.errors}`,
        `dc: ${stats.disconnected}`,
    ].join(' | ');
    process.stdout.write('\r' + line + '   ');
}

// ── Simulated player ──────────────────────────────────────────────────────────
class SimPlayer {
    constructor(index, opts) {
        this.index = index;
        this.opts = opts;
        this.uuid = null;
        this.displayName = null;
        this.ws = null;
        this.sessionId = opts.session;
        this.willAnswer = Math.random() < opts.answerRate;
        this.dropHeartbeat = Math.random() < opts.dropHeartbeatRate;
        this.willReload = Math.random() < opts.reloadRate;
        this.heartbeatTimer = null;
        this.hasReloaded = false;
        this.pendingReconnectTimer = null;
    }

    log(...args) {
        if (this.opts.verbose) {
            console.log(`[Player ${this.index}]`, ...args);
        }
    }

    async join() {
        const res = await fetch(`${this.opts.url}/player/join`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sessionId: this.sessionId }),
        });

        if (!res.ok) {
            stats.joinFailed++;
            const err = await res.text();
            this.log('Join failed:', res.status, err);
            return false;
        }

        const data = await res.json();
        this.uuid = data.uuid;
        this.displayName = data.displayName;
        stats.joined++;
        this.log(`Joined as ${this.displayName} (${this.uuid})`);
        return true;
    }

    connect() {
        return new Promise((resolve) => {
            const wsUrl = this.opts.url.replace(/^http/, 'ws') + `/ws?uuid=${this.uuid}`;
            this.ws = new WebSocket(wsUrl);

            const timeout = setTimeout(() => {
                stats.wsFailed++;
                this.log('WS connect timeout');
                resolve(false);
            }, 10000);

            this.ws.on('open', () => {
                clearTimeout(timeout);
                stats.wsConnected++;
                this.log('WS connected');

                // Send join message
                this.ws.send(JSON.stringify({
                    type: 'join',
                    sessionId: this.sessionId,
                    uuid: this.uuid,
                }));
            });

            this.ws.on('message', (raw) => {
                try {
                    const msg = JSON.parse(raw.toString());
                    this.handleMessage(msg);

                    // Resolve on first 'joined' or 'not_found'
                    if (msg.type === 'joined') {
                        stats.wsJoined++;
                        this.log('WS joined session');
                        if (msg.state === 'active' && msg.currentQuestion != null) {
                            stats.resumedActiveQuestion++;
                        }
                        this.startHeartbeat();
                        this.scheduleHeartbeatDrop();
                        this.scheduleReload();
                        resolve(true);
                    } else if (msg.type === 'not_found') {
                        clearTimeout(timeout);
                        stats.wsFailed++;
                        this.log('WS not_found');
                        resolve(false);
                    }
                } catch {
                    stats.errors++;
                }
            });

            this.ws.on('error', (err) => {
                clearTimeout(timeout);
                stats.wsFailed++;
                this.log('WS error:', err.message);
                resolve(false);
            });

            this.ws.on('close', () => {
                stats.disconnected++;
                this.stopHeartbeat();
            });
        });
    }

    startHeartbeat() {
        this.stopHeartbeat();
        this.heartbeatTimer = setInterval(() => {
            if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;
            this.ws.send(
                JSON.stringify({
                    type: 'heartbeat',
                    sessionId: this.sessionId,
                    uuid: this.uuid,
                })
            );
            stats.heartbeatsSent++;
        }, this.opts.heartbeatInterval);
    }

    stopHeartbeat() {
        if (this.heartbeatTimer) {
            clearInterval(this.heartbeatTimer);
            this.heartbeatTimer = null;
        }
    }

    scheduleHeartbeatDrop() {
        if (!this.dropHeartbeat || this.hasReloaded) return;
        setTimeout(() => {
            this.stopHeartbeat();
            this.log('Stopped heartbeat to test stale timeout');
        }, this.opts.heartbeatDropAfter);
    }

    scheduleReload() {
        if (!this.willReload || this.hasReloaded) return;
        this.hasReloaded = true;
        setTimeout(async () => {
            if (this.ws && this.ws.readyState === WebSocket.OPEN) {
                this.log('Simulating reload: closing WS');
                this.ws.close();
            }
            this.pendingReconnectTimer = setTimeout(async () => {
                const ok = await this.connect();
                if (ok) {
                    stats.reconnected++;
                    this.log('Reconnected after simulated reload');
                }
            }, this.opts.reloadDelay);
        }, Math.max(500, this.opts.answerDelay));
    }

    handleMessage(msg) {
        switch (msg.type) {
        case 'question_started':
            stats.questionsReceived++;
            this.log('Question started:', msg.questionId, 'options:', msg.answerOptions);
            if (this.willAnswer) {
                this.scheduleAnswer(msg.answerOptions || []);
            }
            break;

        case 'countdown_started':
            this.log('Countdown:', msg.seconds, 'seconds');
            break;

        case 'countdown_canceled':
            stats.countdownCanceled++;
            this.log('Countdown canceled');
            break;

        case 'question_stopped':
            this.log('Question stopped (solution shown)');
            break;

        case 'answer_received':
            if (msg.correct) {
                stats.answersCorrect++;
            } else {
                stats.answersWrong++;
            }
            this.log('Answer result:', msg.correct ? '✓' : '✗', 'score:', msg.score);
            break;

        case 'error':
            stats.errors++;
            this.log('Server error:', msg.message);
            break;

        case 'joined':
            // Already handled in connect()
            break;

        default:
            this.log('Message:', msg.type);
        }
    }

    scheduleAnswer(answerOptions) {
        // Add jitter: answerDelay ± 50%
        const jitter = this.opts.answerDelay * (0.5 + Math.random());
        setTimeout(() => {
            if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;

            // Pick a random answer from the provided options, or guess a version number
            let answer;
            if (answerOptions.length > 0) {
                answer = answerOptions[Math.floor(Math.random() * answerOptions.length)];
            } else {
                answer = Math.floor(Math.random() * 23); // fallback: random Java version 0-22
            }

            this.ws.send(JSON.stringify({
                type: 'answer',
                sessionId: this.sessionId,
                answer,
            }));
            stats.answersSent++;
            this.log('Answered:', answer);
        }, jitter);
    }

    close() {
        this.stopHeartbeat();
        if (this.pendingReconnectTimer) {
            clearTimeout(this.pendingReconnectTimer);
            this.pendingReconnectTimer = null;
        }
        if (this.ws) {
            this.ws.close();
            this.ws = null;
        }
    }
}

// ── Main ──────────────────────────────────────────────────────────────────────
const opts = parseArgs();

async function sleep(ms) {
    return new Promise((r) => setTimeout(r, ms));
}

async function main() {
    console.log('\nStress Test Configuration:');
    console.log(`  Server:       ${opts.url}`);
    console.log(`  Session:      ${opts.session}`);
    console.log(`  Users:        ${opts.users}`);
    console.log(`  Ramp-up:      ${opts.rampUp}ms between joins`);
    console.log(`  Answer delay: ${opts.answerDelay}ms (±50% jitter)`);
    console.log(`  Answer rate:  ${(opts.answerRate * 100).toFixed(0)}%`);
    console.log(`  Heartbeat:    every ${opts.heartbeatInterval}ms`);
    console.log(`  Drop HB:      ${(opts.dropHeartbeatRate * 100).toFixed(0)}% after ${opts.heartbeatDropAfter}ms`);
    console.log(`  Reload sim:   ${(opts.reloadRate * 100).toFixed(0)}% reconnect after ${opts.reloadDelay}ms`);
    console.log('');

    startTime = Date.now();
    const players = [];

    // Stats display interval
    const statsInterval = setInterval(printStats, 500);

    // Phase 1: Join all users via HTTP + WS with ramp-up
    console.log(`Phase 1: Joining ${opts.users} users...\n`);

    const joinPromises = [];
    for (let i = 0; i < opts.users; i++) {
        const player = new SimPlayer(i + 1, opts);
        players.push(player);

        joinPromises.push(
            (async () => {
                const joined = await player.join();
                if (joined) {
                    await player.connect();
                }
            })()
        );

        // Ramp-up delay between user joins
        if (opts.rampUp > 0 && i < opts.users - 1) {
            await sleep(opts.rampUp);
        }
    }

    // Wait for all join+connect to finish
    await Promise.all(joinPromises);

    console.log('\n\nPhase 1 complete. All users joined.');
    printStats();
    console.log('\n');
    console.log('Phase 2: Waiting for questions (Ctrl+C to stop)...\n');

    // Phase 2: Stay connected, answer questions as they come
    // Keep running until Ctrl+C
    process.on('SIGINT', () => {
        console.log('\n\nShutting down...');
        clearInterval(statsInterval);

        // Close all connections
        for (const p of players) {
            p.close();
        }

        // Final stats
        console.log('\n── Final Stats ──────────────────────────');
        console.log(`  Users joined (HTTP):   ${stats.joined} / ${opts.users}`);
        console.log(`  Join failures:         ${stats.joinFailed}`);
        console.log(`  WS connected:          ${stats.wsConnected}`);
        console.log(`  WS joined session:     ${stats.wsJoined}`);
        console.log(`  WS failures:           ${stats.wsFailed}`);
        console.log(`  Questions received:     ${stats.questionsReceived}`);
        console.log(`  Answers sent:          ${stats.answersSent}`);
        console.log(`  Correct answers:       ${stats.answersCorrect}`);
        console.log(`  Wrong answers:         ${stats.answersWrong}`);
        console.log(`  Heartbeats sent:       ${stats.heartbeatsSent}`);
        console.log(`  Countdown canceled:    ${stats.countdownCanceled}`);
        console.log(`  Resumed active q:      ${stats.resumedActiveQuestion}`);
        console.log(`  Reconnected:           ${stats.reconnected}`);
        console.log(`  Errors:                ${stats.errors}`);
        console.log(`  Disconnections:        ${stats.disconnected}`);
        console.log(`  Total time:            ${((Date.now() - startTime) / 1000).toFixed(1)}s`);
        console.log('─────────────────────────────────────────\n');
        process.exit(0);
    });
}

main().catch((err) => {
    console.error('Fatal error:', err);
    process.exit(1);
});
