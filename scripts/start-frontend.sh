#!/bin/bash
# Start the Vite + React frontend

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "Starting frontend with pnpm..."
cd "$PROJECT_ROOT/frontend"
pnpm dev
