// Presenter entry point for webpack
import { createApp } from 'vue';
import PresenterApp from './components/PresenterApp.vue';
import PresenterViewContainer from './components/PresenterViewContainer.vue';
import Prism from 'prismjs';
import 'prismjs/components/prism-java';
import 'prismjs/themes/prism-tomorrow.css';
import './shared.css';
// Initialize Prism for syntax highlighting
window.Prism = Prism;

// Determine which component to mount based on URL path
const isViewPath = window.location.pathname.includes('/presenter/view');
const RootComponent = isViewPath ? PresenterViewContainer : PresenterApp;

createApp(RootComponent).mount('#app');
