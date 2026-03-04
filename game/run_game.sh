#!/usr/bin/env bash
# run_game.sh – Build and run the Java Version multiplayer quiz (conference mode)
#
# Usage:
#   ./game/run_game.sh [options]
#
# Modes (mutually exclusive; default is foreground production):
#   --dev                    Foreground dev mode: webpack --watch + nodemon/node
#   --setup-supervisor       Install supervisord services and start them (Uberspace)
#   --teardown-supervisor    Stop and remove supervisord services
#
# Options:
#   --port PORT              Server port (default: 3000)
#   --secret SECRET          Admin/presenter password (default: "changeme")
#   --base-url URL           Base URL for quiz JSON generation
#                            (default: http://localhost:<PORT>/)
#   --service-name NAME      Supervisor program name prefix (default: java-quiz)
#   --supervisor-dir DIR     Directory for .ini files (default: ~/etc/services.d)
#   --skip-generate          Skip running generate_quiz.py
#   --skip-install           Skip npm install
#   -h, --help               Show this help message

set -euo pipefail

# ── Defaults ─────────────────────────────────────────────────────────────────
PORT=3000
ADMIN_SECRET="changeme"
BASE_URL=""
MODE="production"          # production | dev | setup-supervisor | teardown-supervisor
SERVICE_NAME="java-quiz"
SUPERVISOR_DIR="${HOME}/etc/services.d"
SKIP_GENERATE=false
SKIP_INSTALL=false

# ── Helpers ───────────────────────────────────────────────────────────────────
usage() {
  grep '^#' "$0" | grep -v '^#!/' | sed 's/^# \?//'
  exit 0
}

info()  { echo "[INFO]  $*"; }
warn()  { echo "[WARN]  $*" >&2; }
error() { echo "[ERROR] $*" >&2; exit 1; }

# ── Argument parsing ──────────────────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
  case "$1" in
    --dev)                MODE="dev" ;;
    --setup-supervisor)   MODE="setup-supervisor" ;;
    --teardown-supervisor) MODE="teardown-supervisor" ;;
    --port)               PORT="$2"; shift ;;
    --secret)             ADMIN_SECRET="$2"; shift ;;
    --base-url)           BASE_URL="$2"; shift ;;
    --service-name)       SERVICE_NAME="$2"; shift ;;
    --supervisor-dir)     SUPERVISOR_DIR="$2"; shift ;;
    --skip-generate)      SKIP_GENERATE=true ;;
    --skip-install)       SKIP_INSTALL=true ;;
    -h|--help)            usage ;;
    *) error "Unknown option: $1 (use --help for usage)" ;;
  esac
  shift
done

[[ -z "$BASE_URL" ]] && BASE_URL="http://localhost:${PORT}/"

# ── Locate project roots ──────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
GAME_DIR="${SCRIPT_DIR}"
CONF_DIR="${GAME_DIR}/conference"

[[ -f "${GAME_DIR}/generate_quiz.py" ]] || error "generate_quiz.py not found in ${GAME_DIR}"
[[ -f "${CONF_DIR}/package.json" ]]     || error "conference/package.json not found – is the repo complete?"

# ── Teardown shortcut (no build needed) ───────────────────────────────────────
if [[ "$MODE" == "teardown-supervisor" ]]; then
  WP_INI="${SUPERVISOR_DIR}/${SERVICE_NAME}-webpack.ini"
  SV_INI="${SUPERVISOR_DIR}/${SERVICE_NAME}-server.ini"
  info "Stopping supervisor programs…"
  supervisorctl stop "${SERVICE_NAME}-webpack" 2>/dev/null || true
  supervisorctl stop "${SERVICE_NAME}-server"  2>/dev/null || true
  rm -f "${WP_INI}" "${SV_INI}"
  supervisorctl reread
  supervisorctl update
  info "Services removed."
  exit 0
fi

# ── Step 1: Generate quiz JSON ─────────────────────────────────────────────────
if $SKIP_GENERATE; then
  info "Skipping quiz JSON generation (--skip-generate)"
else
  info "Generating quiz JSON files (this may take a moment)…"
  cd "${PROJECT_ROOT}"
  python3 game/generate_quiz.py --game both --base-url "${BASE_URL}"
  info "Quiz JSON generated and copied to conference/public/"
fi

# ── Step 2: npm install ────────────────────────────────────────────────────────
cd "${CONF_DIR}"

if $SKIP_INSTALL; then
  info "Skipping npm install (--skip-install)"
else
  info "Installing npm dependencies…"
  npm install
fi

# ── Detect nodemon ────────────────────────────────────────────────────────────
NODE_CMD="node"
NODEMON_BIN="${CONF_DIR}/node_modules/.bin/nodemon"
if [[ -x "${NODEMON_BIN}" ]]; then
  # Watch server.js and restart on changes; ignore public/ to avoid webpack noise
  NODE_CMD="${NODEMON_BIN} --watch server.js --ignore public/"
  info "nodemon found – server will auto-restart on server.js changes"
