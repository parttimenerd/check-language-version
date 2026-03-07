<template>
    <div id="presenter-view-game">
        <h1>
            <template v-if="countdown !== null">
                Guess in {{ countdown }} seconds
                <button @click="cancelCountdown" class="countdown-cancel-inline" title="Cancel countdown">✕</button>
            </template>
            <template v-else>{{ quizMode === 'sizes' ? 'Guess the Object Size' : 'Guess the Java Version' }}</template>
        </h1>

        <!-- Pre-Quiz QR View -->
        <div v-if="currentSession && (!showQuiz || showQrOverlay)" class="qr-view">
            <p class="qr-heading">📱 Scan to Join</p>
            <div class="qr-card">
                <div class="qr-stats">
                    <strong>{{ totalPlayers }}</strong> signed up
                </div>
                <img
                    v-if="qrCode"
                    :src="qrCode"
                    alt="QR Code"
                    class="qr-image"
                />
                <div class="qr-session">Session ID: {{ sessionId }}</div>
                <button
                    v-if="!showQuiz"
                    @click="setShowQuiz(true); showQrOverlay = false"
                    class="go-quiz-btn"
                >
                    Go to Quiz
                </button>
                <button
                    v-else
                    @click="showQrOverlay = false"
                    class="go-quiz-btn"
                >
                    Back to Quiz
                </button>
            </div>
        </div>

        <!-- Question Mode -->
        <div
            v-else-if="currentSession && showQuiz && currentSession.state === 'active' && !showingResults"
        >
            <div class="stats">
                <span>{{ submittedCount }}/{{ totalPlayers }} players submitted</span>
                <span v-if="currentSession && showQuiz" class="qr-link" @click="showQrOverlay = true">📱 QR</span>
            </div>

            <!-- Question Content -->
            <div v-if="currentQuestion" class="question-content">
                <div class="code-container">
                    <pre class="language-java"><code class="language-java" v-html="highlightedCode"></code></pre>
                </div>
                <div v-if="quizMode === 'sizes' && currentQuestion.useCompactHeaders !== undefined" class="mode-hint">
                    <span class="subtle">Answer uses</span>
                    <span class="pill">Compact headers: {{ currentQuestion.useCompactHeaders ? 'ON' : 'OFF' }}</span>
                </div>
                <div class="options">
                    <button
                        v-for="(option, idx) in answerOptions"
                        :key="idx"
                        class="option-btn"
                    >
                        {{ quizMode === 'sizes' ? option + ' B' : 'Java ' + formatVersion(option) }}
                    </button>
                </div>
            </div>

            <div class="close-btns">
                <button @click="closeQuestion" class="close-btn-small close-btn-now" title="Close immediately">✕ Close</button>
                <button v-if="countdown === null" @click="startCountdown(10)" class="close-btn-small" title="Close in 10 seconds">✕ Close (10s)</button>
                <button v-if="countdown === null" @click="startCountdown(20)" class="close-btn-small" title="Close in 20 seconds">✕ Close (20s)</button>
            </div>
        </div>

        <!-- Results Mode -->
        <div v-else-if="currentSession && showQuiz && showingResults && currentQuestion">
            <!-- Code & Correct Answer -->
            <div class="code-container">
                <pre class="language-java"><code class="language-java" v-html="highlightedCode"></code></pre>
            </div>
            <div class="feedback" :class="{ wrong: false }">
                <strong>Correct Answer:</strong>
                {{
                    quizMode === 'sizes'
                        ? currentQuestion.correct + ' B'
                        : 'Java ' + formatVersion(currentQuestion.correct)
                }}
                <div v-if="quizMode === 'sizes' && currentQuestion.useCompactHeaders !== undefined" class="mode-hint" style="margin-top:8px;">
                    <span class="subtle">Answer uses</span>
                    <span class="pill">Compact headers: {{ currentQuestion.useCompactHeaders ? 'ON' : 'OFF' }}</span>
                </div>
                <div v-if="quizMode === 'sizes' && currentQuestion.classLayout && currentQuestion.classLayout.length" class="layout-info">
                    <div v-for="(cl, clIdx) in currentQuestion.classLayout" :key="clIdx" class="class-layout-block">
                        <div class="class-layout-title">
                            <span class="type">{{ prettifyJvmTypeName(cl.type) }}</span>
                            <span v-if="cl.instanceSize != null" class="tag">Instance size: {{ cl.instanceSize }} B</span>
                            <span v-if="cl.spaceLosses && cl.spaceLosses.total" class="tag">Space losses: {{ cl.spaceLosses.total }} B</span>
                        </div>
                        <div class="table-wrap">
                            <table class="data mono">
                                <thead><tr><th>offset</th><th>size</th><th>type</th><th>description</th></tr></thead>
                                <tbody>
                                    <tr v-for="(row, rIdx) in cl.rows" :key="rIdx">
                                        <td>{{ row.offset }}</td>
                                        <td>{{ row.size }}</td>
                                        <td>{{ prettifyJvmTypeName(row.type) }}</td>
                                        <td class="wrap">{{ row.description }}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div v-if="detectedFeatures.length" class="feature-list">
                    <h3>Features Used:</h3>
                    <div class="feature-accordion">
                        <details
                            v-for="feat in detectedFeatures"
                            :key="feat.name"
                            class="feature"
                        >
                            <summary>
                                <span class="feature-title">
                                    <span class="label">{{ feat.label }}</span>
                                    <span class="meta">(Java {{ formatVersion(feat.version) }})</span>
                                </span>
                                <span class="chevron" aria-hidden="true"></span>
                            </summary>
                            <div class="feature-body">
                                <div
                                    v-if="feat.description"
                                    class="md"
                                    v-html="renderMarkdown(feat.description)"
                                ></div>
                                <div v-else class="feature-item">No description available.</div>
                            </div>
                        </details>
                    </div>
                </div>
            </div>

            <!-- Answer Distribution Histogram -->
            <div class="histogram">
                <h3>Answer Distribution</h3>
                <div class="bars">
                    <div v-for="(option, idx) in answerOptions" :key="idx" class="bar-row">
                        <div class="bar-label">
                            {{ quizMode === 'sizes' ? option + ' B' : 'Java ' + formatVersion(option) }}
                        </div>
                        <div class="bar-container">
                            <div
                                class="bar"
                                :style="{
                                    width:
                                        totalPlayers > 0
                                            ? (answerCounts[idx] / totalPlayers) * 100 + '%'
                                            : '0%',
                                    background:
                                        option === currentQuestion.correct ? 'var(--success)' : 'var(--danger)',
                                }"
                            ></div>
                        </div>
                        <div class="count">{{ answerCounts[idx] }}</div>
                    </div>
                </div>
            </div>

            <!-- Leaderboard -->
            <Leaderboard :players="leaderboard" current-uuid="" style="margin-top: 20px" />

            <button @click="nextQuestion" class="next-btn">Next Question</button>
        </div>

        <!-- Waiting for question (quiz view active) -->
        <div v-else-if="currentSession && showQuiz" class="waiting">
            <div class="stats">
                <span>{{ totalPlayers }} players connected</span>
                <span class="qr-link" @click="showQrOverlay = true">📱 QR</span>
            </div>
            <p class="ready-hint">Ready for the next question?</p>
            <button
                @click="startRandomQuestion"
                class="next-btn start-btn"
                :disabled="!hasQuizData"
            >
                Start Random Question
            </button>
            <p v-if="!hasQuizData" class="loading-hint">Loading questions...</p>
        </div>

        <!-- Waiting -->
        <div v-else-if="!currentSession" class="waiting">
            <p>Waiting for session...</p>
        </div>

        <!-- Quit Game Button (always visible when session is active) -->
        <div v-if="currentSession" class="quit-section">
            <button @click="quitGame" class="quit-btn">🚪 Quit Game</button>
        </div>
    </div>
