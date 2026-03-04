<template>
    <div>
        <div class="code-container">
            <pre class="language-java"><code class="language-java" v-html="highlightedCode"></code></pre>
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
                return String(option);
            }
            return `Java ${this.formatVersion(option)}`;
        },
    },
};
</script>

<style scoped>
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

.solution-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin: 20px 0;
    padding: 16px;
    background: #f8f9fa;
    border-left: 4px solid #007bff;
    border-radius: 4px;
}

.solution-header {
    font-size: 1.1em;
    font-weight: bold;
    color: #333;
}

.correct-text {
    color: #28a745;
}

.wrong-text {
    color: #dc3545;
}

.missed-text {
    color: #6c757d;
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
    color: #666;
    min-width: 120px;
}

.answer {
    font-weight: bold;
    font-size: 1.05em;
    color: #007bff;
}

.solution-your-answer .correct {
    color: #28a745;
    font-weight: bold;
}

.solution-your-answer .wrong {
    color: #dc3545;
    font-weight: bold;
}

.solution-score {
    margin-top: 8px;
    padding-top: 10px;
    border-top: 1px solid #dee2e6;
    font-size: 1.05em;
    color: #333;
}
</style>
