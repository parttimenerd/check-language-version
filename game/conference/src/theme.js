/**
 * Theme management.
 *
 * Precedence (highest first):
 *   1. ?theme=dark|light  URL parameter
 *   2. localStorage 'theme' value
 *   3. prefers-color-scheme media query
 *
 * The chosen theme is applied as  data-theme="dark"  on <html>.
 * When absent, the default (light) CSS variables apply.
 */

const STORAGE_KEY = 'theme';

function resolve() {
    const params = new URLSearchParams(window.location.search);
    const fromUrl = params.get('theme');
    if (fromUrl === 'dark' || fromUrl === 'light') return fromUrl;

    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored === 'dark' || stored === 'light') return stored;

    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        return 'dark';
    }
    return 'light';
}

function apply(theme) {
    if (theme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    } else {
        document.documentElement.removeAttribute('data-theme');
    }
}

/** Initialise theme on page load. Returns the active theme string. */
export function initTheme() {
    const theme = resolve();
    apply(theme);
    return theme;
}

/** Toggle between light / dark. Returns the new theme string. */
export function toggleTheme() {
    const current = document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
    const next = current === 'dark' ? 'light' : 'dark';
    apply(next);
    localStorage.setItem(STORAGE_KEY, next);
    return next;
}

/** Get the current active theme. */
export function getTheme() {
    return document.documentElement.getAttribute('data-theme') === 'dark' ? 'dark' : 'light';
}
