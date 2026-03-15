# Spring Actuator Debugger Skill

This skill provides expert knowledge for debugging and monitoring the FastFood Spring Boot application using Actuator endpoints.

## Actuator Endpoints

- `GET http://localhost:8080/actuator`: List all available endpoints.
- `GET http://localhost:8080/actuator/health`: Check application health.
- `GET http://localhost:8080/actuator/info`: Get info about the app.
- `GET http://localhost:8080/actuator/beans`: List all loaded Spring beans (useful for dependency injection issues).
- `GET http://localhost:8080/actuator/mappings`: List all registered REST controllers and their paths (useful for 404/405 errors).
- `GET http://localhost:8080/actuator/env`: View application environment properties.
- `GET http://localhost:8080/actuator/loggers`: View and configure logging levels at runtime.
- `GET http://localhost:8080/actuator/configprops`: View all `@ConfigurationProperties` beans.

## Workflow

1. **Verify Connectivity**: Use `fetch` (via the MCP) or `curl` to ensure the app is running and the actuator is reachable.
2. **Inspect State**: Use the relevant endpoint to diagnose issues (e.g., check `beans` for circular dependencies, `mappings` for missing endpoints).
3. **Analyze Logs**: Use the `loggers` endpoint to increase verbosity for specific packages (e.g., `net.leozeballos.FastFood`) if needed.
4. **Environment Check**: Use the `env` endpoint to verify that the active profile (`dev` or `test`) and data source properties are correctly loaded.

## Debugging Common Issues

- **Bean Not Found**: Check `actuator/beans` to see if the bean exists and its name matches expectations.
- **REST 404**: Check `actuator/mappings` to ensure the controller's `@RequestMapping` is correctly registered.
- **Connection Refused**: Ensure the app has been started with `mvn spring-boot:run`.
- **Database Failure**: Use the `postgres` MCP to inspect the live schema and verify it matches the JPA entities.
