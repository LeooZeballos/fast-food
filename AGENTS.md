# Agent Rules — FastFood Project

This file contains mandatory rules for all AI agents working on this codebase.

## Mandatory Workflow

1. **Read before writing.** Always read a file with `read_file` before modifying it.
2. **Service Management.** Use the provided scripts for managing services in the background:
   - **Start all:** `./start-all.sh` (Starts DB, Backend on 4080, and Frontend on 4000)
   - **Stop all:** `./stop-all.sh`
   - **Check status:** `.gemini/service-check.sh status`
   If you need to start individual services in the background manually:
   - **Backend:** `nohup ./start-backend.sh > backend.log 2>&1 &`
   - **Frontend:** `nohup ./start-frontend.sh > frontend.log 2>&1 &`
   ALWAYS check logs (`tail -f backend.log`) to ensure successful startup.
3. **Test before you are DONE.** After ANY code modification, you MUST run tests. This is a non-negotiable requirement for completing any task.
   - For backend/core changes: `mvn clean test`
   - For frontend changes: `cd frontend && pnpm test:e2e` (where applicable)
4. **CRITICAL RULE: NEVER STOP IF TESTS FAIL.** Your task is NOT DONE if ANY tests are failing. You MUST autonomously fix ALL test failures before you are allowed to stop working.
5. **Iterate.** Read the failing test output, fix the root cause, and run tests again until ALL tests pass.
6. **Report results.** Your final response must prove that all tests passed:
   `Tests run: X, Failures: 0, Errors: 0`

## Advanced MCP & Debugging Capabilities

- **Spring Actuator:** Use `curl` to call `http://localhost:4080/actuator` endpoints to diagnose runtime issues.
- **Actuator Skill:** Activate the `actuator` skill for expert guidance on debugging the Spring application state.

## Project Structure

- `src/main/java/net/leozeballos/FastFood/` — main source
- Core domains: `product`, `menu`, `branch`, `foodorder`, `inventory`
- Each domain has: `Controller`, `Service`, `Repository`, `Entity`
- Tests are in `src/test/` — unit tests use Mockito, integration tests use H2 in-memory DB

## Allowed Commands (via run_command tool)

- `mvn clean test` — run all tests (USE THIS, it's the native Linux Maven)
- `mvn clean test -Dtest=SomeTest` — run a specific test
- `git diff` — see what changed

## Code Style

- Follow existing patterns in the codebase
- Use `@Autowired` via constructor injection (see existing services)
- REST endpoints follow the pattern: `@GetMapping("/{entity}/count")`
