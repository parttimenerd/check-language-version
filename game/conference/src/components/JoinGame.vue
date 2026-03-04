<template>
    <div class="join-section">
        <h2>Join a Game</h2>
        <input
            v-model="sessionId"
            type="text"
            placeholder="Enter Session ID"
            @keyup.enter="joinGame"
        />
        <button @click="joinGame">Join Game</button>
        <div v-if="errorMsg" class="alert alert-error" style="margin-top: 10px">{{ errorMsg }}</div>
    </div>
</template>

<script>
export default {
    props: {
        onJoin: {
            type: Function,
            required: true,
        },
    },
    data() {
        return {
            sessionId: '',
            errorMsg: '',
        };
    },
    mounted() {
        // Check if sessionId is in URL query params
        const params = new URLSearchParams(window.location.search);
        const sessionParam = params.get('session');
        if (sessionParam) {
            this.sessionId = sessionParam;
            // Auto-join if sessionId provided
            this.$nextTick(() => {
                this.joinGame();
            });
        }
    },
    methods: {
        joinGame() {
            if (!this.sessionId.trim()) {
                this.errorMsg = 'Please enter a Session ID';
                return;
            }
            this.errorMsg = '';
            this.onJoin(this.sessionId.trim());
        },
    },
};
</script>
