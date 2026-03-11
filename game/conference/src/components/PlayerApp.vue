<template>
    <div class="player-root">
        <div v-if="connectionLost" class="connection-lost-banner">
            ⚠️ Connection lost — reconnecting…
        </div>
        <h1>
            {{ quizModeLocal === 'sizes' ? 'Guess the Object Size' : 'Guess the Java Version' }}
            <span v-if="qrCode" class="qr-toggle" @click="showQrFullscreen = true">📱 QR</span>
        </h1>

        <!-- Prominent user name badge (shown when in game) -->
        <div v-if="step !== 'join' && displayName" class="player-name-banner">
            <span class="player-name-icon">👤</span>
            <span class="player-name-text">{{ displayName }}</span>
            <span v-if="score > 0" class="player-name-score">⭐ {{ score }}</span>
        </div>

        <JoinGame v-if="step === 'join'" @join="handleJoin" />
        <WaitingScreen
            v-else-if="step === 'waiting'"
            :display-name="displayName"
            :session-id="sessionId"
        />
        <div v-else-if="step === 'question' && currentQuestion && !showingSolution && !hasAnswered">
            <Question
                :question="currentQuestion"
                :timer-remaining="timerRemaining"
                :has-answered="hasAnswered"
                :is-correct="isCorrect"
                :selected-answer="selectedAnswer"
                :quiz-mode="quizModeLocal"
                :server-answers="serverAnswers"
                @answer="handleAnswer"
            />
        </div>
        <div v-else-if="step === 'question' && currentQuestion && hasAnswered && !showingSolution" class="waiting-container">
            <div class="code-container">
                <pre class="language-java"><code class="language-java" v-html="highlightedCode"></code></pre>
            </div>
            <div class="waiting-message">
                ⏳ Waiting for presenter to show the answer...
            </div>
        </div>
        <Solution
            v-else-if="step === 'question' && currentQuestion && showingSolution"
            :question="currentQuestion"
            :is-correct="isCorrect"
            :selected-answer="selectedAnswer"
            :quiz-mode="quizModeLocal"
            :score="score"
            :bonus="bonus"
        />

        <!-- Fullscreen QR Code Overlay -->
        <div v-if="showQrFullscreen && qrCode" class="qr-fullscreen-backdrop" @click.self="showQrFullscreen = false" role="dialog" aria-modal="true">
            <button class="qr-fullscreen-close" @click="showQrFullscreen = false" aria-label="Close">&#x2715;</button>
            <div class="qr-fullscreen-content" @click="showQrFullscreen = false">
                <img :src="qrCode" alt="Join QR Code" class="qr-fullscreen-img" />
                <p class="qr-fullscreen-label">Scan to join!</p>
                <p class="qr-fullscreen-hint">Tap anywhere to close</p>
            </div>
        </div>

        <!-- Footer -->
        <footer class="site-footer">
            <div v-if="step !== 'join'" class="quit-row">
                <a href="#" class="quit-link" @click.prevent="quitGame">🚪 Quit Game</a>
            </div>
            This is just a fun quiz and may contain errors. &nbsp;&nbsp;
            Created by <a href="https://mostlynerdless.de" target="_blank" rel="noopener">Johannes Bechberger</a>
            from the <a href="https://sapmachine.io" target="_blank" rel="noopener">SapMachine</a> team.
            &nbsp;&nbsp;
            <a href="https://github.com/parttimenerd/check-language-version/tree/main/game" target="_blank" rel="noopener">GitHub</a>
            &nbsp;&nbsp;
            <a href="#" @click.prevent="showPrivacy = true">Privacy</a>
            &nbsp;&nbsp;
            <a href="#" class="theme-toggle" @click.prevent="doToggleTheme">{{ currentTheme === 'dark' ? '☀️' : '🌙' }}</a>
        </footer>

        <!-- Privacy modal -->
        <div v-if="showPrivacy" class="privacy-backdrop" @click.self="showPrivacy = false" role="dialog" aria-modal="true">
            <div class="privacy-modal">
                <button class="privacy-close" @click="showPrivacy = false" aria-label="Close">&#x2715;</button>
                <h2>&#128274; Privacy Notice</h2>
                <p>This quiz runs entirely in your browser. <strong>No personal data is collected or transmitted.</strong></p>
                <p><strong>Session data:</strong> When you join, you are assigned a randomly generated anonymous name (e.g. "HappyPanda") and a random session ID. Both are stored in a browser cookie (24-hour expiry) solely so you can reconnect automatically if you reload the page. They are never shared with third parties.</p>
                <p><strong>Server logs:</strong> Standard access logs (IP, timestamp, path) may be retained per the hosting provider's policy. No quiz answers are stored after the session ends.</p>
                <p><strong>External requests:</strong> All assets load from the same host. No third-party tracking scripts are used.</p>
                <hr class="privacy-divider">
                <p><strong>Delete my data:</strong> This will remove your player record, all your quiz answers from the database, and clear the browser cookie. <em>Standard server access logs (IP address, timestamps) retained by the hosting provider cannot be deleted here.</em></p>
                <button
                    class="privacy-delete-btn"
                    :disabled="deletingData"
                    @click="deleteMyData"
                >{{ deletingData ? 'Deleting…' : '&#128465; Delete All My Data' }}</button>
                <p v-if="deleteDataMsg" :class="deleteDataMsgIsError ? 'privacy-delete-error' : 'privacy-delete-ok'">{{ deleteDataMsg }}</p>
                <p style="margin-bottom:0;">Questions? <a href="https://github.com/parttimenerd/check-language-version" target="_blank" rel="noopener">GitHub</a>.</p>
            </div>
        </div>
    </div>
