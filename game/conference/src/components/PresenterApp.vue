<template>
    <div id="presenter-app">
        <div class="presenter-container">
            <h1>📊 Conference Presenter View</h1>

            <div
                v-if="!isAuthenticated"
                class="auth-section"
                style="
                    max-width: 400px;
                    margin: 40px auto;
                    padding: 30px;
                    background: #f8f9fa;
                    border-radius: 8px;
                    text-align: center;
                "
            >
                <h2>🔐 Presenter Authentication</h2>
                <p style="color: #6c757d">Enter your admin secret to access presenter controls</p>
                <input
                    v-model="authSecret"
                    type="password"
                    placeholder="Admin Secret"
                    @keyup.enter="authenticate"
                    style="
                        width: 100%;
                        padding: 10px;
                        margin-bottom: 10px;
                        border: 1px solid #ccc;
                        border-radius: 4px;
                        box-sizing: border-box;
                        font-size: 14px;
                    "
                />
                <p v-if="authError" style="color: #d9534f; margin: 10px 0">{{ authError }}</p>
                <button
                    @click="authenticate"
                    style="
                        width: 100%;
                        padding: 10px;
                        background: #007bff;
                        color: white;
                        border: none;
                        border-radius: 4px;
                        cursor: pointer;
                        font-size: 16px;
                    "
                >
                    Authenticate
                </button>
            </div>

            <template v-else>
                <!-- Sessions List - Always Visible -->
                <div style="max-width: 900px; margin: 0 auto; padding: 20px">
                    <div
                        style="
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 25px;
                        "
                    >
                        <h2 style="margin: 0">📋 Active Sessions</h2>
                        <button
                            @click="logout"
                            style="
                                padding: 8px 16px;
                                background: #dc3545;
                                color: white;
                                border: none;
                                border-radius: 4px;
                                cursor: pointer;
                                font-size: 14px;
                            "
                        >
                            Logout
                        </button>
                    </div>

                    <!-- Create New Session -->
                    <div
                        style="
                            display: flex;
                            gap: 10px;
                            margin-bottom: 20px;
                            flex-wrap: wrap;
                        "
                    >
                        <input
                            v-model="sessionNameInput"
                            placeholder="Enter session name"
                            @keyup.enter="createNewSession('java')"
                            style="
                                padding: 8px 12px;
                                border: 1px solid #ccc;
                                border-radius: 4px;
                                font-size: 14px;
                                flex: 1;
                                min-width: 200px;
                            "
                        />
                        <button
                            @click="createNewSession('java')"
                            style="
                                padding: 8px 20px;
                                background: #28a745;
                                color: white;
                                border: none;
                                border-radius: 4px;
                                cursor: pointer;
                                font-size: 14px;
                                font-weight: 600;
                            "
                        >
                            ☕ Version Quiz
                        </button>
                        <button
                            @click="createNewSession('sizes')"
                            style="
                                padding: 8px 20px;
                                background: #17a2b8;
                                color: white;
                                border: none;
                                border-radius: 4px;
                                cursor: pointer;
                                font-size: 14px;
                                font-weight: 600;
                            "
                        >
                            📐 Sizes Quiz
                        </button>
                        <button
                            @click="loadSessionsList"
                            style="
                                padding: 8px 16px;
                                background: #6c757d;
                                color: white;
                                border: none;
                                border-radius: 4px;
                                cursor: pointer;
                                font-size: 14px;
                            "
                        >
                            Refresh
                        </button>
                    </div>

                    <!-- Sessions Loading State -->
                    <div
                        v-if="sessionsLoading"
                        style="text-align: center; padding: 40px; color: #6c757d"
                    >
                        Loading sessions...
                    </div>

                    <!-- No Sessions Message -->
                    <div
                        v-else-if="sessionsList.length === 0"
                        style="text-align: center; padding: 40px"
                    >
                        <p style="color: #6c757d; font-size: 16px">No active sessions</p>
                    </div>

                    <!-- Sessions Grid -->
                    <div v-else class="sessions-grid">
                        <div
                            v-for="session in sessionsList"
                            :key="session.sessionId"
                            class="session-card"
                        >
                            <div class="session-header">
                                <h3>{{ session.name }}</h3>
                                <span class="session-id">{{ session.sessionId }}</span>
                            </div>
                            <div class="session-stats">
                                <div class="stat">
                                    <span class="label">Players:</span>
                                    <span class="value">{{ session.playerCount }}</span>
                                </div>
                                <div class="stat">
                                    <span class="label">State:</span>
                                    <span class="value">{{ session.state }}</span>
                                </div>
                                <div class="stat">
                                    <span class="label">Mode:</span>
                                    <span class="value">{{ session.quizMode === 'sizes' ? '📐 Sizes' : '☕ Version' }}</span>
                                </div>
                            </div>
                            <div class="session-actions">
                                <button @click="joinSession(session)" class="join-btn">
                                    Join Session
                                </button>
                                <button
                                    @click="restartSession(session.sessionId)"
                                    class="restart-btn"
                                >
                                    Restart
                                </button>
                                <button
                                    @click="deleteSession(session.sessionId)"
                                    class="delete-btn"
                                >
                                    Delete
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </template>

            <!-- OLD CONTENT - Remove below -->
            <!-- This section is being replaced by the new sessions list view above -->
            <template v-if="false">
                <!-- Game View (when session is active) -->
                <PresenterViewGame
                    v-if="currentSession && currentSession.state === 'active'"
                    :session-id="sessionIdInput"
                    :auth-secret="authSecret"
                    @back="goBackToWaiting"
                />

                <!-- Waiting for players screen -->
                <div
                    v-if="sessionIdCreated && !currentSession"
                    style="text-align: center; padding: 60px 20px; max-width: 600px; margin: 0 auto"
                >
                    <h2 style="font-size: 32px; margin-bottom: 20px">👥 Waiting for Players</h2>
                    <div
                        style="
                            font-size: 72px;
                            font-weight: bold;
                            color: #007bff;
                            margin-bottom: 30px;
                        "
                    >
                        {{ playerCount }}
                    </div>
                    <p style="font-size: 16px; color: #666; margin-bottom: 30px">
                        Registered {{ playerCount }} {{ playerCount === 1 ? 'player' : 'players' }}
                    </p>
                    <img
                        v-if="qrCode"
                        :src="qrCode"
                        alt="QR Code"
                        style="
                            width: 280px;
                            height: 280px;
                            margin-bottom: 30px;
                            border: 3px solid #007bff;
                        "
                    />
                    <div style="margin-bottom: 20px">
                        <p style="font-size: 14px; color: #999">
                            Session ID: {{ sessionIdCreated }}
                        </p>
                    </div>
                    <button
                        @click="startQuiz"
                        style="
                            padding: 12px 40px;
                            background: #28a745;
                            color: white;
                            border: none;
                            border-radius: 4px;
                            cursor: pointer;
                            font-size: 18px;
                            font-weight: bold;
                        "
                    >
                        Start Quiz
                    </button>
                </div>

                <!-- Load session controls (when no session created yet) -->
                <div v-else>
                    <div
                        style="
                            display: flex;
                            justify-content: space-between;
                            align-items: center;
                            margin-bottom: 20px;
                        "
                    >
                        <div class="controls-section">
                            <button
                                @click="createNewSession"
                                style="
                                    padding: 10px 20px;
                                    background: #28a745;
                                    color: white;
                                    border: none;
                                    border-radius: 4px;
                                    cursor: pointer;
                                    font-size: 16px;
                                    margin-right: 10px;
                                "
                            >
                                Create New Session
                            </button>
                            <input
                                v-model="sessionNameInput"
                                placeholder="Enter session name"
                                @keyup.enter="createNewSession"
                                style="
                                    padding: 8px 12px;
                                    margin-right: 10px;
                                    border: 1px solid #ccc;
                                    border-radius: 4px;
                                    font-size: 14px;
                                "
                            />
                            <button
                                @click="loadSession"
                                style="
                                    padding: 10px 20px;
                                    background: #007bff;
                                    color: white;
                                    border: none;
                                    border-radius: 4px;
                                    cursor: pointer;
                                    font-size: 16px;
                                "
                            >
                                Load Session
                            </button>
                            <input
                                v-model="sessionIdInput"
                                placeholder="Enter Session ID"
                                @keyup.enter="loadSession"
                                style="
                                    padding: 8px 12px;
                                    margin-left: 10px;
                                    border: 1px solid #ccc;
                                    border-radius: 4px;
                                    font-size: 14px;
                                "
                            />
                        </div>
                        <button
                            @click="logout"
                            style="
                                padding: 8px 16px;
                                background: #dc3545;
                                color: white;
                                border: none;
                                border-radius: 4px;
                                cursor: pointer;
                                font-size: 14px;
                            "
                        >
                            Logout
                        </button>
                    </div>
                </div>

                <div
                    v-if="currentSession"
                    class="session-info"
                    style="margin-top: 20px; padding: 15px; background: #f8f9fa; border-radius: 4px"
                >
                    <h2>Session: {{ currentSession.sessionId }}</h2>
                    <div
                        style="
                            display: grid;
                            grid-template-columns: 1fr 1fr 1fr;
                            gap: 15px;
                            margin-top: 15px;
                        "
                    >
                        <div
                            style="
                                background: white;
                                padding: 15px;
                                border-radius: 4px;
                                border-left: 4px solid #007bff;
                            "
                        >
                            <div style="font-size: 28px; font-weight: bold">
                                {{ currentSession.playerCount }}
                            </div>
                            <div style="color: #6c757d; font-size: 12px">Players</div>
                        </div>
                        <div
                            style="
                                background: white;
                                padding: 15px;
                                border-radius: 4px;
                                border-left: 4px solid #28a745;
                            "
                        >
                            <div style="font-size: 28px; font-weight: bold">
                                {{ currentSession.answeredCount }}
                            </div>
                            <div style="color: #6c757d; font-size: 12px">Answered</div>
                        </div>
                        <div
                            style="
                                background: white;
                                padding: 15px;
                                border-radius: 4px;
                                border-left: 4px solid #ffc107;
                            "
                        >
                            <div style="font-size: 28px; font-weight: bold">
                                {{ currentQuestion ? 'Q' + (currentQuestionIndex + 1) : '-' }}
                            </div>
                            <div style="color: #6c757d; font-size: 12px">Current Question</div>
                        </div>
                    </div>
                </div>

                <Leaderboard
                    v-if="currentSession"
                    :players="leaderboard"
                    current-uuid=""
                    style="margin-top: 30px"
                />

                <div
                    v-if="!currentSession"
                    style="
                        margin-top: 40px;
                        padding: 40px;
                        text-align: center;
                        background: #f8f9fa;
                        border-radius: 4px;
                    "
                >
                    <p style="color: #6c757d; font-size: 18px">
                        Enter a session ID to view presenter stats
                    </p>
                </div>
            </template>
        </div>
    </div>
