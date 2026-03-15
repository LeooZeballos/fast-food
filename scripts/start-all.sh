#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
LOG_DIR="$PROJECT_ROOT/logs"

# Ensure logs directory exists
mkdir -p "$LOG_DIR"

echo "🚀 Starting all services in the background..."

# 1. Start Backend
echo "☕ Starting Backend (port 4080)..."
nohup "$SCRIPT_DIR/start-backend.sh" > "$LOG_DIR/backend.log" 2>&1 &

# 2. Start Frontend
echo "☕ Starting Frontend (port 4000)..."
nohup "$SCRIPT_DIR/start-frontend.sh" > "$LOG_DIR/frontend.log" 2>&1 &

echo "----------------------------------------------------"
echo "✅ All services are starting!"
echo "📖 Backend logs:  tail -f logs/backend.log"
echo "📖 Frontend logs: tail -f logs/frontend.log"
echo "🛑 To stop everything, run: ./scripts/stop-all.sh"
echo "----------------------------------------------------"
