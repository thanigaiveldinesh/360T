#!/usr/bin/env bash
set -e

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
TARGET="$PROJECT_DIR/target"

echo ">>> Building project ..."
cd "$PROJECT_DIR"
mvn clean package -q
echo ">>> Build complete."

MODE="${1:-help}"

if [ "$MODE" = "same" ]; then
    java -jar "$TARGET/player-same-process.jar"

elif [ "$MODE" = "multi" ]; then
    java -jar "$TARGET/player-server.jar" &
    SERVER_PID=$!
    sleep 1
    java -jar "$TARGET/player-client.jar"
    wait "$SERVER_PID" 2>/dev/null || true

else
    echo "Usage: $0 [same|multi]"
    exit 1
fi