</template>

<script>
import JoinGame from './JoinGame.vue';
import WaitingScreen from './WaitingScreen.vue';
import Question from './Question.vue';
import Solution from './Solution.vue';
import Prism from 'prismjs';
import 'prismjs/components/prism-java';
import { apiUrl, wsUrl } from '../basePath.js';

let ws = null;
let heartbeatInterval = null;
let reconnectTimer = null;
let reconnectAttempts = 0;
let pendingAnswer = null;
let missedHeartbeats = 0;
const MAX_MISSED_HEARTBEATS = 3; // Force reconnect after 3 missed acks (~30s)
let quizDataJava = { entries: [] };
let quizDataSizes = [];
let quizData = { entries: [] };
let quizMode = 'java';
// Server-client clock offset (ms) for accurate timer sync
let serverClockOffset = 0;
// Guard against concurrent loadQuizData() calls — if a load is already in
// flight (e.g. mounted() + receiveQuestion() retry racing), reuse the
// existing promise instead of firing a duplicate fetch on stressed WiFi.
let _loadQuizDataPromise = null;

/**
 * Fetch wrapper with automatic retry for critical operations.
 * Uses exponential backoff with jitter — essential for conference WiFi
 * where requests randomly fail but succeed moments later.
 */
async function fetchWithRetry(url, options = {}, { retries = 2, baseDelay = 500, timeout = 15000 } = {}) {
    for (let attempt = 0; attempt <= retries; attempt++) {
        // Per-attempt timeout via AbortController — prevents hung requests
        // on conference WiFi where TCP connects but the response never arrives.
        const controller = new AbortController();
        const timer = timeout > 0 ? setTimeout(() => controller.abort(), timeout) : null;
        try {
            const res = await fetch(url, { ...options, signal: controller.signal });
            if (timer) clearTimeout(timer);
            // Don't retry 4xx errors (client mistakes, not network issues)
            if (!res.ok && res.status >= 400 && res.status < 500) return res;
            if (res.ok) return res;
            // 5xx — server glitch, worth retrying
            if (attempt === retries) return res;
        } catch (err) {
            if (timer) clearTimeout(timer);
            if (attempt === retries) throw err;
        }
        // Exponential backoff with jitter to avoid thundering herd
        const delay = baseDelay * Math.pow(2, attempt) * (0.5 + Math.random() * 0.5);
        await new Promise(r => setTimeout(r, delay));
    }
}

/** Save/restore pending answer across page refreshes on mobile */
function savePendingAnswer(answer) {
    pendingAnswer = answer;
    if (answer) {
        try { sessionStorage.setItem('pendingAnswer', JSON.stringify(answer)); } catch { /* quota */ }
    } else {
        try { sessionStorage.removeItem('pendingAnswer'); } catch { /* ignore */ }
    }
}
function loadPendingAnswer() {
    if (pendingAnswer) return pendingAnswer;
    try {
        const stored = sessionStorage.getItem('pendingAnswer');
        if (stored) { pendingAnswer = JSON.parse(stored); return pendingAnswer; }
    } catch { /* ignore */ }
    return null;
}

async function loadQuizData() {
    // Dedup: if a load is already in flight, piggyback on it
    if (_loadQuizDataPromise) return _loadQuizDataPromise;
    _loadQuizDataPromise = _doLoadQuizData();
    try { await _loadQuizDataPromise; } finally { _loadQuizDataPromise = null; }
}

async function _doLoadQuizData() {
    // Load both quiz data files so the correct one can be selected
    // based on the session's quizMode from the server.
    // Uses retry wrapper — critical for conference WiFi where the first
    // fetch after page load often fails on mobile devices.
    try {
        const res = await fetchWithRetry(apiUrl('/code.json'), {}, { retries: 3 });
        if (res && res.ok) quizDataJava = await res.json();
    } catch (e) {
        console.warn('No code.json found', e);
    }
    try {
        const res = await fetchWithRetry(apiUrl('/object-sizes.json'), {}, { retries: 3 });
        if (res && res.ok) {
            const raw = await res.json();
            quizDataSizes = preprocessSizesData(raw);
        }
    } catch (e) {
        console.warn('No object-sizes.json found', e);
    }
    // Default to java; will be overridden when server sends quizMode
    quizData = quizDataJava;
    quizMode = 'java';
}

/**
 * Simple 32-bit FNV-1a hash (matches original game).
 */
function fnv1a32(str) {
    let h = 0x811c9dc5;
    for (let i = 0; i < str.length; i++) {
        h ^= str.charCodeAt(i);
        h = (h + ((h << 1) + (h << 4) + (h << 7) + (h << 8) + (h << 24))) >>> 0;
    }
    return h >>> 0;
}

/**
 * Preprocess raw object-sizes.json entries into a quiz-ready format
 * with `correct` (totalSize), `code`, and `useCompactHeaders` fields.
 * Deterministically chooses between compact and non-compact variants
 * per question (matching the original game logic).
 */
