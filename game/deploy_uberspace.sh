#!/usr/bin/env bash
# deploy_uberspace.sh – Deploy (or update) the conference quiz on Uberspace
#
# Usage:
#   ./game/deploy_uberspace.sh --secret <password> [options]
#
# Required:
#   --secret SECRET      Admin / presenter password
#   --web-path PATH      Uberspace web backend path (e.g. /conference-game)
#
# Options:
#   --host HOST          SSH destination (default: uqudy@helike.uberspace.de)
#   --port PORT          Server port on Uberspace (default: 3003)
#   --folder DIR         Remote install directory (default: ~/java-conference-game)
#   --domain DOMAIN      Public domain for base URL (default: mostlynerdless.de)
#   --base-url URL       Full public base URL (overrides --domain derivation)
#   --service-name NAME  Supervisord program prefix (default: java-quiz)
#   --skip-generate      Skip local quiz JSON generation (reuse existing)
#   --node-version VER   Node.js version to set on Uberspace (default: 20)
#   --dry-run            Show what would be done without executing
#   -h, --help           Show this help message

set -euo pipefail

# ── Defaults ──────────────────────────────────────────────────────────────────
HOST="uqudy@helike.uberspace.de"
ADMIN_SECRET=""
PORT=3003
REMOTE_DIR="~/java-conference-game"
DOMAIN="mostlynerdless.de"
BASE_URL=""
WEB_PATH=""
SERVICE_NAME="java-quiz"
SKIP_GENERATE=false
NODE_VERSION="20"
DRY_RUN=false

# ── Helpers ───────────────────────────────────────────────────────────────────
usage() {
  grep '^#' "$0" | grep -v '^#!/' | sed 's/^# \?//'
  exit 0
}

info()  { echo "[INFO]  $*"; }
warn()  { echo "[WARN]  $*" >&2; }
error() { echo "[ERROR] $*" >&2; exit 1; }

run() {
  if $DRY_RUN; then
    echo "[DRY]   $*"
  else
    "$@"
  fi
}

# ── Argument parsing ──────────────────────────────────────────────────────────
while [[ $# -gt 0 ]]; do
  case "$1" in
    --host)            HOST="$2"; shift ;;
    --secret)          ADMIN_SECRET="$2"; shift ;;
    --port)            PORT="$2"; shift ;;
    --remote-dir|--folder) REMOTE_DIR="$2"; shift ;;
    --domain)          DOMAIN="$2"; shift ;;
    --base-url)        BASE_URL="$2"; shift ;;
    --web-path)        WEB_PATH="$2"; shift ;;
    --service-name)    SERVICE_NAME="$2"; shift ;;
    --skip-generate)   SKIP_GENERATE=true ;;
    --node-version)    NODE_VERSION="$2"; shift ;;
    --dry-run)         DRY_RUN=true ;;
    -h|--help)         usage ;;
    *) error "Unknown option: $1 (use --help for usage)" ;;
  esac
  shift
done

# ── Validate required args ────────────────────────────────────────────────────
[[ -n "$ADMIN_SECRET" ]] || error "--secret is required (e.g. --secret my-presenter-password)"
[[ -n "$WEB_PATH" ]]     || error "--web-path is required (e.g. --web-path /conference-game). Use a subpath to avoid overriding your main website!"

# Derive base URL from domain if not provided
if [[ -z "$BASE_URL" ]]; then
  BASE_URL="https://${DOMAIN}${WEB_PATH}/"
  info "Derived base URL: ${BASE_URL}"
fi

# ── Locate project roots ─────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
GAME_DIR="${SCRIPT_DIR}"
CONF_DIR="${GAME_DIR}/conference"

[[ -f "${CONF_DIR}/package.json" ]] || error "conference/package.json not found – is the repo complete?"

# ── Step 1: Generate quiz JSON locally ────────────────────────────────────────
if $SKIP_GENERATE; then
  info "Skipping quiz JSON generation (--skip-generate)"
else
  info "Generating quiz JSON files locally…"
  cd "${PROJECT_ROOT}"
  python3 game/generate_quiz.py --game both --base-url "${BASE_URL}"
  info "Quiz JSON generated."
fi

