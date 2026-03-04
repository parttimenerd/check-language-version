<template>
    <div class="result-screen">
        <div class="result-content">
            <div :class="isCorrect ? 'correct-result' : 'incorrect-result'">
                <h2>{{ isCorrect ? '✅ Correct!' : '❌ Incorrect' }}</h2>
                <p v-if="isCorrect" style="color: #28a745; font-size: 18px">+1 Point</p>
                <p v-else style="color: #dc3545; font-size: 14px">
                    Correct answer: <strong>{{ correctAnswerLabel }}</strong>
                </p>
            </div>

            <div v-if="features && features.length > 0" style="margin-top: 20px">
                <h3>📚 Features in the Correct Answer:</h3>
                <div style="display: flex; flex-wrap: wrap; gap: 8px">
                    <span
                        v-for="feature in features"
                        :key="feature"
                        style="
                            background: #e3f2fd;
                            color: #1976d2;
                            padding: 4px 12px;
                            border-radius: 16px;
                            font-size: 12px;
                            font-weight: 500;
                        "
                    >
                        {{ feature }}
                    </span>
                </div>
            </div>

            <div style="margin-top: 30px; color: #6c757d">
                <p>Next question in {{ countdownRemaining }} seconds...</p>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    props: {
        isCorrect: {
            type: Boolean,
            required: true,
        },
        correctAnswerLabel: {
            type: String,
            default: 'A',
        },
        countdownRemaining: {
            type: Number,
            default: 5,
        },
        features: {
            type: Array,
            default: () => [],
        },
    },
};
</script>

<style scoped>
.result-screen {
    display: flex;
    align-items: center;
    justify-content: center;
    min-height: 80vh;
}

.result-content {
    text-align: center;
    padding: 40px;
    background: #f8f9fa;
    border-radius: 8px;
    min-width: 300px;
}

.correct-result {
    padding: 20px;
    background: #d4edda;
    border: 1px solid #c3e6cb;
    border-radius: 4px;
    color: #155724;
}

.incorrect-result {
    padding: 20px;
    background: #f8d7da;
    border: 1px solid #f5c6cb;
    border-radius: 4px;
    color: #721c24;
}
</style>