function preprocessSizesData(raw) {
    if (!Array.isArray(raw)) return [];
    return raw.map(e => {
        if (!e) return null;
        const variants = Array.isArray(e.layout) ? e.layout : [];
        const nonCompact = variants.find(v => v && v.UseCompactObjectHeaders === false);
        const compact = variants.find(v => v && v.UseCompactObjectHeaders === true);
        // Deterministic choice between compact/non-compact per question
        const questionKey = (e['class'] || '') + '|' + ((e.sanitizedCode || e.code || '').trim());
        const preferCompact = (fnv1a32('|' + questionKey) & 1) === 1;
        const chosen = (preferCompact ? (compact || nonCompact) : (nonCompact || compact)) || variants[0] || null;
        const totalSize = chosen && typeof chosen.totalSize === 'number' ? chosen.totalSize : null;
        if (typeof totalSize !== 'number') return null;
        return {
            kind: 'sizes',
            code: (e.sanitizedCode || e.code || '').trim(),
            correct: totalSize,
            useCompactHeaders: chosen.UseCompactObjectHeaders === true,
        };
    }).filter(Boolean);
}

function selectQuizMode(mode) {
    quizMode = mode;
    if (mode === 'sizes') {
        quizData = quizDataSizes;
    } else {
        quizData = quizDataJava;
    }
}

