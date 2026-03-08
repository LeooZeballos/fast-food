# Gemini Project Context

This file documents project-specific findings, architectural decisions, and configuration details to optimize development and prevent redundant research.

## Environment & Infrastructure
- **Development Environment:** Devcontainer with PostgreSQL and H2.
- **Environment Variables:**
  - `SPRING_DATASOURCE_URL`: Points to `jdbc:postgresql://db:5432/fastfood` in the devcontainer.
  - `SPRING_DATASOURCE_USERNAME`: `postgres`
  - `SPRING_DATASOURCE_PASSWORD`: `postgres`
- **Active Profiles:**
  - `dev` (default): Uses PostgreSQL.
  - `test`: Uses H2 in-memory database.
- **Service Management:**
  - Use `.gemini/service-check.sh status` to check service status.
  - Backend: `mvn spring-boot:run &> backend.log & .gemini/service-check.sh wait-backend`
  - Frontend: `cd frontend && pnpm dev & .gemini/service-check.sh wait-frontend`
  - This ensures services run in the background with reliable health checks.

## Architecture & Coding Standards
- **Decoupled Architecture**: The project is split into a Spring Boot backend (`/src`) and a Vite + React frontend (`/frontend`).
- **REST API**: Backend controllers under `/api/v1` are `@RestController`s returning JSON DTOs.
- **Frontend Stack**: Vite, React, TypeScript, Tailwind CSS, and Shadcn UI. Managed via `pnpm`.
- **CORS**: Configured in `WebConfig.java` to allow `http://localhost:5173`.
- **Constructor Injection**: Use constructor injection instead of `@Autowired` on fields. Spring Boot automatically manages this if there is a single constructor.
- **Spring Boot Version**: Upgraded to **3.4.4**.
- **Price Formatting**: Logic centralized in `FormattingUtils.java` using `Locale.forLanguageTag("es-ES")`.
- **JPA Inheritance**: `Item` hierarchy uses `SINGLE_TABLE` strategy for performance.
- **Global Error Handling**: Centralized in `GlobalExceptionHandler.java` using `@ControllerAdvice`.

## Testing Strategy
- **Integration Tests:** Files like `FastFoodApplicationTests` must use `@ActiveProfiles("test")` and `@TestPropertySource` to override system environment variables (like `SPRING_DATASOURCE_URL`) to ensure they run against H2 rather than attempting to connect to the external PostgreSQL service.
- **Local Native Maven:** Use `mvn` directly in the CLI instead of `./mvnw` to avoid execution/permission issues in the container environment.
