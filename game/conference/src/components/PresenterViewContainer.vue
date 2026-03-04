<template>
    <div id="presenter-view-container">
        <template v-if="!isAuthenticated">
            <div
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
        </template>

        <template v-else>
            <PresenterViewGame
                v-if="currentSession"
                :session-id="sessionId"
                :auth-secret="authSecret"
                @back="goBack"
            />
            <div v-else style="text-align: center; padding: 40px">
                <p>Loading session...</p>
            </div>
        </template>
    </div>
</template>

<script>
import PresenterViewGame from './PresenterViewGame.vue';

export default {
    components: {
        PresenterViewGame,
    },
    data() {
        return {
            isAuthenticated: false,
            authSecret: '',
            authError: '',
            sessionId: '',
            sessionSlug: '',
            currentSession: null,
        };
    },
    mounted() {
        this.checkAuthFromCookie();
        this.getSessionFromUrl();
        if (this.isAuthenticated && this.sessionSlug) {
            this.resolveSessionId().then(() => {
                if (this.sessionId) {
                    this.loadSession();
                }
            });
        }
    },
    methods: {
        checkAuthFromCookie() {
            const cookie = document.cookie
                .split('; ')
                .find((row) => row.startsWith('presenter_auth='));
            if (cookie) {
                this.authSecret = cookie.split('=')[1];
                this.isAuthenticated = true;
            }
        },
        getSessionFromUrl() {
            // Extract sessionId from URL path: /presenter/view/:sessionId
            const pathSegments = window.location.pathname.split('/');
            const viewIndex = pathSegments.indexOf('view');
            if (viewIndex >= 0 && pathSegments[viewIndex + 1]) {
                this.sessionSlug = decodeURIComponent(pathSegments[viewIndex + 1]);
                return;
            }
            // Fallback to query param for backward compatibility
            const params = new URLSearchParams(window.location.search);
            this.sessionSlug = params.get('session') || '';
        },
        async resolveSessionId() {
            if (!this.sessionSlug) return;
            try {
                const res = await fetch('/admin/sessions', {
                    headers: {
                        'x-admin-secret': this.authSecret,
                    },
                });
                if (res.ok) {
                    const data = await res.json();
                    const sessions = data.sessions || [];
                    const matched = sessions.find((s) => s.sessionId === this.sessionSlug) ||
                        sessions.find((s) =>
                            (s.name || '').toLowerCase() === this.sessionSlug.toLowerCase()
                        );
                    if (matched) {
                        this.sessionId = matched.sessionId;
                        return;
                    }
                }
            } catch (e) {
                console.error('Failed to resolve session id', e);
            }
            await this.createSessionFromSlug();
        },
        async createSessionFromSlug() {
            try {
                const res = await fetch('/session/create', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'x-admin-secret': this.authSecret,
                    },
                    body: JSON.stringify({ name: this.sessionSlug }),
                });
                if (!res.ok) {
                    console.error('Failed to create session from slug');
                    return;
                }
                const data = await res.json();
                this.sessionId = data.sessionId;
                localStorage.setItem(
                    'presenter_session',
                    JSON.stringify({ sessionId: data.sessionId })
                );
            } catch (e) {
                console.error('Failed to create session from slug', e);
            }
        },
        setAuthCookie(secret) {
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
            // Load the session after authentication
            if (this.sessionSlug) {
                await this.resolveSessionId();
                if (this.sessionId) {
                    await this.loadSession();
                }
            }
        },
        async loadSession() {
            try {
                const res = await fetch(`/session/${this.sessionId}`, {
                    headers: {
                        'x-admin-secret': this.authSecret,
                    },
                });

                if (!res.ok) {
                    alert('Failed to load session');
                    return;
                }

                this.currentSession = await res.json();
            } catch (e) {
                console.error('Failed to load session', e);
                alert('Error loading session');
            }
        },
        goBack() {
            window.location.href = '/presenter';
        },
    },
    beforeUnmount() {
        // Cleanup if needed
    },
};
</script>

<style scoped>
#presenter-view-container {
    /* Layout is handled by shared.css #app */
}
</style>
