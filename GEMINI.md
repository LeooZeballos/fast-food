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

## Architecture & Coding Standards
- **Constructor Injection:** Use constructor injection instead of `@Autowired` on fields. Spring Boot automatically manages this if there is a single constructor.
- **Spring Boot Version:** Upgraded to **3.4.4**. This version was selected as the most stable modern target that maintains compatibility with Lombok and Mockito (3.5.x introduced class-loading regressions in this specific environment).
- **Price Formatting:** Logic in `FoodOrder.getFormattedTotal()` and `Product.getFormattedPrice()` must use `Locale.forLanguageTag("es-ES")` to ensure decimal commas are used, as required by the existing test suite.

## Testing Strategy
- **Integration Tests:** Files like `FastFoodApplicationTests` must use `@ActiveProfiles("test")` and `@TestPropertySource` to override system environment variables (like `SPRING_DATASOURCE_URL`) to ensure they run against H2 rather than attempting to connect to the external PostgreSQL service.
- **Local Native Maven:** Use `mvn` directly in the CLI instead of `./mvnw` to avoid execution/permission issues in the container environment.
