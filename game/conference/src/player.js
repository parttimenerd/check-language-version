// Player entry point for webpack
import { createApp } from 'vue';
import PlayerApp from './components/PlayerApp.vue';
import Prism from 'prismjs';
import 'prismjs/components/prism-java';
import 'prismjs/themes/prism-tomorrow.css';
import './shared.css';

// Initialize Prism for syntax highlighting
window.Prism = Prism;

createApp(PlayerApp).mount('#app');
