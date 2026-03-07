// Base path helper for subpath deployments (e.g. /conference-game)
// The server injects window.__BASE_PATH into the HTML <head>.

/**
 * Return the base path prefix (e.g. '/conference-game' or '').
 */
export function getBasePath() {
    return window.__BASE_PATH || '';
}

/**
 * Prefix an API path with the base path.
 *   apiUrl('/admin/sessions')  →  '/conference-game/admin/sessions'
 */
export function apiUrl(path) {
    return getBasePath() + path;
}

/**
 * Build a full WebSocket URL with the base path.
 *   wsUrl('/ws?uuid=abc')  →  'wss://host/conference-game/ws?uuid=abc'
 */
export function wsUrl(path) {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    return `${protocol}//${window.location.host}${getBasePath()}${path}`;
}

/**
 * Build a navigation URL with the base path.
 *   navUrl('/presenter')  →  '/conference-game/presenter'
 */
export function navUrl(path) {
    return getBasePath() + path;
}