</template>

<script>
import Leaderboard from './Leaderboard.vue';
import MarkdownIt from 'markdown-it';
import Prism from 'prismjs';
import 'prismjs/components/prism-java';

let ws = null;

export default {
    components: {
        Leaderboard,
    },
    props: {
        sessionId: String,
        authSecret: String,
    },
    data() {
        return {
            currentSession: null,
            currentQuestion: null,
            quizMode: 'java',
            quizData: null,
            featureDescriptions: {},
            answerOptions: [],
            submittedCount: 0,
            totalPlayers: 0,
            answerCounts: [],
            showingResults: false,
            leaderboard: [],
            statsInterval: null,
            showQuiz: false,
            qrCode: '',
            showQrOverlay: false,
            countdown: null,
            countdownInterval: null,
            md: null,
        };
    },
    computed: {
        highlightedCode() {
            if (!this.currentQuestion || !this.currentQuestion.code) return '';
            try {
                return Prism.highlight(this.currentQuestion.code, Prism.languages.java, 'java');
            } catch {
                return this.currentQuestion.code;
            }
        },
        detectedFeatures() {
            if (!this.currentQuestion || !Array.isArray(this.currentQuestion.features)) return [];
            return this.currentQuestion.features
                .filter(Boolean)
                .map((name) => {
                    const meta = this.featureDescriptions[name] || {};
                    return {
                        name,
                        label: meta.label || name,
                        description: meta.description || '',
                        version: meta.version,
                    };
                })
                .sort((a, b) => (b.version || 0) - (a.version || 0));
        },
        hasQuizData() {
            return Array.isArray(this.quizData) && this.quizData.length > 0;
        },
    },
    mounted() {
        this.md = new MarkdownIt({
            html: false,
            linkify: false,
            breaks: false,
            typographer: false,
            highlight(str, lang) {
                const grammar = Prism.languages[lang] || Prism.languages.java;
                try {
                    return `<pre class="language-${lang || 'java'}"><code class="language-${lang || 'java'}">${Prism.highlight(str, grammar, lang || 'java')}</code></pre>`;
                } catch {
                    return '';
                }
            },
        });
        // Restore UI flags from sessionStorage
        if (sessionStorage.getItem(`showQuiz_${this.sessionId}`) === 'true') {
            this.showQuiz = true;
        }
        if (sessionStorage.getItem(`showingResults_${this.sessionId}`) === 'true') {
            this.showingResults = true;
        }
        // Chain: loadSession (gets quizMode) -> loadQuizData (needs quizMode) -> syncCurrentQuestion
        this.loadSession().then(() => {
            if (this.currentSession && (this.currentSession.state === 'active' || this.currentSession.currentQuestion != null)) {
                this.setShowQuiz(true);
            }
            // Now quizMode is set from session — load the right quiz data
            return this.loadQuizData();
        }).then(() => {
            // If we restored showingResults, fetch stats to repopulate histogram/leaderboard
            if (this.showingResults && this.currentQuestion) {
                this.fetchStats();
            }
        });
        this.fetchQrCode();
        this.connectWebSocket();
    },
    methods: {
        async fetchQrCode() {
            try {
                const res = await fetch(`/session/${this.sessionId}/qr`, {
                    headers: {
                        'x-admin-secret': this.authSecret,
                    },
                });
                if (!res.ok) return;
                const data = await res.json();
                this.qrCode = data.qrCode || '';
                if (typeof data.playerCount === 'number') {
                    this.totalPlayers = data.playerCount;
                }
            } catch (e) {
                console.error('Failed to fetch QR code', e);
            }
        },
        async loadQuizData() {
            try {
                // Load data based on quizMode (set from session)
                let response;
                if (this.quizMode === 'sizes') {
                    response = await fetch('/object-sizes.json');
                } else {
                    response = await fetch('/code.json');
                    if (!response.ok) {
                        // Fall back to sizes if code.json doesn't exist
                        response = await fetch('/object-sizes.json');
                        this.quizMode = 'sizes';
                    }
                }
                const data = await response.json();
                let entries = Array.isArray(data) ? data : data.entries || [];
                // Preprocess sizes data to add correct/code fields
                if (this.quizMode === 'sizes') {
                    entries = this.preprocessSizesData(entries);
                }
                this.quizData = entries;
                if (this.quizMode === 'java') {
                    this.loadDescriptions();
                }
                this.syncCurrentQuestion();
            } catch (e) {
                console.error('Failed to load quiz data', e);
            }
        },
        async loadDescriptions() {
            try {
                const res = await fetch('/descriptions.json');
                if (!res.ok) return;
                const data = await res.json();
                this.featureDescriptions = (data && data.features) || {};
            } catch (e) {
                console.error('Failed to load descriptions', e);
            }
        },
        async loadSession() {
            try {
                const res = await fetch(`/session/${this.sessionId}`, {
                    headers: {
                        'x-admin-secret': this.authSecret,
                    },
                });
                if (res.ok) {
                    this.currentSession = await res.json();
                    this.totalPlayers = this.currentSession.playerCount || 0;
                    if (this.currentSession.quizMode) {
                        this.quizMode = this.currentSession.quizMode;
                    }
                    this.syncCurrentQuestion();
                }
            } catch (e) {
                console.error('Failed to load session', e);
            }
        },
        syncCurrentQuestion() {
            if (!this.quizData || !this.currentSession || !this.currentSession.currentQuestion) {
                return;
            }
            const fullQuestion = this.quizData[this.currentSession.currentQuestion];
            if (fullQuestion) {
                this.currentQuestion = fullQuestion;
                this.answerOptions = this.computeAnswerOptions(fullQuestion);
                this.answerCounts = new Array(this.answerOptions.length).fill(0);
            }
        },
        formatVersion(version) {
            if (version === -3) return '1.0-\u03b11';
            if (version === -2) return '1.0-\u03b12';
            if (version === -1) return '1.0-\u03b13';
            if (version === 0) return '1.0';
            if (version >= 1 && version <= 4) return '1.' + version;
            if (version === null || version === undefined) return '?';
            return String(version);
        },
        prettifyJvmTypeName(t) {
            if (!t) return '';
            const s = String(t);
            if (!s.startsWith('[') && s.endsWith(';') && s.startsWith('L')) return s.slice(1, -1);
            if (!s.startsWith('[')) return s;
            let dims = 0;
            while (s.charAt(dims) === '[') dims++;
            const base = s.slice(dims);
            const primMap = { Z:'boolean', B:'byte', C:'char', S:'short', I:'int', J:'long', F:'float', D:'double' };
            let name = '';
            if (primMap[base]) name = primMap[base];
            else if (base.startsWith('L') && base.endsWith(';')) name = base.slice(1, -1);
            else name = base;
            for (let i = 0; i < dims; i++) name += '[]';
            return name;
        },
        renderMarkdown(md) {
            if (!this.md || !md) return '';
            return this.md.render(String(md));
        },
        setShowQuiz(val) {
            this.showQuiz = val;
            sessionStorage.setItem(`showQuiz_${this.sessionId}`, String(val));
        },
        setShowingResults(val) {
            this.showingResults = val;
            sessionStorage.setItem(`showingResults_${this.sessionId}`, String(val));
        },
        connectWebSocket() {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const presenterId = `presenter-${Math.random().toString(36).slice(2, 10)}`;
            ws = new WebSocket(`${protocol}//${window.location.host}/ws?uuid=${presenterId}`);

            ws.onmessage = (event) => {
                const msg = JSON.parse(event.data);
                if (msg.type === 'leaderboard') {
                    this.leaderboard = msg.data || [];
                } else if (msg.type === 'question_started') {
                    const questionId = msg.questionId;
                    const fullQuestion = this.quizData ? this.quizData[questionId] : null;
                    if (fullQuestion) {
                        this.currentQuestion = fullQuestion;
                    } else {
                        this.currentQuestion = { id: questionId };
                    }
                    if (this.currentSession) {
                        this.currentSession.state = 'active';
                        this.currentSession.currentQuestion = questionId;
                    }
                    this.setShowingResults(false);
                    this.submittedCount = 0;
                    if (fullQuestion) {
                        this.answerOptions = this.computeAnswerOptions(fullQuestion);
                        this.answerCounts = new Array(this.answerOptions.length).fill(0);
                    }
                } else if (msg.type === 'player_answered') {
                    this.submittedCount++;
                } else if (msg.type === 'question_stopped') {
                    this.setShowingResults(true);
                    if (this.currentSession) {
                        this.currentSession.state = 'waiting';
                    }
                    this.fetchStats();
                } else if (msg.type === 'session_restarted') {
                    this.setShowingResults(false);
                    this.currentQuestion = null;
                    this.submittedCount = 0;
                    this.answerOptions = [];
                    this.answerCounts = [];
                } else if (msg.type === 'stats-update') {
                    // Receive stats push from server
                    this.totalPlayers = msg.playerCount || 0;
                    this.submittedCount = msg.answeredCount || 0;
                }
            };

            ws.onopen = () => {
                if (this.sessionId) {
                    ws.send(
                        JSON.stringify({
                            type: 'join-session-presenter',
                            sessionId: this.sessionId,
                        })
                    );
                }
            };
        },
        async fetchStats() {
            try {
                const res = await fetch('/admin/stats', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'x-admin-secret': this.authSecret,
                    },
                    body: JSON.stringify({ sessionId: this.sessionId }),
                });
                if (!res.ok) return;
                const data = await res.json();
                const distribution = data.answerDistribution || {};
                this.leaderboard = data.leaderboard || this.leaderboard;
                this.answerCounts = this.answerOptions.map((option) => {
                    const key = String(option);
                    return distribution[key] || 0;
                });
            } catch (e) {
                console.error('Failed to fetch stats', e);
            }
        },
        startStatsPolling() {
            // No longer needed - stats are pushed via WebSocket
        },
        startCountdown(seconds = 20) {
            this.countdown = seconds;
            // Notify server so it can relay countdown to players
            fetch('/admin/start_countdown', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'x-admin-secret': this.authSecret,
                },
                body: JSON.stringify({
                    sessionId: this.sessionId,
                    seconds,
                }),
            }).catch(err => console.error('Failed to notify countdown:', err));
            this.countdownInterval = setInterval(() => {
                this.countdown--;
                if (this.countdown <= 0) {
                    this.cancelCountdown();
                    this.closeQuestion();
                }
            }, 1000);
        },
        cancelCountdown() {
            if (this.countdownInterval) {
                clearInterval(this.countdownInterval);
                this.countdownInterval = null;
            }
            this.countdown = null;
        },
        async closeQuestion() {
            this.cancelCountdown();
            try {
                const res = await fetch(`/admin/stop_question`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'x-admin-secret': this.authSecret,
                    },
                    body: JSON.stringify({ sessionId: this.sessionId }),
                });
                if (!res.ok) {
                    const text = await res.text();
                    alert(`Failed to close question: ${text || res.status}`);
                } else {
                    this.setShowingResults(true);
                    if (this.currentSession) {
                        this.currentSession.state = 'waiting';
                    }
                    this.fetchStats();
                }
            } catch (e) {
                console.error('Failed to close question', e);
            }
        },
        getRandomQuestionId() {
            const entries = Array.isArray(this.quizData) ? this.quizData : [];
            if (!entries.length) return null;
            // Build list of valid (non-alpha) indices
            const validIndices = [];
            for (let i = 0; i < entries.length; i++) {
                const v = entries[i].correct;
                if (v === undefined || v >= 0) {
                    validIndices.push(i);
                }
            }
            if (!validIndices.length) return null;
            return validIndices[Math.floor(Math.random() * validIndices.length)];
        },
        async startRandomQuestion() {
            try {
                if (!this.authSecret) {
                    alert('Missing admin secret. Please reload and authenticate again.');
                    return;
                }
                if (!this.sessionId) {
                    alert('Missing session id. Please reload the presenter view.');
                    return;
                }
                if (!this.hasQuizData) {
                    await this.loadQuizData();
                }
                const questionId = this.getRandomQuestionId();
                if (questionId === null) {
                    alert('No questions loaded yet. Please wait a moment and try again.');
                    return;
                }
                const fullQuestion = this.quizData ? this.quizData[questionId] : null;
                const correctAnswer = fullQuestion ? fullQuestion.correct : null;
                const res = await fetch('/admin/start_question', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'x-admin-secret': this.authSecret,
                    },
                    body: JSON.stringify({
                        sessionId: this.sessionId,
                        questionId,
                        correctAnswer,
                        answerOptions: fullQuestion ? this.computeAnswerOptions(fullQuestion) : [],
                    }),
                });
                if (!res.ok) {
                    const text = await res.text();
                    alert(`Failed to start question: ${text || res.status}`);
                } else {
                    if (fullQuestion) {
                        this.currentQuestion = fullQuestion;
                        this.answerOptions = this.computeAnswerOptions(fullQuestion);
                        this.answerCounts = new Array(this.answerOptions.length).fill(0);
                    }
                    this.setShowingResults(false);
                    this.submittedCount = 0;
                    if (this.currentSession) {
                        this.currentSession.state = 'active';
                        this.currentSession.currentQuestion = questionId;
                    }
                }
            } catch (e) {
                console.error('Failed to start question', e);
            }
        },
        nextQuestion() {
            this.setShowingResults(false);
            this.currentQuestion = null;
            this.startRandomQuestion();
        },
        computeAnswerOptions(question) {
            if (this.quizMode === 'sizes') {
                return this.generateSizesOptions(question);
            }
            // For Java quiz, generate deterministic options using question code as seed
            const correct = question.correct;
            const codeHash = question.code
                .split('')
                .reduce((h, c) => (h << 5) - h + c.charCodeAt(0), 0);
            const seed = Math.abs(codeHash) % 1000;

            const wrong = [];
            for (let i = 0; i < 4; i++) {
                const val = (((seed + i * 123) % 25) + i) % 25;
                if (val !== correct) {
                    wrong.push(val);
                }
            }
            return [...new Set([correct, ...wrong])].sort((a, b) => a - b).slice(0, 5);
        },
        /**
         * Simple 32-bit FNV-1a hash (matches original game).
         */
        fnv1a32(str) {
            let h = 0x811c9dc5;
            for (let i = 0; i < str.length; i++) {
                h ^= str.charCodeAt(i);
                h = (h + ((h << 1) + (h << 4) + (h << 7) + (h << 8) + (h << 24))) >>> 0;
            }
            return h >>> 0;
        },
        /**
         * Preprocess raw object-sizes entries into quiz-ready format.
         * Deterministically chooses between compact and non-compact variants.
         */
        preprocessSizesData(raw) {
            if (!Array.isArray(raw)) return [];
            return raw.map(e => {
                if (!e) return null;
                const variants = Array.isArray(e.layout) ? e.layout : [];
                const nonCompact = variants.find(v => v && v.UseCompactObjectHeaders === false);
                const compact = variants.find(v => v && v.UseCompactObjectHeaders === true);
                const questionKey = (e['class'] || '') + '|' + ((e.sanitizedCode || e.code || '').trim());
                const preferCompact = (this.fnv1a32('|' + questionKey) & 1) === 1;
                const chosen = (preferCompact ? (compact || nonCompact) : (nonCompact || compact)) || variants[0] || null;
                const totalSize = chosen && typeof chosen.totalSize === 'number' ? chosen.totalSize : null;
                if (typeof totalSize !== 'number') return null;
                return {
                    kind: 'sizes',
                    code: (e.sanitizedCode || e.code || '').trim(),
                    correct: totalSize,
                    useCompactHeaders: chosen.UseCompactObjectHeaders === true,
                    classLayout: Array.isArray(chosen.classLayout) ? chosen.classLayout : [],
                    footprint: Array.isArray(chosen.footprint) ? chosen.footprint : [],
                    rawCode: (e.code || '').trim(),
                };
            }).filter(Boolean);
        },
        /**
         * Generate plausible answer options for a sizes question.
         * Picks wrong answers from the pool of all correct sizes in the dataset,
         * falling back to multiples of 8 near the correct answer.
         */
        generateSizesOptions(question) {
            const correct = question.correct;
            // Build a pool of all unique sizes from the quiz data
            let pool = (this.quizData || []).map(q => q && typeof q.correct === 'number' ? q.correct : null).filter(v => typeof v === 'number');
            pool = [...new Set(pool)].filter(v => v !== correct);

            // Deterministic shuffle using code hash
            const codeHash = (question.code || '').split('').reduce((h, c) => (h << 5) - h + c.charCodeAt(0), 0);
            const seed = Math.abs(codeHash);
            pool.sort((a, b) => {
                const ha = ((seed ^ (a * 2654435761)) >>> 0) % 1000000;
                const hb = ((seed ^ (b * 2654435761)) >>> 0) % 1000000;
                return ha - hb;
            });

            const wrong = pool.slice(0, 4);

            // If not enough, fill with nearby multiples of 8
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
        },
        logout() {
            this.$emit('back');
        },
        quitGame() {
            if (!confirm('Quit the game?')) return;
            if (ws) ws.close();
            if (this.statsInterval) clearInterval(this.statsInterval);
            this.cancelCountdown();
            this.$emit('back');
        },
    },
    beforeUnmount() {
        if (this.statsInterval) clearInterval(this.statsInterval);
        this.cancelCountdown();
        if (ws) ws.close();
    },
};
</script>

