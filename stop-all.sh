#!/bin/bash

echo "🛑 Stopping all services..."

# 1. Stop Backend
echo "Stopping Backend..."
pkill -f "mvn spring-boot:run"
pkill -f "FastFoodApplication"
rm -f .backend.pid

# 2. Stop Frontend
echo "Stopping Frontend..."
pkill -f "pnpm dev"
pkill -f "vite"
rm -f .frontend.pid

# 3. Stop Database
echo "Stopping Database..."
docker-compose down

echo "----------------------------------------------------"
echo "✅ All services stopped!"
echo "----------------------------------------------------"
