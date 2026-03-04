#!/usr/bin/env node

/**
 * Conference Mode Load Tester
 *
 * Simulates multiple concurrent players joining a session and answering questions.
 * Measures performance metrics: latency, throughput, errors, and WebSocket reliability.
 *
 * Usage:
 *   node load-test.js [options]
 *
 * Options:
 *   --players <n>      Number of concurrent players (default: 10)
 *   --questions <n>    Number of questions to play (default: 5)
 *   --delay <ms>       Delay between player joins in ms (default: 50)
 *   --host <url>       Server URL (default: http://localhost:3000)
 *   --verbose          Enable verbose logging
 */

const http = require('http');
const WebSocket = require('ws');
const { v4: uuidv4 } = require('uuid');

class LoadTester {
    constructor(options = {}) {
        this.host = options.host || 'http://localhost:3000';
        this.wsHost = this.host.replace('http', 'ws');
        this.numPlayers = options.players || 10;
        this.numQuestions = options.questions || 5;
        this.joinDelay = options.delay || 50;
        this.verbose = options.verbose || false;

        this.players = [];
        this.metrics = {
            totalJoined: 0,
            totalFailed: 0,
            totalAnswered: 0,
            totalErrors: 0,
            latencies: [],
            startTime: null,
            endTime: null,
        };

        this.sessionId = null;
        this.quizData = null;
        this.currentQuestion = null;
    }

    log(msg) {
        if (this.verbose) {
            console.log(`[${new Date().toISOString()}] ${msg}`);
        }
    }

    async loadQuizData() {
        return new Promise((resolve, reject) => {
            const url = `${this.host}/code.json`;
            const startTime = Date.now();

            http.get(url, (res) => {
                let data = '';
                res.on('data', (chunk) => (data += chunk));
                res.on('end', () => {
                    const latency = Date.now() - startTime;
                    this.metrics.latencies.push({ op: 'load_quiz', latency });
                    try {
                        this.quizData = JSON.parse(data);
                        this.log(`Loaded quiz: ${this.quizData.entries.length} questions`);
                        resolve();
                    } catch (e) {
                        reject(e);
                    }
                });
            }).on('error', reject);
        });
    }

