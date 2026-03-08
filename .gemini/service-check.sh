#!/bin/bash

# Configuration
BACKEND_PORT=8080
FRONTEND_PORT=5173
BACKEND_HEALTH_URL="http://localhost:8080/actuator/health"
FRONTEND_URL="http://localhost:5173"
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
  for i in {1..60}; do
    if check_backend_health; then
      echo "Backend is ready!"
      return 0
    fi
    sleep 2
  done
  echo "Backend failed to start in time."
  return 1
}

wait_for_frontend() {
  echo "Waiting for Frontend to be healthy..."
  for i in {1..30}; do
    if check_frontend_health; then
      echo "Frontend is ready!"
      return 0
    fi
    sleep 2
  done
  echo "Frontend failed to start in time."
  return 1
}

start_all() {
  # Kill existing if any
  fuser -k $BACKEND_PORT/tcp $FRONTEND_PORT/tcp > /dev/null 2>&1 || true

  echo "Starting Backend..."
  mvn spring-boot:run > backend.log 2>&1 &

  echo "Starting Frontend..."
  (cd frontend && pnpm dev) > frontend.log 2>&1 &

  # Start watcher in background if not running
  $0 watch > /dev/null 2>&1 &

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
