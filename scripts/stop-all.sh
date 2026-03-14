#!/bin/bash

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "🛑 Stopping all services..."

# 1. Stop Backend
echo "Stopping Backend..."
pkill -f "mvn spring-boot:run"
pkill -f "FastFoodApplication"
rm -f "$PROJECT_ROOT/.backend.pid"

# 2. Stop Frontend
echo "Stopping Frontend..."
pkill -f "pnpm dev"
pkill -f "vite"
rm -f "$PROJECT_ROOT/.frontend.pid"

# 3. Stop Database
echo "Stopping Database..."
cd "$PROJECT_ROOT"
docker-compose down

echo "----------------------------------------------------"
echo "✅ All services stopped!"
echo "----------------------------------------------------"