    async createSession() {
        return new Promise((resolve, reject) => {
            const postData = JSON.stringify({ secret: process.env.ADMIN_SECRET || 'test' });
            const options = {
                hostname: new URL(this.host).hostname,
                port: new URL(this.host).port || 3000,
                path: '/admin/create',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': postData.length,
                },
            };

            const startTime = Date.now();
            const req = http.request(options, (res) => {
                let data = '';
                res.on('data', (chunk) => (data += chunk));
                res.on('end', () => {
                    const latency = Date.now() - startTime;
                    this.metrics.latencies.push({ op: 'create_session', latency });
                    try {
                        const response = JSON.parse(data);
                        this.sessionId = response.sessionId;
                        this.log(`Created session: ${this.sessionId}`);
                        resolve();
                    } catch (e) {
                        reject(e);
                    }
                });
            });

            req.on('error', reject);
            req.write(postData);
            req.end();
        });
    }

    async joinPlayer(playerId) {
        return new Promise((resolve, reject) => {
            const postData = JSON.stringify({ sessionId: this.sessionId });
            const options = {
                hostname: new URL(this.host).hostname,
                port: new URL(this.host).port || 3000,
                path: '/player/join',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': postData.length,
                },
            };

            const startTime = Date.now();
            const req = http.request(options, (res) => {
                let data = '';
                res.on('data', (chunk) => (data += chunk));
                res.on('end', () => {
                    const latency = Date.now() - startTime;
                    this.metrics.latencies.push({ op: 'join', latency });
                    try {
                        const response = JSON.parse(data);
                        this.metrics.totalJoined++;
                        resolve({
                            playerId,
                            uuid: response.uuid,
                            displayName: response.displayName,
                        });
                    } catch (e) {
                        this.metrics.totalFailed++;
                        reject(e);
                    }
                });
            });

            req.on('error', () => {
                this.metrics.totalFailed++;
                reject(new Error(`Failed to join player ${playerId}`));
            });

            req.write(postData);
            req.end();
        });
    }

    connectWebSocket(player) {
        return new Promise((resolve, reject) => {
            const url = `${this.wsHost}/ws?uuid=${player.uuid}`;
            const ws = new WebSocket(url);
            let connected = false;

            const timeout = setTimeout(() => {
                if (!connected) {
                    ws.close();
                    this.metrics.totalErrors++;
                    reject(new Error(`WebSocket connection timeout for ${player.displayName}`));
                }
            }, 5000);

            ws.on('open', () => {
                connected = true;
                clearTimeout(timeout);
                this.log(`WebSocket connected: ${player.displayName}`);
                ws.send(
                    JSON.stringify({ type: 'join', sessionId: this.sessionId, uuid: player.uuid })
                );
                resolve(ws);
            });

            ws.on('error', (error) => {
                clearTimeout(timeout);
                this.metrics.totalErrors++;
                reject(error);
            });

            ws.on('close', () => {
                this.log(`WebSocket closed: ${player.displayName}`);
            });
        });
    }

    async spawnPlayer(playerId) {
        try {
            // Join game
            const player = await this.joinPlayer(playerId);
            this.log(`Player joined: ${player.displayName} (${player.uuid})`);

            // Connect WebSocket
            const ws = await this.connectWebSocket(player);

            // Store player state
            const playerState = {
                ...player,
                ws,
                answered: false,
                score: 0,
            };

            this.players.push(playerState);

            // Listen for messages
            ws.on('message', (data) => {
                try {
                    const message = JSON.parse(data);
                    this.handleMessage(playerState, message);
                } catch (e) {
                    this.log(`Error parsing message: ${e.message}`);
                }
            });
        } catch (error) {
            this.log(`Failed to spawn player ${playerId}: ${error.message}`);
        }
    }

    handleMessage(player, message) {
        if (message.type === 'question_started') {
            this.log(`Question started for ${player.displayName}: Q${message.questionId}`);
            this.currentQuestion = message.questionId;
            player.answered = false;

            // Submit answer after random delay (simulating thinking time)
            const thinkTime = Math.random() * 8000 + 1000; // 1-9 seconds
            setTimeout(() => {
                this.submitAnswer(player);
            }, thinkTime);
        } else if (message.type === 'question_stopped') {
            this.log(`Question stopped for ${player.displayName}`);
        }
    }

    submitAnswer(player) {
        if (player.answered || !player.ws || player.ws.readyState !== WebSocket.OPEN) {
            return;
        }

        player.answered = true;
        const randomAnswer = Math.floor(Math.random() * 29) - 3; // Random version -3 to 25

        const startTime = Date.now();
        player.ws.send(
            JSON.stringify({
                type: 'answer',
                sessionId: this.sessionId,
                answer: randomAnswer,
            }),
            (error) => {
                const latency = Date.now() - startTime;
                this.metrics.latencies.push({ op: 'answer', latency });

                if (error) {
                    this.metrics.totalErrors++;
                    this.log(`Error submitting answer for ${player.displayName}: ${error.message}`);
                } else {
                    this.metrics.totalAnswered++;
                    this.log(`Answer submitted: ${player.displayName} → ${randomAnswer}`);
                }
            }
        );
    }

    async startQuestion() {
        const questionId = Math.floor(Math.random() * this.quizData.entries.length);
        const postData = JSON.stringify({
            secret: process.env.ADMIN_SECRET || 'test',
            sessionId: this.sessionId,
            questionId,
            durationSeconds: 30,
        });

        return new Promise((resolve) => {
            const options = {
                hostname: new URL(this.host).hostname,
                port: new URL(this.host).port || 3000,
                path: '/admin/question/start',
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Content-Length': postData.length,
                },
            };

            const req = http.request(options, () => {
                this.log(`Question started: Q${questionId}`);
                resolve();
            });

            req.on('error', (error) => {
                this.log(`Error starting question: ${error.message}`);
                resolve();
            });

            req.write(postData);
            req.end();
        });
    }

    async run() {
        console.log('\n🎯 Conference Mode Load Tester');
        console.log('================================\n');
        console.log(`Host: ${this.host}`);
        console.log(`Players: ${this.numPlayers}`);
        console.log(`Questions: ${this.numQuestions}`);
        console.log(`Join delay: ${this.joinDelay}ms\n`);

        try {
            this.metrics.startTime = Date.now();

            // Load quiz data
            await this.loadQuizData();

            // Create session
            await this.createSession();

            // Spawn players with staggered joins
            console.log(`Spawning ${this.numPlayers} players...\n`);
            for (let i = 0; i < this.numPlayers; i++) {
                await this.spawnPlayer(i);
                await new Promise((resolve) => setTimeout(resolve, this.joinDelay));
            }

            // Wait for all players to connect
            await new Promise((resolve) => setTimeout(resolve, 2000));

            // Run questions
            console.log(`\nRunning ${this.numQuestions} questions...\n`);
            for (let q = 0; q < this.numQuestions; q++) {
                await this.startQuestion();

                // Wait for question duration + buffer
                await new Promise((resolve) => setTimeout(resolve, 35000));

                // Stop question
                await new Promise((resolve) => {
                    const postData = JSON.stringify({
                        secret: process.env.ADMIN_SECRET || 'test',
                        sessionId: this.sessionId,
                    });

                    const options = {
                        hostname: new URL(this.host).hostname,
                        port: new URL(this.host).port || 3000,
                        path: '/admin/question/stop',
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Content-Length': postData.length,
                        },
                    };

                    const req = http.request(options, () => {
                        this.log(`Question ${q + 1} stopped`);
                        resolve();
                    });

                    req.on('error', () => resolve());
                    req.write(postData);
                    req.end();
                });

                // Wait between questions
                await new Promise((resolve) => setTimeout(resolve, 2000));
            }

            // Final summary
            this.metrics.endTime = Date.now();
            this.printSummary();
        } catch (error) {
            console.error('Fatal error:', error.message);
            process.exit(1);
        }

        // Close all connections
        this.players.forEach((p) => {
            if (p.ws && p.ws.readyState === WebSocket.OPEN) {
                p.ws.close();
            }
        });

        // Exit after a delay to let connections close
        setTimeout(() => {
            process.exit(0);
        }, 2000);
    }

    printSummary() {
        const duration = (this.metrics.endTime - this.metrics.startTime) / 1000;
        const avgLatency =
            this.metrics.latencies.reduce((sum, m) => sum + m.latency, 0) /
                this.metrics.latencies.length || 0;

        console.log('\n================================');
        console.log('📊 Load Test Results');
        console.log('================================\n');

        console.log('Performance Metrics:');
        console.log(`  Duration: ${duration.toFixed(1)}s`);
        console.log(`  Total latency samples: ${this.metrics.latencies.length}`);
        console.log(`  Avg latency: ${avgLatency.toFixed(2)}ms`);

        // Latency breakdown by operation
        const opLatencies = {};
        this.metrics.latencies.forEach((m) => {
            if (!opLatencies[m.op]) opLatencies[m.op] = [];
            opLatencies[m.op].push(m.latency);
        });

        console.log('\n  Latency by operation:');
        Object.entries(opLatencies).forEach(([op, latencies]) => {
            const avg = latencies.reduce((a, b) => a + b, 0) / latencies.length;
            const min = Math.min(...latencies);
            const max = Math.max(...latencies);
            console.log(
                `    ${op}: avg=${avg.toFixed(2)}ms min=${min}ms max=${max}ms (n=${latencies.length})`
            );
        });

        console.log('\nPlayer Activity:');
        console.log(`  Joined: ${this.metrics.totalJoined}/${this.numPlayers}`);
        console.log(`  Failed: ${this.metrics.totalFailed}/${this.numPlayers}`);
        console.log(`  Answers submitted: ${this.metrics.totalAnswered}`);
        console.log(`  Errors: ${this.metrics.totalErrors}`);

        const joinRate = ((this.metrics.totalJoined / this.numPlayers) * 100).toFixed(1);
        console.log(`  Join rate: ${joinRate}%`);

        const expectedAnswers = this.metrics.totalJoined * this.numQuestions;
        const answerRate = ((this.metrics.totalAnswered / expectedAnswers) * 100).toFixed(1);
        console.log(
            `  Answer rate: ${answerRate}% (${this.metrics.totalAnswered}/${expectedAnswers})`
        );

        console.log('\nThroughput:');
        console.log(`  Players/second: ${(this.metrics.totalJoined / duration).toFixed(2)}`);
        console.log(`  Answers/second: ${(this.metrics.totalAnswered / duration).toFixed(2)}`);

        // Overall health
        const health =
            this.metrics.totalFailed === 0 && this.metrics.totalErrors === 0
                ? '✅ PASS'
                : '⚠️  DEGRADED';
        console.log(`\nOverall Health: ${health}\n`);
    }
}

// Parse command-line arguments
const args = process.argv.slice(2);
const options = {
    players: 10,
    questions: 5,
    delay: 50,
    verbose: false,
};

for (let i = 0; i < args.length; i++) {
    if (args[i] === '--players') options.players = parseInt(args[++i]);
    else if (args[i] === '--questions') options.questions = parseInt(args[++i]);
    else if (args[i] === '--delay') options.delay = parseInt(args[++i]);
    else if (args[i] === '--host') options.host = args[++i];
    else if (args[i] === '--verbose') options.verbose = true;
}

// Run load test
const tester = new LoadTester(options);
tester.run().catch((error) => {
    console.error('Load test failed:', error);
    process.exit(1);
});