elif command -v nodemon &>/dev/null; then
  NODE_CMD="nodemon --watch server.js --ignore public/"
  info "nodemon found (global) – server will auto-restart on server.js changes"
else
  warn "nodemon not found; server will NOT auto-restart on changes (run npm install nodemon to add it)"
fi

# ── Step 3: Mode dispatch ─────────────────────────────────────────────────────
export ADMIN_SECRET
export PORT

case "$MODE" in

  # ── Foreground dev ──────────────────────────────────────────────────────────
  dev)
    info "Building initial bundles…"
    npm run build

    info "Starting webpack in watch mode…"
    npm run build:watch &
    WEBPACK_PID=$!

    cleanup() {
      info "Shutting down…"
      kill "${WEBPACK_PID}" 2>/dev/null || true
    }
    trap cleanup EXIT INT TERM

    # Give webpack a moment to produce the initial bundles.
    sleep 2

    info "Starting server (dev) on port ${PORT}…"
    info "  Player:    http://localhost:${PORT}/"
    info "  Presenter: http://localhost:${PORT}/presenter"
    info "  Secret:    ${ADMIN_SECRET}"

    ${NODE_CMD} server.js
    ;;

  # ── Uberspace / supervisord setup ──────────────────────────────────────────
  setup-supervisor)
    command -v supervisorctl &>/dev/null || error "supervisorctl not found – are you on Uberspace?"

    [[ -d "${SUPERVISOR_DIR}" ]] || mkdir -p "${SUPERVISOR_DIR}"

    WP_INI="${SUPERVISOR_DIR}/${SERVICE_NAME}-webpack.ini"
    SV_INI="${SUPERVISOR_DIR}/${SERVICE_NAME}-server.ini"

    # ── initial production build ──────────────────────────────────────────────
    info "Building frontend bundles (initial production build)…"
    npm run build

    # ── webpack watch daemon ──────────────────────────────────────────────────
    info "Writing ${WP_INI}…"
    cat > "${WP_INI}" <<INIEOF
[program:${SERVICE_NAME}-webpack]
directory=${CONF_DIR}
command=$(npm bin)/webpack --watch
autostart=true
autorestart=true
redirect_stderr=true
stdout_logfile=${HOME}/logs/supervisord/${SERVICE_NAME}-webpack.log
stdout_logfile_maxbytes=5MB
stdout_logfile_backups=3
environment=NODE_ENV="production",PATH="$(npm bin):%(ENV_PATH)s"
INIEOF

    # ── node server daemon ────────────────────────────────────────────────────
    info "Writing ${SV_INI}…"
    # Resolve the actual server command (nodemon preferred for auto-restart on code changes)
    if [[ "${NODE_CMD}" != "node" ]]; then
      SERVER_CMD="${NODE_CMD} server.js"
    else
      SERVER_CMD="node server.js"
    fi

    cat > "${SV_INI}" <<INIEOF
[program:${SERVICE_NAME}-server]
directory=${CONF_DIR}
command=${SERVER_CMD}
autostart=true
autorestart=true
redirect_stderr=true
stdout_logfile=${HOME}/logs/supervisord/${SERVICE_NAME}-server.log
stdout_logfile_maxbytes=5MB
stdout_logfile_backups=3
environment=NODE_ENV="production",ADMIN_SECRET="${ADMIN_SECRET}",PORT="${PORT}",PATH="$(npm bin):%(ENV_PATH)s"
INIEOF

    # ── register & start ──────────────────────────────────────────────────────
    mkdir -p "${HOME}/logs/supervisord"
    info "Registering services with supervisord…"
    supervisorctl reread
    supervisorctl update

    info "Services registered and started."
    info ""
    info "  Player:    http://localhost:${PORT}/"
    info "  Presenter: http://localhost:${PORT}/presenter"
    info ""
    info "Useful commands:"
    info "  supervisorctl status"
    info "  supervisorctl restart ${SERVICE_NAME}-server"
    info "  supervisorctl restart ${SERVICE_NAME}-webpack"
    info "  supervisorctl tail -f ${SERVICE_NAME}-server"
    info ""
    info "To remove the services later, run:"
    info "  $0 --teardown-supervisor --service-name ${SERVICE_NAME}"
    ;;

  # ── Foreground production (default) ────────────────────────────────────────
  production)
    info "Building frontend bundles (production)…"
    npm run build

    info "Starting server on port ${PORT}…"
    info "  Player:    http://localhost:${PORT}/"
    info "  Presenter: http://localhost:${PORT}/presenter"
    info "  Secret:    ${ADMIN_SECRET}"

    node server.js
    ;;

esac
