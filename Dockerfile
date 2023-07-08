# Use a multi-stage build for smaller final image
# Stage 1: Build stage
FROM maven:3.8.4-openjdk-8-slim AS build
WORKDIR /app

# Copy and install dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Final stage
FROM openjdk:8-jre-alpine
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application with the "docker" profile
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar"]