# ── Step 2: Check for existing deployment ─────────────────────────────────────
info "Checking for existing deployment on ${HOST}…"
IS_UPDATE=false
if ssh "${HOST}" "test -d ${REMOTE_DIR}/game/conference" 2>/dev/null; then
  IS_UPDATE=true
  info "Existing installation found – this will be an UPDATE."
else
  info "No existing installation – this will be a FRESH deploy."
fi

# ── Step 3: Upload files via rsync ────────────────────────────────────────────
info "Uploading project files to ${HOST}:${REMOTE_DIR}/ …"

# Ensure the remote directory structure exists before rsync
run ssh "${HOST}" "mkdir -p ${REMOTE_DIR}/game/conference"

# We upload only what's needed on the server:
#   game/conference/   – server, webpack config, frontend source, public assets
#   game/run_game.sh   – supervisor management
#   game/generate_quiz.py – needed by run_game.sh (existence check)
# We explicitly exclude heavy/unnecessary dirs.

cd "${PROJECT_ROOT}"
run rsync -avz \
  --exclude 'node_modules/' \
  --exclude '.git/' \
  --exclude '__pycache__/' \
  --exclude '.DS_Store' \
  --exclude 'data/' \
  --exclude '*.log' \
  game/conference/ \
  "${HOST}:${REMOTE_DIR}/game/conference/"

# Upload the helper scripts and quiz generator (thin)
run rsync -avz \
  game/run_game.sh \
  game/generate_quiz.py \
  game/alpha_features.json \
  game/alpha1_features.json \
  "${HOST}:${REMOTE_DIR}/game/"

info "Upload complete."

# ── Step 4: Remote setup / update ─────────────────────────────────────────────
if $IS_UPDATE; then
  info "Restarting services on ${HOST}…"
  run ssh "${HOST}" bash -l <<REMOTE_UPDATE
set -euo pipefail

cd ${REMOTE_DIR}/game/conference

echo "[REMOTE] Installing npm dependencies…"
npm install

echo "[REMOTE] Building webpack bundles…"
npm run build

echo "[REMOTE] Restarting supervisord services…"
supervisorctl restart ${SERVICE_NAME}-server 2>/dev/null || true
supervisorctl restart ${SERVICE_NAME}-webpack 2>/dev/null || true
supervisorctl status
echo "[REMOTE] Update complete."
REMOTE_UPDATE
else
  info "Setting up fresh installation on ${HOST}…"
  run ssh "${HOST}" bash -l <<REMOTE_SETUP
set -euo pipefail

# Ensure Node.js is available at the right version
echo "[REMOTE] Setting Node.js version to ${NODE_VERSION}…"
uberspace tools version use node ${NODE_VERSION} 2>/dev/null || echo "(node version may already be set)"
echo "[REMOTE] Node: \$(node --version)"

cd ${REMOTE_DIR}/game

# Make run_game.sh executable
chmod +x run_game.sh

# Install npm deps + set up supervisor daemons
echo "[REMOTE] Running run_game.sh --setup-supervisor…"
./run_game.sh --setup-supervisor \
  --secret "${ADMIN_SECRET}" \
  --port ${PORT} \
  --base-path "${WEB_PATH}" \
  --service-name "${SERVICE_NAME}" \
  --skip-generate

# Set up Uberspace web backend to route traffic to our port
echo "[REMOTE] Configuring web backend (${WEB_PATH} → port ${PORT})…"
uberspace web backend set "${WEB_PATH}" --http --port ${PORT}

echo ""
echo "======================================"
echo "  Deployment complete!"
echo "======================================"
echo ""
echo "  Player:    ${BASE_URL}"
echo "  Presenter: ${BASE_URL}presenter"
echo "  Secret:    ${ADMIN_SECRET}"
echo ""
echo "  Supervisor: supervisorctl status"
echo "  Logs:       supervisorctl tail -f ${SERVICE_NAME}-server"
echo ""
REMOTE_SETUP
fi

info "Done! 🎉"
if $IS_UPDATE; then
  info "Services restarted."
else
  info "Fresh deployment complete."
  info "  Player:    ${BASE_URL}"
  info "  Presenter: ${BASE_URL}presenter"
fi
