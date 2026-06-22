#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
SERVER_HOST="${BADLAND_SERVER_HOST:?Set BADLAND_SERVER_HOST in the environment}"
SERVER_USER="${BADLAND_SERVER_USER:-root}"
REMOTE_DIR="${BADLAND_REMOTE_DIR:-/root/badland}"
REMOTE_NAKAMA_DIR="${REMOTE_DIR}/nakama"

require_local_files() {
  [[ -f "${REPO_ROOT}/docker-compose.nakama.yml" ]] || {
    echo "Missing docker-compose.nakama.yml" >&2
    exit 1
  }
  [[ -f "${REPO_ROOT}/nakama/nakama.yml" ]] || {
    echo "Missing nakama/nakama.yml" >&2
    exit 1
  }
}

upload_files() {
  ssh "${SERVER_USER}@${SERVER_HOST}" "mkdir -p '${REMOTE_NAKAMA_DIR}'"
  scp "${REPO_ROOT}/docker-compose.nakama.yml" "${SERVER_USER}@${SERVER_HOST}:${REMOTE_DIR}/docker-compose.nakama.yml.tmp"
  scp "${REPO_ROOT}/nakama/nakama.yml" "${SERVER_USER}@${SERVER_HOST}:${REMOTE_NAKAMA_DIR}/nakama.yml.tmp"
  ssh "${SERVER_USER}@${SERVER_HOST}" "\
    mv '${REMOTE_DIR}/docker-compose.nakama.yml.tmp' '${REMOTE_DIR}/docker-compose.nakama.yml' && \
    mv '${REMOTE_NAKAMA_DIR}/nakama.yml.tmp' '${REMOTE_NAKAMA_DIR}/nakama.yml'"
}

start_stack() {
  ssh "${SERVER_USER}@${SERVER_HOST}" "\
    cd '${REMOTE_DIR}' && \
    docker compose -f docker-compose.nakama.yml up -d"
}

stop_stack() {
  ssh "${SERVER_USER}@${SERVER_HOST}" "\
    cd '${REMOTE_DIR}' && \
    docker compose -f docker-compose.nakama.yml down"
}

show_logs() {
  ssh "${SERVER_USER}@${SERVER_HOST}" "\
    cd '${REMOTE_DIR}' && \
    docker compose -f docker-compose.nakama.yml logs --tail=100 nakama postgres"
}

show_help() {
  cat <<'EOF'
Usage: scripts/deploy_nakama.sh [command]

Commands:
  deploy   Upload compose/config and start Nakama stack (default)
  start    Start existing remote stack
  stop     Stop remote stack
  logs     Show recent remote logs
EOF
}

COMMAND="${1:-deploy}"

case "${COMMAND}" in
  deploy)
    require_local_files
    upload_files
    start_stack
    ;;
  start)
    start_stack
    ;;
  stop)
    stop_stack
    ;;
  logs)
    show_logs
    ;;
  -h|--help|help)
    show_help
    exit 0
    ;;
  *)
    echo "Unknown command: ${COMMAND}" >&2
    show_help
    exit 1
    ;;
esac

echo "Nakama command '${COMMAND}' completed on ${SERVER_USER}@${SERVER_HOST}"