</template>

<script>
import Leaderboard from './Leaderboard.vue';
import PresenterViewGame from './PresenterViewGame.vue';

let ws = null;

export default {
    components: {
        Leaderboard,
        PresenterViewGame,
    },
    data() {
        return {
            isAuthenticated: false,
            authSecret: '',
            authError: '',
            sessionIdInput: '',
            sessionNameInput: '',
            sessionIdCreated: '',
            qrCode: '',
            playerCount: 0,
            currentSession: null,
            currentQuestion: null,
            currentQuestionIndex: 0,
            leaderboard: [],
            statsInterval: null,
            qrStatsInterval: null,
            sessionsList: [],
            sessionsLoading: false,
        };
    },
    mounted() {
        this.checkAuthFromCookie();
        this.loadSessionFromStorage();
        // Load sessions list for display
        if (this.isAuthenticated) {
            this.loadSessionsList();
        }
    },
    methods: {
        loadSessionFromStorage() {
            const stored = localStorage.getItem('presenter_session');
            if (stored) {
                const session = JSON.parse(stored);
                this.sessionIdCreated = session.sessionId;
                this.sessionIdInput = session.sessionId;
            }
        },
        async loadSessionsList() {
            if (!this.isAuthenticated) return Promise.resolve();
            try {
                this.sessionsLoading = true;
                const res = await fetch('/admin/sessions', {
                    headers: {
                        'x-admin-secret': this.authSecret,
                    },
                });
                if (res.ok) {
                    const data = await res.json();
                    this.sessionsList = data.sessions || [];
                }
            } catch (e) {
                console.error('Failed to load sessions list', e);
            } finally {
                this.sessionsLoading = false;
            }
        },
        checkAuthFromCookie() {
            const cookie = document.cookie
                .split('; ')
                .find((row) => row.startsWith('presenter_auth='));
            if (cookie) {
                this.authSecret = cookie.split('=')[1];
                this.isAuthenticated = true;
            }
        },
        setAuthCookie(secret) {
            // Set cookie with 7-day expiration
            const date = new Date();
            date.setTime(date.getTime() + 7 * 24 * 60 * 60 * 1000);
            document.cookie = `presenter_auth=${encodeURIComponent(secret)}; expires=${date.toUTCString()}; path=/`;
        },
        deleteAuthCookie() {
            document.cookie = 'presenter_auth=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        },
        async authenticate() {
            if (!this.authSecret.trim()) {
                this.authError = 'Please enter the admin secret';
                return;
            }
            this.authError = '';
            this.isAuthenticated = true;
            this.setAuthCookie(this.authSecret);
            // Immediately load and display sessions
            await this.loadSessionsList();
        },
        async createNewSession(quizMode = 'java') {
            const sessionName = this.sessionNameInput.trim();
            if (!sessionName) {
                alert('Please enter a session name');
                return;
            }

            try {
                const res = await fetch('/session/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'x-admin-secret': this.authSecret,
                    },
                    body: JSON.stringify({
                        name: this.sessionNameInput,
                        quizMode,
                    }),
                });

                if (res.ok) {
                    const data = await res.json();
                    this.sessionIdCreated = data.sessionId;
                    this.sessionNameInput = '';
                    // Save to localStorage
                    localStorage.setItem(
                        'presenter_session',
                        JSON.stringify({
                            sessionId: data.sessionId,
                        })
                    );
                    // Navigate to view
                    window.location.href = `/presenter/view/${encodeURIComponent(sessionName)}`;
                } else {
                    alert('Failed to create session');
                }
            } catch (e) {
                console.error('Failed to create session', e);
                alert('Error creating session');
            }
        },
        logout() {
            this.isAuthenticated = false;
            this.authSecret = '';
            this.currentSession = null;
            this.sessionIdCreated = '';
            this.sessionIdInput = '';
            this.leaderboard = [];
            this.qrCode = '';
            this.playerCount = 0;
            this.deleteAuthCookie();
            localStorage.removeItem('presenter_session');
            if (ws) ws.close();
            if (this.qrStatsInterval) clearInterval(this.qrStatsInterval);
            if (this.statsInterval) clearInterval(this.statsInterval);
        },
        async loadSession() {
            const sessionId = this.sessionIdInput.trim();
            if (!sessionId) return;

            try {
                const res = await fetch(`/session/${sessionId}`, {
                    headers: {
                        'x-admin-secret': this.authSecret,
                    },
                });

                if (!res.ok) {
                    alert('Failed to load session');
                    return;
                }

                this.currentSession = await res.json();
                // Save to localStorage
                localStorage.setItem(
                    'presenter_session',
                    JSON.stringify({
                        sessionId: sessionId,
                    })
                );
                // Navigate to view
                window.location.href = `/presenter/view/${encodeURIComponent(sessionId)}`;
            } catch (e) {
                console.error('Failed to load session', e);
                alert('Error loading session');
            }
        },
        connectWebSocket() {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const presenterId = `presenter-${Math.random().toString(36).slice(2, 10)}`;
            ws = new WebSocket(`${protocol}//${window.location.host}/ws?uuid=${presenterId}`);

            ws.onmessage = (event) => {
                const msg = JSON.parse(event.data);
                if (msg.type === 'leaderboard') {
                    this.leaderboard = msg.data || [];
                } else if (msg.type === 'question') {
                    this.currentQuestion = msg.data;
                }
            };

            ws.onopen = () => {
                if (this.currentSession) {
                    ws.send(
                        JSON.stringify({
                            type: 'join-session-presenter',
                            sessionId: this.currentSession.sessionId,
                        })
                    );
                }
            };
        },
        startStatsPolling() {
            if (this.statsInterval) clearInterval(this.statsInterval);
            this.statsInterval = setInterval(async () => {
                try {
                    const res = await fetch(`/session/${this.currentSession.sessionId}/stats`, {
                        headers: {
                            'x-admin-secret': this.authSecret,
                        },
                    });
                    if (res.ok) {
                        const stats = await res.json();
                        this.currentSession.playerCount = stats.playerCount || 0;
                        this.currentSession.answeredCount = stats.answeredCount || 0;
                    }
                } catch (e) {
                    console.error('Failed to fetch stats', e);
                }
            }, 2000);
        },
        async fetchQRAndStats() {
            try {
                const res = await fetch(`/session/${this.sessionIdCreated}/qr`, {
                    headers: {
                        'x-admin-secret': this.authSecret,
                    },
                });
                if (res.ok) {
                    const data = await res.json();
                    this.qrCode = data.qrCode;
                    this.playerCount = data.playerCount || 0;
                }
            } catch (e) {
                console.error('Failed to fetch QR code', e);
            }
        },
        async joinSession(session) {
            const sessionSlug = (session && session.name) || (session && session.sessionId) || '';
            if (!sessionSlug.trim()) return;
            // Navigate to view
            window.location.href = `/presenter/view/${encodeURIComponent(sessionSlug.trim())}`;
        },
        async restartSession(sessionId) {
            if (!confirm(`Restart session ${sessionId}?`)) return;
            try {
                const res = await fetch('/admin/session/restart', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'x-admin-secret': this.authSecret,
                    },
                    body: JSON.stringify({ sessionId }),
                });
                if (!res.ok) {
                    alert('Failed to restart session');
                    return;
                }
                this.loadSessionsList();
            } catch (e) {
                console.error('Failed to restart session', e);
                alert('Error restarting session');
            }
        },
        async deleteSession(sessionId) {
            if (!confirm(`Delete session ${sessionId}?`)) return;
            try {
                await fetch('/admin/session/delete', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'x-admin-secret': this.authSecret,
                    },
                    body: JSON.stringify({ sessionId }),
                });
                this.loadSessionsList();
            } catch (e) {
                console.error('Failed to delete session', e);
                alert('Error deleting session');
            }
        },
        async startQuiz() {
            if (!this.sessionIdCreated) return;
            try {
                const res = await fetch(`/session/${this.sessionIdCreated}`, {
                    headers: {
                        'x-admin-secret': this.authSecret,
                    },
                });
                if (res.ok) {
                    this.currentSession = await res.json();
                    this.connectWebSocket();
                    this.startStatsPolling();
                    if (this.qrStatsInterval) clearInterval(this.qrStatsInterval);
                }
            } catch (e) {
                console.error('Failed to start quiz', e);
            }
        },
        goBackToWaiting() {
            this.currentSession = null;
            this.sessionIdInput = '';
        },
    },
    beforeUnmount() {
        if (this.statsInterval) clearInterval(this.statsInterval);
        if (this.qrStatsInterval) clearInterval(this.qrStatsInterval);
        if (ws) ws.close();
    },
};
</script>

