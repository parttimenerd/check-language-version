<template>
    <div>
        <h1>
            {{ quizModeLocal === 'sizes' ? 'Guess the Object Size' : 'Guess the Java Version' }}
            <span v-if="qrCode" class="qr-toggle" @click="showQr = !showQr">📱 QR</span>
        </h1>
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
        />

        <!-- Footer -->
        <footer class="site-footer">
            <div v-if="qrCode && showQr" class="qr-share">
                <img :src="qrCode" alt="Join QR Code" class="qr-share-img" />
                <span class="qr-share-label">Scan to join!</span>
            </div>
            This is just a fun quiz and may contain errors. &nbsp;&nbsp;
            Created by <a href="https://mostlynerdless.de" target="_blank" rel="noopener">Johannes Bechberger</a>
            from the <a href="https://sapmachine.io" target="_blank" rel="noopener">SapMachine</a> team.
            &nbsp;&nbsp;
            <a href="https://github.com/parttimenerd/check-language-version/tree/main/game" target="_blank" rel="noopener">GitHub</a>
            &nbsp;&nbsp;
            <a href="#" @click.prevent="showPrivacy = true">Privacy</a>
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
let quizData = { entries: [] };
let quizMode = 'java';

async function loadQuizData() {
    try {
        const res = await fetch('code.json');
        quizData = await res.json();
        quizMode = 'java';
    } catch (e1) {
        try {
            const res = await fetch('object-sizes.json');
            quizData = await res.json();
            quizMode = 'sizes';
        } catch (e2) {
            console.error('Failed to load quiz data', e1, e2);
            quizData = { entries: [] };
        }
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
            showingSolution: false,
            score: 0,
            qrCode: '',
            showQr: false,
        };
    },
    async mounted() {
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
    },
    beforeUnmount() {
        this.clearTimer();
        if (ws) ws.close();
    },
};
</script>

<style scoped>
.site-footer {
    margin-top: 24px;
    padding: 10px 16px;
    font-size: 0.78em;
    color: #6c757d;
    text-align: center;
    border-top: 1px solid #dee2e6;
}
.site-footer a { color: #007bff; text-decoration: none; }
.site-footer a:hover { text-decoration: underline; }

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
    background: #fff;
    border-radius: 8px;
    padding: 28px 32px;
    max-width: 480px;
    width: calc(100% - 32px);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
    position: relative;
    font-size: 0.92em;
    line-height: 1.6;
    color: #333;
}
.privacy-modal h2 { margin: 0 0 14px 0; font-size: 1.15em; }
.privacy-modal p  { margin: 0 0 10px 0; color: #444; }
.privacy-modal a  { color: #007bff; text-decoration: none; }
.privacy-modal a:hover { text-decoration: underline; }
.privacy-close {
    position: absolute;
    top: 12px;
    right: 14px;
    background: none;
    border: none;
    font-size: 1.3em;
    cursor: pointer;
    color: #555;
    line-height: 1;
    padding: 2px 6px;
}
.privacy-close:hover { color: #000; }

.waiting-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.code-container {
    background: #f5f5f5;
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
    color: #666;
    background: #f8f9fa;
    border-radius: 8px;
    border-left: 4px solid #007bff;
}

.score-display {
    text-align: center;
    padding: 16px;
    font-size: 1.15em;
    color: #333;
    background: #fff8e1;
    border-radius: 8px;
    border-left: 4px solid #ffc107;
}

.qr-share {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 10px;
}

.qr-share-img {
    width: 100px;
    height: 100px;
    border-radius: 6px;
}

.qr-share-label {
    font-size: 0.82em;
    color: #555;
    margin-top: 4px;
}

.qr-toggle {
    font-size: 0.5em;
    cursor: pointer;
    margin-left: 12px;
    padding: 2px 10px;
    border-radius: 6px;
    background: #e9ecef;
    vertical-align: middle;
    user-select: none;
}
.qr-toggle:hover {
    background: #dee2e6;
}
</style>
