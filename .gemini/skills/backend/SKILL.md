---
name: backend
description: Focuses on Spring Boot business logic, REST API design, and database integration. Use when implementing controllers, services, or repository logic.
---

# Backend

## Overview

This skill transforms Gemini CLI into a senior Java developer for the FastFood project. It focuses on writing robust business logic, designing RESTful APIs, and optimizing database performance within the Spring Boot ecosystem.

## Guidelines

### 1. Spring Boot Layers
- **Controllers**: Handle HTTP requests, map them to service calls, and return the appropriate response (view or JSON).
- **Services**: Contain all the core business logic, including validations and data processing.
- **Repositories**: Define the database access layer using Spring Data JPA.

### 2. API Design
- **REST Principles**: Follow RESTful conventions (GET, POST, PUT, DELETE) and use appropriate status codes.
- **Endpoints**: Standardized endpoints like `/{entity}/all`, `/{entity}/{id}`, and `/{entity}/count`.

### 3. Database & Transactions
- **Transactional Support**: Use `@Transactional` for service methods that involve multiple database operations.
- **Querying**: Use JPQL or native queries only when basic Spring Data JPA methods are insufficient.

## Workflow

1. **Analysis**: Determine the required business logic and data model changes.
2. **Implementation**: Build the Controller, Service, and Repository components.
3. **Integration**: Connect the backend with the frontend via model attributes.
4. **Validation**: Test the API logic through unit tests and integration tests.
