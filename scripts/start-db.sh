#!/bin/bash
# Start the PostgreSQL database container

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

echo "Starting PostgreSQL container..."
cd "$PROJECT_ROOT"
docker-compose up -d db
echo "Database container is starting. You can check status with 'docker-compose ps'."
