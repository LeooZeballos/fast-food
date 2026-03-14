#!/bin/bash
# Start the Spring Boot backend

# Load env vars from .env if it exists
if [ -f .env ]; then
  echo "📄 Loading environment variables from .env"
  export $(grep -v '^#' .env | xargs)
fi

echo "☕ Starting backend with Maven..."
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.datasource.url=${SPRING_DATASOURCE_URL} -Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME} -Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD}"
