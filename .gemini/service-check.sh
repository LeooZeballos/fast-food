#!/bin/bash

# Configuration
BACKEND_PORT=8080
FRONTEND_PORT=5173
BACKEND_HEALTH_URL="http://localhost:8080/actuator/health"
FRONTEND_URL="http://localhost:5173"

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

status() {
  echo "--- Service Status ---"
  
  if check_port $BACKEND_PORT; then
    if check_backend_health; then
      echo "Backend: RUNNING (Healthy)"
    else
      echo "Backend: RUNNING (Starting/Unhealthy)"
    fi
  else
    echo "Backend: STOPPED"
  fi

  if check_port $FRONTEND_PORT; then
    if check_frontend_health; then
      echo "Frontend: RUNNING (Healthy)"
    else
      echo "Frontend: RUNNING (Starting/Unhealthy)"
    fi
  else
    echo "Frontend: STOPPED"
  fi
  echo "----------------------"
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

case "$1" in
  status)
    status
    ;;
  wait-backend)
    wait_for_backend
    ;;
  wait-frontend)
    wait_for_frontend
    ;;
  *)
    echo "Usage: $0 {status|wait-backend|wait-frontend}"
    exit 1
    ;;
esac
