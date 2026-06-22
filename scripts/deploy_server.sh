#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
SERVER_HOST="${BADLAND_SERVER_HOST:?Set BADLAND_SERVER_HOST in the environment}"
SERVER_USER="${BADLAND_SERVER_USER:-root}"
REMOTE_DIR="${BADLAND_REMOTE_DIR:-/root/badland}"
JAR_NAME="BadLand-1.0-SNAPSHOT-all.jar"
LOCAL_JAR="${REPO_ROOT}/target/${JAR_NAME}"

build_jar() {
  (
    cd "${REPO_ROOT}"
    mvn -q -DskipTests package
  )
}

ensure_local_jar() {
  if [[ ! -f "${LOCAL_JAR}" ]]; then
    echo "Missing ${LOCAL_JAR}. Building deployment jar..."
    build_jar
  fi
}

upload_jar() {
  ssh "${SERVER_USER}@${SERVER_HOST}" "mkdir -p '${REMOTE_DIR}'"
  scp "${LOCAL_JAR}" "${SERVER_USER}@${SERVER_HOST}:${REMOTE_DIR}/${JAR_NAME}.tmp"
  ssh "${SERVER_USER}@${SERVER_HOST}" "mv '${REMOTE_DIR}/${JAR_NAME}.tmp' '${REMOTE_DIR}/${JAR_NAME}'"
}

restart_server() {
  ssh "${SERVER_USER}@${SERVER_HOST}" "pkill -f '${JAR_NAME} --server' || true"
  ssh "${SERVER_USER}@${SERVER_HOST}" "cd '${REMOTE_DIR}' && nohup java -jar '${JAR_NAME}' --server > server.log 2>&1 < /dev/null &"
}

show_help() {
  cat <<'EOF'
Usage: scripts/deploy_server.sh [--skip-build] [--restart]

  --skip-build   Reuse an existing target/BadLand-1.0-SNAPSHOT-all.jar
  --restart      Restart the dedicated server after upload
EOF
}

SKIP_BUILD=false
RESTART=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --skip-build)
      SKIP_BUILD=true
      shift
      ;;
    --restart)
      RESTART=true
      shift
      ;;
    -h|--help)
      show_help
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      show_help
      exit 1
      ;;
  esac
done

if [[ "${SKIP_BUILD}" == "false" ]]; then
  build_jar
else
  ensure_local_jar
fi

upload_jar

if [[ "${RESTART}" == "true" ]]; then
  restart_server
fi

echo "Deployment complete: ${SERVER_USER}@${SERVER_HOST}:${REMOTE_DIR}/${JAR_NAME}"