export default {
    components: {
        JoinGame,
        WaitingScreen,
        Question,
        Solution,
    },
    data() {
        return {
            step: 'join', // join | waiting | question
            sessionId: '',
            uuid: '',
            displayName: '',
            currentQuestion: null,
            hasAnswered: false,
            isCorrect: false,
            selectedAnswer: null,
            timerRemaining: 0,
            timerInterval: null,
            timerActive: false,
            timerEndsAt: null, // Absolute timestamp (ms) for drift-free countdown
            quizModeLocal: quizMode,
            serverAnswers: [],
            showPrivacy: false,
            deletingData: false,
            deleteDataMsg: '',
            deleteDataMsgIsError: false,
            showingSolution: false,
            score: 0,
            qrCode: '',
            showQrFullscreen: false,
            currentTheme: 'light',
            connectionLost: false,
            bonus: 0,
        };
    },
    async mounted() {
        this.currentTheme = this.$getTheme();
        await loadQuizData();
        this.quizModeLocal = quizMode;
        // Restore any pending answer from a previous page load (mobile refresh while answering)
        loadPendingAnswer();
        this.tryResumeFromCookie();

        // Page Visibility API — when the user switches back to this tab
        // (common on mobile: lock screen, switch apps, open camera, etc.),
        // immediately check if WS is still alive and reconnect if needed.
        // Without this, players stare at "Connection lost" until the next
        // exponential-backoff timer fires (up to 30 seconds).
        this._visibilityHandler = () => {
            if (document.visibilityState === 'visible' && this.uuid && this.sessionId && this.step !== 'join') {
                // Re-sync timer from stored absolute end time (interval was likely throttled by OS)
                if (this.timerEndsAt && this.timerActive) {
                    const remaining = Math.max(0, Math.round((this.timerEndsAt - Date.now() + serverClockOffset) / 1000));
                    this.timerRemaining = remaining;
                    if (remaining <= 0) {
                        this.clearTimer();
                        if (!this.hasAnswered) {
                            this.hasAnswered = true;
                        }
                    }
                }
                if (!ws || ws.readyState !== WebSocket.OPEN) {
                    this.cancelReconnect();
                    this.connectWebSocket({ resuming: true, autoReconnect: true });
                }
            }
        };
        document.addEventListener('visibilitychange', this._visibilityHandler);

        // Online/Offline events — mobile devices fire these when WiFi drops/returns.
        // Reconnect immediately on 'online' instead of waiting for backoff timer.
        this._onlineHandler = () => {
            if (this.uuid && this.sessionId && this.step !== 'join') {
                if (!ws || ws.readyState !== WebSocket.OPEN) {
                    console.log('[network] online event — reconnecting immediately');
                    this.cancelReconnect();
                    // Small random delay (0-500ms) to avoid thundering herd when
                    // conference WiFi comes back and 500 phones fire 'online' at once
                    setTimeout(() => {
                        this.connectWebSocket({ resuming: true, autoReconnect: true });
                    }, Math.random() * 500);
                }
            }
        };
        this._offlineHandler = () => {
            if (this.uuid && this.sessionId && this.step !== 'join') {
                this.connectionLost = true;
                console.log('[network] offline event — will reconnect when back online');
            }
        };
        window.addEventListener('online', this._onlineHandler);
        window.addEventListener('offline', this._offlineHandler);
    },
    computed: {
        highlightedCode() {
            if (!this.currentQuestion) return '';
            try {
                return Prism.highlight(this.currentQuestion.code, Prism.languages.java, 'java');
            } catch {
                return this.currentQuestion.code;
            }
        },
    },
    methods: {
        // ── QR code ───────────────────────────────────────────────────────
        async fetchQrCode() {
            if (!this.sessionId) return;
            try {
                const res = await fetchWithRetry(apiUrl(`/session/${encodeURIComponent(this.sessionId)}/qr-public`));
                if (!res || !res.ok) return;
                const data = await res.json();
                this.qrCode = data.qrCode || '';
            } catch (e) {
                console.error('Failed to fetch QR code', e);
            }
        },
        // ── Cookie helpers ─────────────────────────────────────────────────
        savePlayerCookie() {
            const value = JSON.stringify({
                uuid: this.uuid,
                displayName: this.displayName,
                sessionId: this.sessionId,
            });
            // 24 hour expiry
            const expires = new Date(Date.now() + 24 * 60 * 60 * 1000).toUTCString();
            document.cookie = `player_session=${encodeURIComponent(value)}; expires=${expires}; path=/; SameSite=Lax`;
        },
        clearPlayerCookie() {
            document.cookie = 'player_session=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/; SameSite=Lax';
        },
        async deleteMyData() {
            if (!this.uuid) {
                // No active session – just clear cookies
                this.clearPlayerCookie();
                this.deleteDataMsg = 'Cookies cleared. No server data was associated with this browser.';
                this.deleteDataMsgIsError = false;
                return;
            }
            this.deletingData = true;
            this.deleteDataMsg = '';
            try {
                const res = await fetchWithRetry(apiUrl('/player/delete-data'), {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ uuid: this.uuid }),
                });
                if (!res || !res.ok) {
                    const err = await res.json().catch(() => ({}));
                    throw new Error(err.error || 'Server error');
                }
                // Close WebSocket
                if (ws) { try { ws.close(); } catch (_) {} ws = null; }
                if (heartbeatInterval) {
                    clearInterval(heartbeatInterval);
                    heartbeatInterval = null;
                }
                this.cancelReconnect();
                savePendingAnswer(null);
                // Clear all local state
                this.clearPlayerCookie();
                this.uuid = '';
                this.displayName = '';
                this.sessionId = '';
                this.currentQuestion = null;
                this.hasAnswered = false;
                this.selectedAnswer = null;
                this.score = 0;
                this.step = 'join';
                this.deleteDataMsg = 'All your data has been deleted. Note: standard server access logs (IP, timestamps) may still be retained by the hosting provider.';
                this.deleteDataMsgIsError = false;
            } catch (e) {
                this.deleteDataMsg = 'Failed to delete data: ' + e.message;
                this.deleteDataMsgIsError = true;
            } finally {
                this.deletingData = false;
            }
        },
        tryResumeFromCookie() {
            const match = document.cookie.match(/(?:^|;\s*)player_session=([^;]*)/);
            if (!match) return;
            try {
                const saved = JSON.parse(decodeURIComponent(match[1]));
                if (saved.uuid && saved.displayName && saved.sessionId) {
                    this.uuid = saved.uuid;
                    this.displayName = saved.displayName;
                    this.sessionId = saved.sessionId;
                    this.step = 'waiting';
                    this.connectWebSocket({ resuming: true });
                    this.fetchQrCode();
                }
            } catch (e) {
                this.clearPlayerCookie();
            }
        },
        // ── Join ──────────────────────────────────────────────────────────
        async handleJoin(sessionId) {
            this.sessionId = sessionId;
            try {
                const joinRes = await fetchWithRetry(apiUrl('/player/join'), {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ sessionId }),
                }, { retries: 2 });
                if (!joinRes.ok) {
                    const err = await joinRes.json().catch(() => ({}));
                    alert(err.error || 'Failed to join session');
                    return;
                }
                const data = await joinRes.json();
                this.uuid = data.uuid;
                this.displayName = data.displayName;
                this.step = 'waiting';
                this.savePlayerCookie();
                this.connectWebSocket();
                this.fetchQrCode();
            } catch (e) {
                alert('Failed to join: ' + e.message);
            }
        },
        connectWebSocket({ resuming = false, autoReconnect = false } = {}) {
            // Close stale WS before opening a new one. Nulling onclose
            // prevents the old socket's close event from triggering a
            // competing reconnect (common on flaky conference WiFi).
            if (ws) {
                try { ws.onclose = null; ws.close(); } catch (_) { /* ignore */ }
                ws = null;
            }
            try {
                ws = new WebSocket(wsUrl(`/ws?uuid=${this.uuid}`));
            } catch (err) {
                // WebSocket constructor can throw on mobile when networking is denied
                console.error('[WS] constructor threw:', err);
                if (this.uuid && this.sessionId && this.step !== 'join') {
                    this.connectionLost = true;
                    this.scheduleReconnect();
                }
                return;
            }
            // Reset solution flag when connecting to a new question set
            this.showingSolution = false;

            // If resuming from cookie (not auto-reconnect) and server doesn't know us, fall back to join
            // Give the server up to 10s to respond on slow conference WiFi
            // (4s was too aggressive — round-trips regularly take 5-8s on
            // congested conference networks with hundreds of devices).
            let resumeTimeout = (resuming && !autoReconnect)
                ? setTimeout(() => {
                      this.clearPlayerCookie();
                      this.step = 'join';
                  }, 10000)
                : null;

            ws.onmessage = (event) => {
                let msg;
                try {
                    msg = JSON.parse(event.data);
                } catch (parseErr) {
                    console.error('[WS] failed to parse message:', parseErr, event.data);
                    return;
                }
                console.log('[WS] received:', msg.type, msg);
                if (msg.type === 'joined') {
                    if (resumeTimeout) { clearTimeout(resumeTimeout); resumeTimeout = null; }
                    reconnectAttempts = 0;
                    this.connectionLost = false;
                    this.score = msg.score || 0;
                    // Calibrate server-client clock offset for accurate timer sync
                    if (msg.serverTime) {
                        serverClockOffset = msg.serverTime - Date.now();
                    }
                    // Update quiz mode from server
                    if (msg.quizMode) {
                        selectQuizMode(msg.quizMode);
                        this.quizModeLocal = msg.quizMode;
                    }
                    // If a question is already active when we (re)connect, show it immediately
                    if (msg.state === 'active' && msg.currentQuestion != null) {
                        this.serverAnswers = msg.answerOptions || [];
                        this.receiveQuestion(msg.currentQuestion);
                        // If a countdown is already running, use absolute server time for accuracy
                        if (msg.timerEndsAt && msg.timerActive) {
                            this.timerEndsAt = msg.timerEndsAt;
                            const remaining = Math.max(0, Math.round((msg.timerEndsAt - Date.now() + serverClockOffset) / 1000));
                            this.timerRemaining = remaining;
                            this.timerActive = true;
                            if (remaining > 0) {
                                this.startTimer();
                            } else {
                                // Timer already expired — lock out if not answered
                                if (!this.hasAnswered && !msg.hasAnswered) {
                                    this.hasAnswered = true;
                                }
                            }
                        } else if (msg.durationSeconds != null && msg.durationSeconds > 0 && msg.timerActive) {
                            // Fallback for older servers without timerEndsAt
                            this.timerRemaining = msg.durationSeconds;
                            this.timerActive = true;
                            this.startTimer();
                        }
                        this.hasAnswered = !!msg.hasAnswered;
                    }
                    // Flush answer that was queued during disconnect (from memory or sessionStorage)
                    const pending = loadPendingAnswer();
                    if (pending && !msg.hasAnswered && ws && ws.readyState === WebSocket.OPEN) {
                        // Mark as answered BEFORE sending so the answer buttons are
                        // disabled immediately. Without this, there's a race window
                        // where the player sees enabled buttons and could tap a
                        // different answer before the server's answer_received arrives.
                        this.hasAnswered = true;
                        this.selectedAnswer = pending.answer;
                        ws.send(JSON.stringify({ type: 'answer', sessionId: pending.sessionId, answer: pending.answer }));
                    }
                    savePendingAnswer(null);
                } else if (msg.type === 'not_found') {
                    if (resumeTimeout) { clearTimeout(resumeTimeout); resumeTimeout = null; }
                    console.log('[WS] not_found – clearing cookie, back to join');
                    this.clearPlayerCookie();
                    this.step = 'join';
                } else if (msg.type === 'question_started') {
                    this.serverAnswers = msg.answerOptions || [];
                    this.receiveQuestion(msg.questionId);
                } else if (msg.type === 'countdown_started') {
                    // Presenter started a close countdown — use absolute server time for accuracy
                    if (msg.timerEndsAt) {
                        this.timerEndsAt = msg.timerEndsAt;
                        // Calibrate clock offset from this message too
                        if (msg.serverTime) serverClockOffset = msg.serverTime - Date.now();
                        const remaining = Math.max(0, Math.round((msg.timerEndsAt - Date.now() + serverClockOffset) / 1000));
                        this.timerRemaining = remaining;
                    } else {
                        this.timerRemaining = msg.seconds;
                        this.timerEndsAt = Date.now() + msg.seconds * 1000;
                    }
                    this.timerActive = true;
                    this.startTimer();
                } else if (msg.type === 'countdown_canceled') {
                    this.clearTimer();
                    this.timerRemaining = 0;
                    this.timerActive = false;
                    this.timerEndsAt = null;
                } else if (msg.type === 'question_stopped') {
                    console.log('[WS] question_stopped');
                    this.clearTimer();
                    this.timerActive = false;
                    this.timerEndsAt = null;
                    // Clear pending answer — question is closed, server won't accept it
                    savePendingAnswer(null);
                    // Show solution to all players (answered or not)
                    this.showingSolution = true;
                } else if (msg.type === 'session_restarted') {
                    // Admin restarted the session — reset all quiz state
                    console.log('[WS] session_restarted');
                    this.clearTimer();
                    this.timerActive = false;
                    this.timerEndsAt = null;
                    this.currentQuestion = null;
                    this.hasAnswered = false;
                    this.selectedAnswer = null;
                    this.isCorrect = false;
                    this.showingSolution = false;
                    this.score = 0;
                    this.bonus = 0;
                    this.serverAnswers = [];
                    this.step = 'waiting';
                    savePendingAnswer(null);
                } else if (msg.type === 'answer_received') {
                    this.isCorrect = msg.correct;
                    this.score = msg.score;
                    this.bonus = msg.bonus || 0;
                    // Server confirmed the answer — clear the pending queue
                    savePendingAnswer(null);
                } else if (msg.type === 'leaderboard') {
                    this.leaderboard = msg.data || [];
                } else if (msg.type === 'next-question-in') {
                    this.resultCountdown = msg.seconds || 5;
                } else if (msg.type === 'heartbeat_ack') {
                    // Server confirmed it received our heartbeat — connection is healthy
                    missedHeartbeats = 0;
                    this.connectionLost = false;
                }
            };

            ws.onopen = () => {
                ws.send(
                    JSON.stringify({
                        type: 'join',
                        sessionId: this.sessionId,
                        uuid: this.uuid,
                    })
                );
                if (heartbeatInterval) {
                    clearInterval(heartbeatInterval);
                }
                missedHeartbeats = 0;
                heartbeatInterval = setInterval(() => {
                    // When the tab is backgrounded (phone locked, app switched),
                    // mobile browsers throttle intervals (e.g. 1/min on Android Chrome)
                    // but also throttle onmessage, so heartbeat_ack never resets the
                    // counter. After 3 throttled fires the client would force-close the
                    // WS and start a reconnect loop in the background — wasting battery
                    // and server resources. Skip heartbeats when hidden; the server's
                    // 60s grace period covers the gap, and the visibilitychange handler
                    // reconnects instantly when the user returns.
                    if (document.hidden) return;
                    if (ws && ws.readyState === WebSocket.OPEN) {
                        missedHeartbeats++;
                        if (missedHeartbeats >= MAX_MISSED_HEARTBEATS) {
                            // Server hasn't acked multiple heartbeats — connection
                            // is likely dead (common on mobile with spotty WiFi).
                            // Force close to trigger reconnect immediately.
                            console.warn('[WS] missed', missedHeartbeats, 'heartbeat acks, forcing reconnect');
                            try { ws.close(); } catch (_) {}
                            return;
                        }
                        ws.send(
                            JSON.stringify({
                                type: 'heartbeat',
                                sessionId: this.sessionId,
                                uuid: this.uuid,
                            })
                        );
                    }
                }, 10000);
            };

            ws.onclose = (event) => {
                if (heartbeatInterval) {
                    clearInterval(heartbeatInterval);
                    heartbeatInterval = null;
                }

                // Check close code to decide whether to reconnect or give up.
                // Terminal close reasons mean the server intentionally kicked us
                // and reconnecting would just waste cycles on conference WiFi.
                const reason = event.reason || '';
                const code = event.code;
                const isTerminal =
                    // Session deleted by admin — no point reconnecting
                    (code === 1000 && reason === 'Session deleted') ||
                    // Player explicitly left via another tab/API
                    (code === 1000 && reason === 'Player left') ||
                    // Another tab/device opened for same UUID — this tab lost
                    code === 4001 ||
                    // Server considers this connection orphaned
                    code === 4002;

                if (isTerminal) {
                    console.log(`[WS] terminal close (code=${code}, reason=${reason}) — returning to join`);
                    this.clearPlayerCookie();
                    savePendingAnswer(null);
                    this.cancelReconnect();
                    this.connectionLost = false;
                    this.step = 'join';
                    return;
                }

                // Auto-reconnect if still in a game
                if (this.uuid && this.sessionId && this.step !== 'join') {
                    this.connectionLost = true;
                    this.scheduleReconnect();
                }
            };

            ws.onerror = () => {
                // onclose fires after onerror; reconnect is handled there
            };
        },
        receiveQuestion(questionId) {
            // Look up question data from the locally loaded quiz JSON
            const entries = Array.isArray(quizData) ? quizData : (quizData.entries || []);
            const question = entries[questionId];
            console.log('[question_started] id:', questionId, 'found:', !!question);
            if (!question) {
                console.warn('[receiveQuestion] questionId', questionId, 'not found in quizData (length:', entries.length, ')');
                // Quiz data may have failed to load on spotty WiFi — retry now
                // so the player can see the question once it loads
                if (entries.length === 0) {
                    loadQuizData().then(() => {
                        if (this.quizModeLocal) selectQuizMode(this.quizModeLocal);
                        const retryEntries = Array.isArray(quizData) ? quizData : (quizData.entries || []);
                        const retryQuestion = retryEntries[questionId];
                        if (retryQuestion && !(retryQuestion.correct !== undefined && retryQuestion.correct < 0)) {
                            this.currentQuestion = retryQuestion;
                            this.hasAnswered = false;
                            this.selectedAnswer = null;
                            this.isCorrect = false;
                            this.bonus = 0;
                            this.showingSolution = false;
                            this.timerRemaining = 0;
                            this.timerActive = false;
                            this.timerEndsAt = null;
                            this.step = 'question';
                        }
                    });
                }
                return;
            }
            // Skip alpha questions (correct < 0)
            if (question.correct !== undefined && question.correct < 0) {
                console.log('[receiveQuestion] skipping alpha question', questionId);
                return;
            }
            this.currentQuestion = question;
            this.hasAnswered = false;
            this.selectedAnswer = null;
            this.isCorrect = false;
            this.bonus = 0;
            this.showingSolution = false;
            this.timerRemaining = 0;
            this.timerActive = false;
            this.timerEndsAt = null;
            this.step = 'question';
        },
        formatVersion(v) {
            if (v === -3) return '1.0-α1';
            if (v === -2) return '1.0-α2';
            if (v === -1) return '1.0-α3';
            if (v === 0) return '1.0';
            if (v > 0 && v <= 4) return '1.' + v;
            return String(v);
        },
        handleAnswer(answerValue) {
            if (this.hasAnswered) return;
            this.hasAnswered = true;
            this.selectedAnswer = answerValue;
            // isCorrect will be set by the server's answer_received message

            if (ws && ws.readyState === WebSocket.OPEN) {
                ws.send(
                    JSON.stringify({
                        type: 'answer',
                        sessionId: this.sessionId,
                        answer: answerValue,
                    })
                );
                // Also persist in case the WS send appears to succeed but
                // the packet is lost (common on flaky conference WiFi)
                savePendingAnswer({ sessionId: this.sessionId, answer: answerValue });
            } else {
                // Queue answer to send on reconnect — persisted to sessionStorage
                // so it survives page refreshes on mobile
                savePendingAnswer({ sessionId: this.sessionId, answer: answerValue });
            }

            this.clearTimer();
        },
        startTimer() {
            if (this.timerInterval) clearInterval(this.timerInterval);
            this.timerInterval = setInterval(() => {
                // Use absolute end time to prevent drift (mobile browsers throttle intervals)
                if (this.timerEndsAt) {
                    this.timerRemaining = Math.max(0, Math.round((this.timerEndsAt - Date.now() + serverClockOffset) / 1000));
                } else {
                    this.timerRemaining--;
                }
                if (this.timerRemaining <= 0) {
                    this.clearTimer();
                    // Auto-close: if player hasn't answered, lock them out
                    if (!this.hasAnswered) {
                        this.hasAnswered = true;
                        // selectedAnswer stays null → Solution will show "You didn't answer"
                    }
                }
            }, 1000);
        },
        clearTimer() {
            if (this.timerInterval) {
                clearInterval(this.timerInterval);
                this.timerInterval = null;
            }
        },
        async quitGame() {
            if (!confirm('Quit the game? This will delete all your data.')) return;
            if (this.uuid) {
                try {
                    await fetchWithRetry(apiUrl('/player/delete-data'), {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ uuid: this.uuid }),
                    });
                } catch (_) { /* best effort */ }
            }
            if (ws) { try { ws.close(); } catch (_) {} ws = null; }
            if (heartbeatInterval) {
                clearInterval(heartbeatInterval);
                heartbeatInterval = null;
            }
            this.cancelReconnect();
            savePendingAnswer(null);
            this.clearPlayerCookie();
            this.clearTimer();
            this.uuid = '';
            this.displayName = '';
            this.sessionId = '';
            this.currentQuestion = null;
            this.hasAnswered = false;
            this.selectedAnswer = null;
            this.score = 0;
            this.qrCode = '';
            this.step = 'join';
        },
        scheduleReconnect() {
            if (reconnectTimer) return;
            // Exponential backoff with jitter: prevents thundering herd when
            // conference WiFi recovers and 500 phones reconnect simultaneously.
            // Jitter spreads reconnects over a random window (50-100% of delay).
            const baseDelay = Math.min(1000 * Math.pow(2, reconnectAttempts), 30000);
            const jitter = baseDelay * (0.5 + Math.random() * 0.5);
            reconnectAttempts++;
            reconnectTimer = setTimeout(() => {
                reconnectTimer = null;
                if (this.uuid && this.sessionId && this.step !== 'join') {
                    this.connectWebSocket({ resuming: true, autoReconnect: true });
                }
            }, jitter);
        },
        cancelReconnect() {
            if (reconnectTimer) {
                clearTimeout(reconnectTimer);
                reconnectTimer = null;
            }
            reconnectAttempts = 0;
            // NOTE: Do NOT clear pendingAnswer here! This method is called from
            // online/visibility handlers right before reconnecting. The pending
            // answer must survive so it gets flushed on the 'joined' message.
            // Pending answers are cleared explicitly in quitGame, deleteMyData,
            // and when the server confirms receipt (answer_received/question_stopped).
            this.connectionLost = false;
        },
        doToggleTheme() {
            this.currentTheme = this.$toggleTheme();
        },
    },
    beforeUnmount() {
        this.clearTimer();
        this.cancelReconnect();
        // Clear identity BEFORE closing WS so the onclose handler's guard
        // condition (this.uuid && this.sessionId && this.step !== 'join')
        // prevents it from scheduling a reconnect after we've cleaned up.
        const savedUuid = this.uuid;
        this.uuid = '';
        this.sessionId = '';
        if (ws) ws.close();
        // Restore uuid in case the component is kept alive (unlikely but safe)
        this.uuid = savedUuid;
        if (heartbeatInterval) {
            clearInterval(heartbeatInterval);
            heartbeatInterval = null;
        }
        if (this._visibilityHandler) {
            document.removeEventListener('visibilitychange', this._visibilityHandler);
        }
        if (this._onlineHandler) {
            window.removeEventListener('online', this._onlineHandler);
        }
        if (this._offlineHandler) {
            window.removeEventListener('offline', this._offlineHandler);
        }
    },
};
</script>

