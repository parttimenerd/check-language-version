<template>
    <div>
        <div class="stats" v-if="timerRemaining > 0">
            <span>⏱️ Time remaining</span>
            <span :style="{ color: timerRemaining <= 5 ? '#dc3545' : 'inherit' }">
                <strong>{{ timerRemaining }}s</strong>
            </span>
        </div>

        <div class="code-container">
            <pre class="language-java"><code class="language-java" v-html="highlightedCode"></code></pre>
        </div>

        <div v-if="quizMode === 'sizes' && question.useCompactHeaders !== undefined" class="mode-hint">
            <span class="subtle">Answer uses</span>
            <span class="pill">Compact headers: {{ question.useCompactHeaders ? 'ON' : 'OFF' }}</span>
        </div>

        <div class="options">
            <button
                v-for="option in options"
                :key="option"
                @click="submitAnswer(option)"
                :disabled="hasAnswered"
                :class="{
                    correct: hasAnswered && option === question.correct,
                    wrong: hasAnswered && option === selectedAnswer && option !== question.correct,
                }"
            >
                {{ formatOption(option) }}
            </button>
        </div>

        <div v-if="hasAnswered" class="feedback" :class="{ wrong: !isCorrect }">
            <strong>{{ isCorrect ? '✓ Correct!' : '✗ Wrong!' }}</strong>
            <span v-if="!isCorrect"> The answer was {{ formatOption(question.correct) }}</span>
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
        timerRemaining: {
            type: Number,
            required: true,
        },
        hasAnswered: {
            type: Boolean,
            required: true,
        },
        isCorrect: {
            type: Boolean,
            default: false,
        },
        selectedAnswer: {
            type: [Number, String],
            default: null,
        },
        onAnswer: {
            type: Function,
            required: true,
        },
        quizMode: {
            type: String,
            default: 'java',
        },
        serverAnswers: {
            type: Array,
            default: () => [],
        },
    },
    computed: {
        options() {
            if (!this.question) return [];
            // Use server-provided answers if available
            if (this.serverAnswers && this.serverAnswers.length > 0) {
                return this.serverAnswers;
            }
            if (this.quizMode === 'sizes') {
                return this.question.answers || [];
            }
            // For Java quiz: generate deterministic options using question code as seed
            const correct = this.question.correct;
            const codeHash = this.question.code.split('').reduce((h, c) => {
                return (h << 5) - h + c.charCodeAt(0);
            }, 0);
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
        submitAnswer(option) {
            if (this.hasAnswered) return;
            this.onAnswer(option);
        },
    },
};
</script>
