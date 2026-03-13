#!/bin/bash

echo "🚀 Starting all services in the background..."

# 1. Start Database
./start-db.sh

# 2. Start Backend
echo "☕ Starting Backend (port 4080)..."
nohup ./start-backend.sh > backend.log 2>&1 &
echo $! > .backend.pid

# 3. Start Frontend
echo "⚛️ Starting Frontend (port 4000)..."
nohup ./start-frontend.sh > frontend.log 2>&1 &
echo $! > .frontend.pid

echo "----------------------------------------------------"
echo "✅ All services are starting!"
echo "📖 Backend logs:  tail -f backend.log"
echo "📖 Frontend logs: tail -f frontend.log"
echo "🛑 To stop everything, run: ./stop-all.sh"
echo "----------------------------------------------------"