<style scoped>
#presenter-app {
    padding: 20px;
    max-width: 1200px;
    margin: 0 auto;
}

.presenter-container h1 {
    margin-bottom: 30px;
}

.controls-section {
    display: flex;
    gap: 10px;
}

.session-info h2 {
    margin: 0;
    margin-bottom: 15px;
}

.sessions-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 15px;
    margin-top: 20px;
}

.session-card {
    border: 1px solid #ddd;
    border-radius: 8px;
    padding: 15px;
    background: #f9f9f9;
    cursor: pointer;
    transition: all 0.2s ease;
}

.session-card:hover {
    background: #f0f0f0;
    border-color: #999;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.session-card h3 {
    margin: 0 0 10px 0;
    color: #333;
}

.session-card p {
    margin: 5px 0;
    font-size: 14px;
    color: #666;
}

.session-stats {
    display: flex;
    gap: 15px;
    margin: 10px 0;
    font-size: 13px;
}

.session-actions {
    display: flex;
    gap: 10px;
    margin-top: 12px;
}

.session-actions button {
    flex: 1;
    padding: 8px 12px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
    font-weight: 600;
    transition: all 0.2s ease;
}

.join-btn {
    background: #4caf50;
    color: white;
}

.join-btn:hover {
    background: #45a049;
}

.restart-btn {
    background: #ff9800;
    color: white;
}

.restart-btn:hover {
    background: #fb8c00;
}

.delete-btn {
    background: #f44336;
    color: white;
}

.delete-btn:hover {
    background: #da190b;
}

.sessions-empty {
    color: #999;
    font-style: italic;
    margin-top: 20px;
}

.sessions-loading {
    color: #666;
    margin-top: 20px;
}
</style>
