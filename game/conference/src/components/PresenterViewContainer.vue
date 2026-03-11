<template>
    <div id="presenter-view-container">
        <template v-if="!isAuthenticated">
            <div class="auth-section">
                <h2>🔐 Presenter Authentication</h2>
                <p class="auth-hint">Enter your admin secret to access presenter controls</p>
                <input
                    v-model="authSecret"
                    type="password"
                    placeholder="Admin Secret"
                    @keyup.enter="authenticate"
                    class="auth-input"
                />
                <p v-if="authError" class="auth-error">{{ authError }}</p>
                <button
                    @click="authenticate"
                    class="auth-btn"
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
import { apiUrl, navUrl } from '../basePath.js';

// Retry wrapper for fetch calls on spotty WiFi
async function fetchWithRetry(url, options = {}, retries = 3, baseDelay = 1000, timeout = 15000) {
    for (let i = 0; i <= retries; i++) {
        // Per-attempt timeout — prevents hung requests on slow WiFi
        const controller = new AbortController();
        const timer = timeout > 0 ? setTimeout(() => controller.abort(), timeout) : null;
        try {
            const res = await fetch(url, { ...options, signal: controller.signal });
            if (timer) clearTimeout(timer);
            // Don't retry 4xx (client errors) — they won't succeed on retry
            if (res.ok || (res.status >= 400 && res.status < 500)) return res;
            // 5xx — server or reverse-proxy glitch (e.g. 502 from nginx),
            // common at conferences. Worth retrying.
            if (i === retries) return res;
        } catch (err) {
            if (timer) clearTimeout(timer);
            if (i === retries) throw err;
        }
        const delay = baseDelay * Math.pow(2, i) * (0.5 + Math.random() * 0.5);
        await new Promise(r => setTimeout(r, delay));
    }
}

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
    async mounted() {
        await this.checkAuthFromUrl();
        if (!this.isAuthenticated) this.checkAuthFromCookie();
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
        async checkAuthFromUrl() {
            const params = new URLSearchParams(window.location.search);
            const secret = params.get('secret') || params.get('password');
            if (secret) {
                this.authSecret = secret;
                try {
                    const res = await fetchWithRetry(apiUrl('/admin/sessions'), {
                        headers: { 'x-admin-secret': secret },
                    });
                    if (res && res.ok) {
                        this.isAuthenticated = true;
                        this.setAuthCookie(secret);
                    } else {
                        this.authError = 'Invalid secret in URL';
                    }
                } catch (e) {
                    console.error('Failed to verify secret from URL', e);
                }
            }
        },
        checkAuthFromCookie() {
            const cookie = document.cookie
                .split('; ')
                .find((row) => row.startsWith('presenter_auth='));
            if (cookie) {
                // Use substring instead of split('=')[1] to handle secrets
                // containing '=' characters. decodeURIComponent reverses the
                // encoding applied by setAuthCookie.
                const raw = cookie.substring(cookie.indexOf('=') + 1);
                try { this.authSecret = decodeURIComponent(raw); } catch { this.authSecret = raw; }
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
                const res = await fetchWithRetry(apiUrl('/admin/sessions'), {
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
                const res = await fetchWithRetry(apiUrl('/session/create'), {
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
            // Verify secret against the server before granting access
            try {
                const res = await fetchWithRetry(apiUrl('/admin/sessions'), {
                    headers: { 'x-admin-secret': this.authSecret },
                });
                if (!res || !res.ok) {
                    this.authError = 'Invalid admin secret';
                    this.isAuthenticated = false;
                    return;
                }
                this.isAuthenticated = true;
                this.setAuthCookie(this.authSecret);
            } catch (e) {
                this.authError = 'Connection error – please try again';
                this.isAuthenticated = false;
                return;
            }
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
                const res = await fetchWithRetry(apiUrl(`/session/${this.sessionId}`), {
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
            window.location.href = navUrl('/presenter');
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
.auth-section {
    max-width: 400px;
    margin: 40px auto;
    padding: 30px;
    background: var(--bg-section);
    border-radius: 8px;
    text-align: center;
}
.auth-hint { color: var(--text-muted); }
.auth-input {
    width: 100%;
    padding: 10px;
    margin-bottom: 10px;
    border: 1px solid var(--border-color);
    border-radius: 4px;
    box-sizing: border-box;
    font-size: 14px;
    background: var(--bg-input);
    color: var(--text-primary);
}
.auth-error { color: var(--danger); margin: 10px 0; }
.auth-btn {
    width: 100%;
    padding: 10px;
    background: var(--accent);
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 16px;
}
</style>
