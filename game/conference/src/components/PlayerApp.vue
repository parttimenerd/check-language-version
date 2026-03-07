<template>
    <div class="player-root">
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
                <p><strong>Session data:</strong> When you join, you are assigned a randomly generated anonymous name (e.g. "HappyPanda") and a random session ID. Both are stored in a browser cookie (8-hour expiry) solely so you can reconnect automatically if you reload the page. They are never shared with third parties.</p>
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

let ws = null;
let quizDataJava = { entries: [] };
let quizDataSizes = [];
let quizData = { entries: [] };
let quizMode = 'java';

async function loadQuizData() {
    // Load both quiz data files so the correct one can be selected
    // based on the session's quizMode from the server
    try {
        const res = await fetch('code.json');
        if (res.ok) quizDataJava = await res.json();
    } catch (e) {
        console.warn('No code.json found', e);
    }
    try {
        const res = await fetch('object-sizes.json');
        if (res.ok) {
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
        };
    },
    async mounted() {
        this.currentTheme = this.$getTheme();
        await loadQuizData();
        this.quizModeLocal = quizMode;
        this.tryResumeFromCookie();
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
                const res = await fetch(`/session/${encodeURIComponent(this.sessionId)}/qr-public`);
                if (!res.ok) return;
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
            // 8 hour expiry
            const expires = new Date(Date.now() + 8 * 60 * 60 * 1000).toUTCString();
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
                const res = await fetch('/player/delete-data', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ uuid: this.uuid }),
                });
                if (!res.ok) {
                    const err = await res.json().catch(() => ({}));
                    throw new Error(err.error || 'Server error');
                }
                // Close WebSocket
                if (ws) { try { ws.close(); } catch (_) {} ws = null; }
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
                const joinRes = await fetch('/player/join', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ sessionId }),
                });
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
        connectWebSocket({ resuming = false } = {}) {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            ws = new WebSocket(`${protocol}//${window.location.host}/ws?uuid=${this.uuid}`);
            // Reset solution flag when connecting to a new question set
            this.showingSolution = false;

            // If resuming from cookie and server doesn't know us, fall back to join
            let resumeTimeout = resuming
                ? setTimeout(() => {
                      this.clearPlayerCookie();
                      this.step = 'join';
                  }, 4000)
                : null;

            ws.onmessage = (event) => {
                const msg = JSON.parse(event.data);
                console.log('[WS] received:', msg.type, msg);
                if (msg.type === 'joined') {
                    if (resumeTimeout) { clearTimeout(resumeTimeout); resumeTimeout = null; }
                    this.score = msg.score || 0;
                    // Update quiz mode from server
                    if (msg.quizMode) {
                        selectQuizMode(msg.quizMode);
                        this.quizModeLocal = msg.quizMode;
                    }
                    // If a question is already active when we (re)connect, show it immediately
                    if (msg.state === 'active' && msg.currentQuestion != null) {
                        this.serverAnswers = msg.answerOptions || [];
                        this.receiveQuestion(msg.currentQuestion);
                        // If a countdown is already running, show remaining time
                        if (msg.durationSeconds != null && msg.durationSeconds > 0 && msg.timerActive) {
                            this.timerRemaining = msg.durationSeconds;
                            this.timerActive = true;
                            this.startTimer();
                        }
                    }
                } else if (msg.type === 'not_found') {
                    if (resumeTimeout) { clearTimeout(resumeTimeout); resumeTimeout = null; }
                    console.log('[WS] not_found – clearing cookie, back to join');
                    this.clearPlayerCookie();
                    this.step = 'join';
                } else if (msg.type === 'question_started') {
                    this.serverAnswers = msg.answerOptions || [];
                    this.receiveQuestion(msg.questionId);
                } else if (msg.type === 'countdown_started') {
                    // Presenter started a close countdown — show timer to player
                    this.timerRemaining = msg.seconds;
                    this.timerActive = true;
                    this.startTimer();
                } else if (msg.type === 'question_stopped') {
                    console.log('[WS] question_stopped');
                    this.clearTimer();
                    this.timerActive = false;
                    // Show solution to all players (answered or not)
                    this.showingSolution = true;
                } else if (msg.type === 'answer_received') {
                    this.isCorrect = msg.correct;
                    this.score = msg.score;
                    this.bonus = msg.bonus || 0;
                } else if (msg.type === 'leaderboard') {
                    this.leaderboard = msg.data || [];
                } else if (msg.type === 'next-question-in') {
                    this.resultCountdown = msg.seconds || 5;
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
            };
        },
        receiveQuestion(questionId) {
            // Look up question data from the locally loaded quiz JSON
            const entries = Array.isArray(quizData) ? quizData : (quizData.entries || []);
            const question = entries[questionId];
            console.log('[question_started] id:', questionId, 'found:', !!question);
            if (!question) {
                console.warn('[receiveQuestion] questionId', questionId, 'not found in quizData (length:', entries.length, ')');
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
            }

            this.clearTimer();
        },
        startTimer() {
            if (this.timerInterval) clearInterval(this.timerInterval);
            this.timerInterval = setInterval(() => {
                this.timerRemaining--;
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
                    await fetch('/player/delete-data', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ uuid: this.uuid }),
                    });
                } catch (_) { /* best effort */ }
            }
            if (ws) { try { ws.close(); } catch (_) {} ws = null; }
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
        doToggleTheme() {
            this.currentTheme = this.$toggleTheme();
        },
    },
    beforeUnmount() {
        this.clearTimer();
        if (ws) ws.close();
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
</style>
