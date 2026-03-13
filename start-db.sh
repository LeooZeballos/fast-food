#!/bin/bash
# Start the PostgreSQL database container
echo "Starting PostgreSQL container..."
docker-compose up -d db
echo "Database container is starting. You can check status with 'docker-compose ps'."
