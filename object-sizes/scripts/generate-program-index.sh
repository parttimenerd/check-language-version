#!/usr/bin/env bash
set -euo pipefail

# Generates a newline-delimited list of fully-qualified class names (FQCNs)
# for all *top-level* Java sources in package me.bechberger.sizes.programs
# and any of its subpackages.
#
# Writes to:
#   - build output directory (target/classes) so it is packaged as a resource
#   - optionally also to src/main/resources (so it is checked/visible in sources)

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SRC_DIR="${1:-${ROOT_DIR}/src/main/java}"
OUT_CLASSES_DIR="${2:-${ROOT_DIR}/target/classes}"
OUT_RESOURCES_DIR="${3:-${ROOT_DIR}/src/main/resources}"

PROGRAMS_DIR_REL="me/bechberger/sizes/programs"
PROGRAMS_DIR="${SRC_DIR}/${PROGRAMS_DIR_REL}"

OUT_FILE_CLASSES="${OUT_CLASSES_DIR}/${PROGRAMS_DIR_REL}.index"
OUT_FILE_RESOURCES="${OUT_RESOURCES_DIR}/${PROGRAMS_DIR_REL}.index"

mkdir -p "$(dirname "${OUT_FILE_CLASSES}")" "$(dirname "${OUT_FILE_RESOURCES}")"

# If there are no programs yet, create empty index files.
if [[ ! -d "${PROGRAMS_DIR}" ]]; then
  : > "${OUT_FILE_CLASSES}"
  : > "${OUT_FILE_RESOURCES}"
  exit 0
fi

# Top-level classes only:
# - derive from *.java files (nested classes only exist as $ in .class output)
# - exclude package-info.java and module-info.java
# - include subpackages under me.bechberger.sizes.programs
# - exclude support classes (program helpers that are not runnable cases)
# - sort for deterministic output
INDEX_CONTENT=$(
  cd "${SRC_DIR}"
  find "${PROGRAMS_DIR_REL}" -type f -name "*.java" \
    ! -name "package-info.java" \
    ! -name "module-info.java" \
    ! -path "${PROGRAMS_DIR_REL}/support/*" \
    -print \
    | sed -e 's#\.java$##' -e 's#/#.#g' \
    | sort -u
)

printf "%s\n" "${INDEX_CONTENT}" > "${OUT_FILE_CLASSES}"
printf "%s\n" "${INDEX_CONTENT}" > "${OUT_FILE_RESOURCES}"