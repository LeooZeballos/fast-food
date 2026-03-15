#!/bin/bash
# Start the Spring Boot backend

# Get the directory where the script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Load env vars from .env if it exists
if [ -f "$PROJECT_ROOT/.env" ]; then
  echo "📄 Loading environment variables from .env"
  # Standard export that handles spaces and comments better
  set -a
  source "$PROJECT_ROOT/.env"
  set +a
fi

echo "☕ Starting backend with Maven..."
# We pass them as system properties to be 100% sure Spring Boot context sees them
cd "$PROJECT_ROOT"
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=${SPRING_DATASOURCE_URL} -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD}"
