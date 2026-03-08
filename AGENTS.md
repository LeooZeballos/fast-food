# Agent Rules — FastFood Project

This file contains mandatory rules for all AI agents working on this codebase.

## Mandatory Workflow

1. **Read before writing.** Always read a file with `read_file` before modifying it.
2. **Test after every change.** After ANY code modification, you MUST run tests. The project is running inside WSL/Linux, so use the native Maven command:
   ```
   mvn clean test
   ```
   using the `run_command` tool.
3. **CRITICAL RULE: NEVER STOP IF TESTS FAIL.** Your task is NOT DONE if ANY tests are failing. It doesn't matter if the failures were pre-existing or caused by you. You MUST autonomously fix ALL test failures (Failures: 0, Errors: 0) before you are allowed to stop working or report completion.
4. **Iterate.** Read the failing test output, edit the code to fix the root cause, and run `mvn clean test` again. Repeat this loop until ALL tests pass.
5. **Report results.** Your final response must prove that all tests passed:
   `Tests run: X, Failures: 0, Errors: 0`

## Advanced MCP & Debugging Capabilities

- **Postgres MCP:** Use the `postgres` MCP server to directly query the database in the `db` container. This is preferred for verifying that JPA mappings match the live schema.
- **Spring Actuator:** Use the `fetch` MCP or `curl` (via `run_shell_command`) to call `http://localhost:8080/actuator` endpoints (like `/beans`, `/mappings`, `/health`) to diagnose runtime issues or verify configurations.
- **Actuator Skill:** Activate the `actuator` skill for expert guidance on debugging the Spring application state.

## Project Structure

- `src/main/java/net/leozeballos/FastFood/` — main source
- Each domain (product, menu, branch, foodorder) has: `Controller`, `Service`, `Repository`, `Entity`
- Tests are in `src/test/` — unit tests use Mockito, integration tests use H2 in-memory DB

## Allowed Commands (via run_command tool)

- `mvn clean test` — run all tests (USE THIS, it's the native Linux Maven)
- `mvn clean test -Dtest=SomeTest` — run a specific test
- `git diff` — see what changed

## Code Style

- Follow existing patterns in the codebase
- Use `@Autowired` via constructor injection (see existing services)
- REST endpoints follow the pattern: `@GetMapping("/{entity}/count")`
