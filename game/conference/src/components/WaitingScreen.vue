<template>
    <div class="waiting-screen">
        <div class="waiting-content">
            <h2>👋 Welcome, {{ displayName }}!</h2>
            <p style="color: #6c757d; margin-bottom: 30px">Waiting for the quiz to start...</p>

            <div style="margin: 40px 0">
                <div
                    class="spinner"
                    style="
                        display: inline-block;
                        border: 3px solid #e9ecef;
                        border-top: 3px solid #007bff;
                        border-radius: 50%;
                        width: 50px;
                        height: 50px;
                        animation: spin 1s linear infinite;
                    "
                ></div>
            </div>

            <p style="color: #6c757d; font-size: 14px">
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

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}
</style>