<style scoped>
.player-root {
    display: flex;
    flex-direction: column;
    min-height: calc(100vh - 48px); /* account for body padding */
}
.site-footer {
    margin-top: auto;
    padding: 10px 16px;
    font-size: 0.78em;
    color: var(--text-muted);
    text-align: center;
    border-top: 1px solid var(--border-separator);
}
.site-footer a { color: var(--accent); text-decoration: none; }
.site-footer a:hover { text-decoration: underline; }
.theme-toggle {
    font-size: 1.2em;
    cursor: pointer;
    user-select: none;
}

.privacy-backdrop {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
}
.privacy-modal {
    background: var(--bg-app);
    border-radius: 8px;
    padding: 28px 32px;
    max-width: 480px;
    width: calc(100% - 32px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
    position: relative;
    font-size: 0.92em;
    line-height: 1.6;
    color: var(--text-primary);
}
.privacy-modal h2 { margin: 0 0 14px 0; font-size: 1.15em; }
.privacy-modal p  { margin: 0 0 10px 0; color: var(--text-body); }
.privacy-modal a  { color: var(--accent); text-decoration: none; }
.privacy-modal a:hover { text-decoration: underline; }
.privacy-close {
    position: absolute;
    top: 12px;
    right: 14px;
    background: none;
    border: none;
    font-size: 1.3em;
    cursor: pointer;
    color: var(--text-secondary);
    line-height: 1;
    padding: 2px 6px;
}
.privacy-close:hover { color: var(--text-primary); }

.privacy-divider {
    margin: 16px 0;
    border: none;
    border-top: 1px solid var(--border-separator);
}

.privacy-delete-btn {
    display: inline-block;
    padding: 8px 18px;
    background: var(--danger);
    color: #fff;
    border: none;
    border-radius: 5px;
    font-size: 0.92em;
    cursor: pointer;
    margin-bottom: 10px;
    transition: background 0.15s;
}
.privacy-delete-btn:hover:not(:disabled) { background: #b02a37; }
.privacy-delete-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.privacy-delete-ok  { color: var(--success); font-size: 0.88em; margin-top: 6px; }
.privacy-delete-error { color: var(--danger); font-size: 0.88em; margin-top: 6px; }

.waiting-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.code-container {
    background: var(--bg-section);
    border-radius: 8px;
    padding: 12px 16px;
    margin: 16px 0;
    overflow-x: auto;
    font-size: 0.9em;
    line-height: 1.4;
}

.code-container pre {
    margin: 0;
    font-family: 'Courier New', monospace;
}

.waiting-message {
    text-align: center;
    padding: 32px 16px;
    font-size: 1.1em;
    color: var(--text-secondary);
    background: var(--bg-section);
    border-radius: 8px;
    border-left: 4px solid var(--accent);
}

.score-display {
    text-align: center;
    padding: 16px;
    font-size: 1.15em;
    color: var(--text-primary);
    background: var(--bg-section);
    border-radius: 8px;
    border-left: 4px solid var(--warning);
}

/* ── Prominent player name banner ─────────────────────────── */
.player-name-banner {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    background: linear-gradient(135deg, var(--accent) 0%, var(--accent-hover) 100%);
    color: #fff;
    padding: 10px 16px;
    border-radius: 8px;
    margin-bottom: 16px;
    font-size: 1.1em;
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(0, 123, 255, 0.25);
}
.player-name-icon {
    font-size: 1.2em;
}
.player-name-text {
    letter-spacing: 0.02em;
}
.player-name-score {
    margin-left: auto;
    background: rgba(255, 255, 255, 0.2);
    padding: 2px 10px;
    border-radius: 12px;
    font-size: 0.9em;
}

/* ── QR toggle button in header ──────────────────────────── */
.qr-toggle {
    font-size: 0.5em;
    cursor: pointer;
    margin-left: 12px;
    padding: 2px 10px;
    border-radius: 6px;
    background: var(--bg-badge);
    vertical-align: middle;
    user-select: none;
}
.qr-toggle:hover {
    background: var(--border-separator);
}

/* ── Fullscreen QR code overlay ──────────────────────────── */
.qr-fullscreen-backdrop {
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.88);
    z-index: 2000;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
}
.qr-fullscreen-close {
    position: absolute;
    top: 16px;
    right: 20px;
    background: rgba(255, 255, 255, 0.15);
    border: none;
    color: #fff;
    font-size: 1.8em;
    cursor: pointer;
    padding: 4px 12px;
    border-radius: 8px;
    line-height: 1;
    z-index: 2001;
    transition: background 0.15s;
}
.qr-fullscreen-close:hover {
    background: rgba(255, 255, 255, 0.3);
}
.qr-fullscreen-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
    padding: 24px;
}
.qr-fullscreen-img {
    width: min(80vw, 80vh, 500px);
    height: min(80vw, 80vh, 500px);
    border-radius: 16px;
    background: #fff;
    padding: 16px;
    box-sizing: border-box;
}
.qr-fullscreen-label {
    color: #fff;
    font-size: 1.6em;
    font-weight: 600;
    margin: 0;
}
.qr-fullscreen-hint {
    color: rgba(255, 255, 255, 0.5);
    font-size: 0.9em;
    margin: 0;
}

/* ── Small phone improvements ────────────────────────────── */
@media (max-width: 400px) {
    .player-name-banner {
        font-size: 0.95em;
        padding: 8px 12px;
        gap: 6px;
    }
    .qr-fullscreen-img {
        width: min(90vw, 90vh, 400px);
        height: min(90vw, 90vh, 400px);
        padding: 12px;
        border-radius: 12px;
    }
    .qr-fullscreen-label {
        font-size: 1.2em;
    }
}

.quit-row {
    margin-bottom: 8px;
}
.quit-link {
    color: var(--danger);
    text-decoration: none;
    font-size: 0.9em;
    cursor: pointer;
}
.quit-link:hover {
    text-decoration: underline;
}

.connection-lost-banner {
    background: var(--warning, #f0ad4e);
    color: #fff;
    text-align: center;
    padding: 8px 16px;
    border-radius: 8px;
    margin-bottom: 12px;
    font-weight: 600;
    font-size: 0.92em;
    animation: pulse-banner 2s ease-in-out infinite;
}
@keyframes pulse-banner {
    0%, 100% { opacity: 1; }
    50% { opacity: 0.7; }
}
</style>
