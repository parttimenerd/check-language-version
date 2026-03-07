<template>
    <div class="waiting-screen">
        <div class="waiting-content">
            <h2>👋 Welcome, {{ displayName }}!</h2>
            <p class="waiting-hint">Waiting for the quiz to start...</p>

            <div class="spinner-wrap">
                <div class="spinner"></div>
            </div>

            <p class="session-label">
                Session: <strong>{{ sessionId }}</strong>
            </p>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        displayName: {
            type: String,
            required: true,
        },
        sessionId: {
            type: String,
            required: true,
        },
    },
};
</script>

<style scoped>
.waiting-screen {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 80vh;
    text-align: center;
}

.waiting-content {
    padding: 20px;
}

.waiting-hint {
    color: var(--text-muted);
    margin-bottom: 30px;
}

.spinner-wrap {
    margin: 40px 0;
}

.spinner {
    display: inline-block;
    border: 3px solid var(--spinner-track);
    border-top: 3px solid var(--spinner-indicator);
    border-radius: 50%;
    width: 50px;
    height: 50px;
    animation: spin 1s linear infinite;
}

.session-label {
    color: var(--text-muted);
    font-size: 14px;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}
</style>
