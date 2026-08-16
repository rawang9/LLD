#!/usr/bin/env bash
# Start/stop the local Redis Podman pod used for atomic Lua rate limiting.
# Usage: ./redis-pod.sh start | stop | status
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
POD_NAME="rate-limit-redis"
CTR_NAME="rate-limit-redis-server"
IMAGE="localhost/rate-limit-redis:local"
HOST_PORT="${REDIS_PORT:-6380}"

usage() {
  echo "Usage: $0 start|stop|status"
  echo "  REDIS_PORT  host port (default 6380; 6379 is often taken by a local redis-server)"
  exit 1
}

pod_exists() {
  podman pod exists "$POD_NAME"
}

build_image() {
  podman build -t "$IMAGE" -f "$SCRIPT_DIR/Dockerfile" "$SCRIPT_DIR"
}

start() {
  build_image
  if pod_exists; then
    echo "Pod $POD_NAME already exists — starting it"
    podman pod start "$POD_NAME" >/dev/null
  else
    echo "Creating pod $POD_NAME on 127.0.0.1:${HOST_PORT}"
    podman pod create \
      --name "$POD_NAME" \
      --publish "127.0.0.1:${HOST_PORT}:6379"
    mkdir -p "$SCRIPT_DIR/lua"
    podman run -d \
      --pod "$POD_NAME" \
      --name "$CTR_NAME" \
      "$IMAGE" >/dev/null
  fi
  if podman container exists "$CTR_NAME" 2>/dev/null; then
    podman cp "$SCRIPT_DIR/lua/." "${CTR_NAME}:/lua"
  fi
  echo "Redis listening on 127.0.0.1:${HOST_PORT}"
  podman pod ps --filter "name=${POD_NAME}"
}

stop() {
  if ! pod_exists; then
    echo "Pod $POD_NAME is not running"
    return 0
  fi
  echo "Stopping and removing pod $POD_NAME"
  podman pod stop "$POD_NAME" >/dev/null
  podman pod rm "$POD_NAME" >/dev/null
  echo "Stopped"
}

status() {
  if ! pod_exists; then
    echo "Pod $POD_NAME: not created"
    exit 1
  fi
  podman pod ps --filter "name=${POD_NAME}"
  podman ps --filter "pod=${POD_NAME}"
}

case "${1:-}" in
  start) start ;;
  stop) stop ;;
  status) status ;;
  *) usage ;;
esac
