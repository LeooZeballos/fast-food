#!/bin/bash

# Configuration
BACKEND_PORT=4080
FRONTEND_PORT=4000
BACKEND_HEALTH_URL="http://localhost:4080/actuator/health"
FRONTEND_URL="http://localhost:4000"
STATUS_FILE="/workspace/.gemini/service-status.txt"

check_port() {
  local port=$1
  lsof -i :$port > /dev/null 2>&1
  return $?
}

check_backend_health() {
  local response=$(curl -s $BACKEND_HEALTH_URL)
  if [[ $response == *"UP"* ]]; then
    return 0
  fi
  return 1
}

check_frontend_health() {
  curl -s $FRONTEND_URL > /dev/null 2>&1
  return $?
}

get_status_text() {
  local out=""
  out+="--- Service Status (Updated: $(date)) ---\n"

  if check_port $BACKEND_PORT; then
    if check_backend_health; then
      out+="Backend: RUNNING (Healthy)\n"
    else
      out+="Backend: RUNNING (Starting/Unhealthy)\n"
    fi
  else
    out+="Backend: STOPPED\n"
  fi

  if check_port $FRONTEND_PORT; then
    if check_frontend_health; then
      out+="Frontend: RUNNING (Healthy)\n"
    else
      out+="Frontend: RUNNING (Starting/Unhealthy)\n"
    fi
  else
    out+="Frontend: STOPPED\n"
  fi
  out+="------------------------------------------\n"
  echo -e "$out"
}

status() {
  get_status_text
}

watch_status() {
  echo "Starting service watcher..."
  while true; do
    get_status_text > "$STATUS_FILE"
    sleep 10
  done
}

wait_for_backend() {
  echo "Waiting for Backend to be healthy..."
  for i in {1..120}; do
    if check_backend_health; then
      echo "Backend is ready!"
      return 0
    fi
    sleep 0.5
  done
  echo "Backend failed to start in time."
  return 1
}

wait_for_frontend() {
  echo "Waiting for Frontend to be healthy..."
  for i in {1..120}; do
    if check_frontend_health; then
      echo "Frontend is ready!"
      return 0
    fi
    sleep 0.5
  done
  echo "Frontend failed to start in time."
  return 1
}

start_all() {
  echo "Stopping existing services..."
  # Use a more targeted kill
  fuser -k 8080/tcp 5173/tcp 3000/tcp 4000/tcp 4080/tcp > /dev/null 2>&1 || true
  sleep 2

  # Always re-build JAR if port changed
  echo "Building JAR..."
  mvn package -Dmaven.test.skip=true

  echo "Starting Backend (Port 4080)..."
  nohup java -Dspring.profiles.active=dev -Duser.timezone=UTC -jar target/FastFood-0.0.1-SNAPSHOT.jar > backend.log 2>&1 &
  sleep 2

  echo "Starting Frontend (Port 4000)..."
  nohup sh -c "cd frontend && pnpm dev" > frontend/frontend.log 2>&1 &
  sleep 2

  # Start watcher
  nohup bash "$0" watch > /dev/null 2>&1 &

  wait_for_backend
  wait_for_frontend
}

case "$1" in
  status)
    status
    ;;
  read-status)
    cat "$STATUS_FILE" 2>/dev/null || status
    ;;
  watch)
    watch_status
    ;;
  wait-backend)
    wait_for_backend
    ;;
  wait-frontend)
    wait_for_frontend
    ;;
  start-all|--start-all)
    start_all
    ;;
  *)
    echo "Usage: $0 {status|read-status|watch|wait-backend|wait-frontend|start-all}"
    exit 1
    ;;
esac
