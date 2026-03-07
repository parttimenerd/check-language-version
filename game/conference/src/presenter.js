// Presenter entry point for webpack
import { createApp } from 'vue';
import PresenterApp from './components/PresenterApp.vue';
import PresenterViewContainer from './components/PresenterViewContainer.vue';
import Prism from 'prismjs';
import 'prismjs/components/prism-java';
import 'prismjs/themes/prism-tomorrow.css';
import './shared.css';
import { initTheme, toggleTheme, getTheme } from './theme.js';

// Initialize theme before mount
initTheme();

// Initialize Prism for syntax highlighting
window.Prism = Prism;

// Determine which component to mount based on URL path
const isViewPath = window.location.pathname.includes('/presenter/view');
const RootComponent = isViewPath ? PresenterViewContainer : PresenterApp;

const app = createApp(RootComponent);
app.config.globalProperties.$toggleTheme = toggleTheme;
app.config.globalProperties.$getTheme = getTheme;
app.mount('#app');
