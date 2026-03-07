<template>
    <div>
        <div class="code-container">
            <pre class="language-java"><code class="language-java" v-html="highlightedCode"></code></pre>
        </div>

        <div v-if="quizMode === 'sizes' && question.useCompactHeaders !== undefined" class="mode-hint">
            <span class="subtle">Answer uses</span>
            <span class="pill">Compact headers: {{ question.useCompactHeaders ? 'ON' : 'OFF' }}</span>
        </div>

        <div class="solution-container">
            <div class="solution-header" v-if="selectedAnswer !== null">
                <strong v-if="isCorrect" class="correct-text">✓ Correct!</strong>
                <strong v-else class="wrong-text">✗ Wrong</strong>
            </div>
            <div class="solution-header" v-else>
                <strong class="missed-text">— You didn't answer</strong>
            </div>

            <div class="solution-answer">
                <span class="label">The answer is:</span>
                <span class="answer">{{ formatOption(question.correct) }}</span>
            </div>

            <div v-if="selectedAnswer !== null" class="solution-your-answer">
                <span class="label">You answered:</span>
                <span :class="{ correct: isCorrect, wrong: !isCorrect }">
                    {{ formatOption(selectedAnswer) }}
                </span>
            </div>

            <div class="solution-score">
                🏆 Your score: <strong>{{ score }}</strong>
                <span v-if="bonus > 0" class="speed-bonus">⚡ +{{ bonus }} speed bonus!</span>
            </div>
        </div>
    </div>
</template>

<script>
import Prism from 'prismjs';
import 'prismjs/components/prism-java';

export default {
    props: {
        question: {
            type: Object,
            required: true,
        },
        isCorrect: {
            type: Boolean,
            required: true,
        },
        selectedAnswer: {
            type: [Number, String],
            default: null,
        },
        quizMode: {
            type: String,
            default: 'java',
        },
        score: {
            type: Number,
            default: 0,
        },
        bonus: {
            type: Number,
            default: 0,
        },
    },
    computed: {
        highlightedCode() {
            if (!this.question) return '';
            try {
                return Prism.highlight(this.question.code, Prism.languages.java, 'java');
            } catch {
                return this.question.code;
            }
        },
    },
    methods: {
        formatVersion(v) {
            if (v === -3) return '1.0-α1';
            if (v === -2) return '1.0-α2';
            if (v === -1) return '1.0-α3';
            if (v === 0) return '1.0';
            if (v >= 1 && v <= 4) return '1.' + v;
            return String(v);
        },
        formatOption(option) {
            if (this.quizMode === 'sizes') {
                return option + ' B';
            }
            return `Java ${this.formatVersion(option)}`;
        },
    },
};
</script>

<style scoped>
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

.solution-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin: 20px 0;
    padding: 16px;
    background: var(--bg-section);
    border-left: 4px solid var(--accent);
    border-radius: 4px;
}

.solution-header {
    font-size: 1.1em;
    font-weight: bold;
    color: var(--text-primary);
}

.correct-text {
    color: var(--success);
}

.wrong-text {
    color: var(--danger);
}

.missed-text {
    color: var(--text-muted);
}

.solution-answer,
.solution-your-answer {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 0.95em;
}

.label {
    font-weight: 500;
    color: var(--text-secondary);
    min-width: 120px;
}

.answer {
    font-weight: bold;
    font-size: 1.05em;
    color: var(--accent);
}

.solution-your-answer .correct {
    color: var(--success);
    font-weight: bold;
}

.solution-your-answer .wrong {
    color: var(--danger);
    font-weight: bold;
}

.solution-score {
    margin-top: 8px;
    padding-top: 10px;
    border-top: 1px solid var(--border-separator);
    font-size: 1.05em;
    color: var(--text-primary);
}

.speed-bonus {
    margin-left: 8px;
    color: var(--warning);
    font-weight: bold;
}
</style>
