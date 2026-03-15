# FastFood Security Audit Checklist

Follow these steps for a manual security audit of the FastFood application.

## 1. Dependency Analysis (High Priority)
- [ ] Run `mvn dependency-check:check` (may require adding the plugin to `pom.xml`).
- [ ] Run `pnpm audit` in the `frontend/` directory.
- [ ] Investigate any high/critical vulnerabilities and propose updates.

## 2. Secrets and Sensitive Data (Medium Priority)
- [ ] Search for hardcoded passwords, API keys, or JWT secrets.
- [ ] Verify `application.properties` doesn't contain plain-text production credentials.
- [ ] Check `.env` files and ensure they are in `.gitignore`.
- [ ] Review `Dockerfile` and `docker-compose.yml` for exposed environment variables.

## 3. Web Layer Analysis (High Priority)
- [ ] Review all `@RestController` and `@Controller` endpoints for access control.
- [ ] Verify CSRF protection is enabled if applicable.
- [ ] Check for CORS misconfigurations in `WebConfig` or `AppController`.
- [ ] Look for SQL/JPQL injection in repository methods.
- [ ] Validate all incoming `@RequestBody` or `@RequestParam` with `@Valid`.

## 4. Frontend Security (Medium Priority)
- [ ] Verify that React isn't using `dangerouslySetInnerHTML` with untrusted data.
- [ ] Check for sensitive data stored in `localStorage` or `sessionStorage`.
- [ ] Ensure API calls use HTTPS (in production context).
- [ ] Audit frontend routing to ensure sensitive views aren't visible to unauthorized users (even if data is protected by API).

## 5. System Integrity (Medium Priority)
- [ ] Audit the `FoodOrder` state machine to ensure valid transitions (e.g., an order can't be 'finished' before 'started').
- [ ] Check for proper error handling to avoid leaking stack traces (check `error.html` and global exception handlers).
