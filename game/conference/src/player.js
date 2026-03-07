// Player entry point for webpack
import { createApp } from 'vue';
import PlayerApp from './components/PlayerApp.vue';
import Prism from 'prismjs';
import 'prismjs/components/prism-java';
import 'prismjs/themes/prism-tomorrow.css';
import './shared.css';
import { initTheme, toggleTheme, getTheme } from './theme.js';

// Initialize theme before mount
initTheme();

// Initialize Prism for syntax highlighting
window.Prism = Prism;

const app = createApp(PlayerApp);
app.config.globalProperties.$toggleTheme = toggleTheme;
app.config.globalProperties.$getTheme = getTheme;
app.mount('#app');
