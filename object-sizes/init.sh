#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TOOLS_DIR="${ROOT_DIR}/.tools/jol"
JOL_URL="${JOL_URL:-https://builds.shipilev.net/jol/jol-cli-latest.jar}"
JOL_JAR="${JOL_JAR:-${TOOLS_DIR}/jol-cli-latest.jar}"

mkdir -p "${TOOLS_DIR}"

if [[ -f "${JOL_JAR}" && -s "${JOL_JAR}" ]]; then
  echo "JOL already installed: ${JOL_JAR}"
else
  echo "Installing JOL..."
  echo "  from: ${JOL_URL}"
  echo "  to:   ${JOL_JAR}"

  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "${JOL_URL}" -o "${JOL_JAR}"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "${JOL_URL}" -O "${JOL_JAR}"
  else
    echo "Neither curl nor wget found on PATH" >&2
    exit 2
  fi
fi

# Create a small wrapper script to run JOL easily.
mkdir -p "${ROOT_DIR}/bin"
cat > "${ROOT_DIR}/bin/jol" <<'EOF'
#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
JOL_JAR="${JOL_JAR:-${ROOT_DIR}/.tools/jol/jol-cli-latest.jar}"

if [[ ! -f "${JOL_JAR}" ]]; then
  echo "Missing JOL jar at: ${JOL_JAR}" >&2
  echo "Run ./init.sh first (or set JOL_JAR)" >&2
  exit 1
fi

exec "${JAVA_HOME:-""}/bin/java" -version >/dev/null 2>&1 || true

# Use java from PATH by default.
exec java -jar "${JOL_JAR}" "$@"
EOF
chmod +x "${ROOT_DIR}/bin/jol"