<style scoped>
/* Layout is handled by shared.css #app — no card styling here */
#presenter-view-game {
    /* content fills parent */
}

.stats {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
    padding: 10px;
    background: var(--bg-badge);
    border-radius: 4px;
    font-size: 0.9em;
}

.qr-link {
    cursor: pointer;
    user-select: none;
}

.qr-link:hover {
    text-decoration: underline;
}

.histogram {
    margin-top: 20px;
}
/* Feature accordion */
.feature-accordion {
    margin-top: 6px;
    border-radius: 6px;
    overflow: hidden;
}

.feature-accordion details {
    background: var(--bg-section);
    border-radius: 6px;
    margin: 6px 0;
    padding: 0;
}

.feature-accordion summary {
    list-style: none;
    cursor: pointer;
    padding: 8px 10px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 10px;
    user-select: none;
    line-height: 1.25;
    white-space: nowrap;
}

.feature-accordion summary::-webkit-details-marker {
    display: none;
}

.feature-accordion summary::before {
    content: '';
    display: none;
}

.feature-accordion .feature-title {
    display: inline-flex;
    align-items: baseline;
    gap: 8px;
    min-width: 0;
    flex: 1 1 auto;
    overflow: hidden;
}

.feature-accordion .feature-title .label {
    font-weight: 600;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.feature-accordion .feature-title .meta {
    opacity: 0.8;
    font-size: 0.9em;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.feature-accordion .chevron {
    width: 1.2em;
    height: 1.2em;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 0.95em;
    opacity: 0.75;
    flex: 0 0 auto;
}

.feature-accordion details[open] summary .chevron::before {
    content: "▲";
}

.feature-accordion details:not([open]) summary .chevron::before {
    content: "▼";
}

.feature-body {
    padding: 0 1em 1em;
}

.feature-body code {
    background: var(--bg-badge);
    color: var(--text-primary);
    padding: 1px 5px;
    border-radius: 3px;
    font-size: 0.9em;
    font-family: 'Courier New', Consolas, monospace;
}

.feature-body pre {
    background: #2d2d2d;
    color: #e0e0e0;
    padding: 10px 14px;
    border-radius: 6px;
    overflow-x: auto;
    font-size: 0.85em;
    line-height: 1.5;
}

.feature-body pre code {
    background: none;
    color: inherit;
    padding: 0;
    font-size: inherit;
}

.feature-item {
    margin: 4px 0;
    padding: 4px 8px;
    background: var(--bg-section);
    border-radius: 3px;
}

.features pre[class*='language-'] {
    height: auto;
    font-size: 0.8em;
}
.qr-heading {
    font-size: 1.3em;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 10px;
}

.qr-view {
    text-align: center;
    padding: 10px 0 20px;
}

.qr-card {
    margin: 0 auto;
    max-width: 420px;
    background: var(--bg-section);
    border-radius: 10px;
    padding: 20px;
    border: 1px solid var(--border-color);
}

.qr-stats {
    font-size: 18px;
    margin-bottom: 12px;
}

.qr-image {
    width: 260px;
    height: 260px;
    border: 3px solid var(--accent);
    border-radius: 8px;
    background: white;
    margin: 8px auto 12px;
    display: block;
}

.qr-session {
    color: var(--text-secondary);
    font-size: 14px;
    margin-bottom: 16px;
}

.go-quiz-btn {
    padding: 10px 24px;
    background: var(--success);
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 15px;
    font-weight: 600;
}

.bars {
    margin-top: 15px;
}

.bar-row {
    display: grid;
    grid-template-columns: 100px 1fr 50px;
    gap: 10px;
    align-items: center;
    margin-bottom: 10px;
}

.bar-label {
    font-weight: bold;
    font-size: 14px;
}

.bar-container {
    background: var(--bg-badge);
    border-radius: 4px;
    overflow: hidden;
    height: 30px;
}

.bar {
    height: 100%;
    transition: width 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding-right: 5px;
    color: white;
    font-weight: bold;
    font-size: 12px;
}

.count {
    text-align: center;
    font-weight: bold;
}

.next-btn {
    display: block;
    width: 100%;
    margin-top: 20px;
    padding: 12px;
    background-color: var(--text-secondary);
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
    font-weight: bold;
    transition: background 0.2s;
}

@media (hover: hover) and (pointer: fine) {
    .next-btn:hover {
        background-color: #343a40;
    }
    .go-quiz-btn:hover {
        background-color: #218838;
    }
}

@media (hover: none) and (pointer: coarse) {
    .next-btn:active {
        background-color: #343a40;
    }
    .go-quiz-btn:active {
        background-color: #218838;
    }
}

.waiting {
    text-align: center;
    padding: 40px;
}

.loading-hint {
    margin: 6px 0 14px;
    color: var(--text-muted);
    font-size: 13px;
}

.ready-hint {
    font-size: 1.1em;
    color: var(--text-secondary);
    margin-bottom: 20px;
}

.start-btn {
    background-color: var(--success);
}

/* Two close buttons wrapper */
.close-btns {
    display: flex;
    gap: 8px;
    float: right;
    margin-top: 14px;
}

/* Small close button */
.close-btn-small {
    display: inline-block;
    padding: 6px 14px;
    background: var(--warning);
    color: var(--text-primary);
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
    font-weight: 600;
}

.close-btn-now {
    background: var(--danger);
    color: #fff;
}

@media (hover: hover) and (pointer: fine) {
    .close-btn-small:hover {
        background: #e0a800;
    }
}

/* Countdown cancel button (inline in h1) */
.countdown-cancel-inline {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    margin-left: 12px;
    width: 32px;
    height: 32px;
    background: rgba(220, 53, 69, 0.15);
    color: var(--danger);
    border: 2px solid var(--danger);
    border-radius: 50%;
    cursor: pointer;
    font-size: 16px;
    font-weight: 700;
    line-height: 1;
    vertical-align: middle;
    transition: background 0.2s, color 0.2s;
    padding: 0;
}

.countdown-cancel-inline:hover {
    background: var(--danger);
    color: #fff;
}

.quit-section {
    margin-top: 40px;
    padding-top: 20px;
    border-top: 1px solid var(--border-separator);
    text-align: center;
}
.quit-btn {
    background: var(--text-muted);
    color: #fff;
    border: none;
    padding: 10px 28px;
    border-radius: 6px;
    font-size: 0.95em;
    cursor: pointer;
    transition: background 0.15s;
}
.quit-btn:hover {
    background: var(--text-secondary);
}
.quit-hint {
    margin-top: 6px;
    font-size: 0.8em;
    color: var(--text-muted);
}

/* Layout info tables for object-size mode */
.layout-info {
    margin-top: 16px;
}
.class-layout-block {
    margin-bottom: 16px;
    background: var(--bg-section);
    border-radius: 6px;
    padding: 12px;
}
.class-layout-title {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;
    margin-bottom: 8px;
    font-size: 0.95em;
}
.class-layout-title .type {
    font-weight: 600;
    font-family: 'Courier New', monospace;
}
.class-layout-title .tag {
    display: inline-block;
    background: var(--bg-badge);
    padding: 2px 8px;
    border-radius: 10px;
    font-size: 0.82em;
    color: var(--text-secondary);
}
.table-wrap {
    overflow-x: auto;
}
table.data {
    width: 100%;
    border-collapse: collapse;
    font-size: 0.85em;
}
table.data th,
table.data td {
    padding: 4px 8px;
    border: 1px solid var(--border-separator);
    text-align: left;
}
table.data th {
    background: var(--bg-badge);
    font-weight: 600;
    white-space: nowrap;
}
table.data td.wrap {
    word-break: break-word;
}
table.mono td {
    font-family: 'Courier New', monospace;
    font-size: 0.92em;
}

/* Compact-headers hint for object-size mode */
.mode-hint {
    text-align: center;
    margin: 8px 0 12px;
    font-size: 0.92em;
}
.mode-hint .subtle {
    color: var(--text-muted);
    margin-right: 6px;
}
.mode-hint .pill {
    display: inline-block;
    background: var(--bg-pill);
    color: var(--pill-text);
    padding: 2px 10px;
    border-radius: 12px;
    font-weight: 600;
    font-size: 0.95em;
}
</style>
