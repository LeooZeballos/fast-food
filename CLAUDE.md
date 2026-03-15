# Claude Project Context — FastFood

This file gives Claude Code the full context needed to work autonomously and correctly on this project.

---

## Mandatory Workflow

1. **Read before writing.** Always read a file before modifying it.
2. **Test after every change.** After ANY code modification, run tests. This is non-negotiable.
   - Backend/core: `mvn clean test`
   - Frontend (E2E): `cd frontend && pnpm test:e2e`
3. **Never stop if tests fail.** A task is NOT done until `Failures: 0, Errors: 0`. Read failing output, fix root cause, re-test.
4. **Prove it.** Final response must include test results: `Tests run: X, Failures: 0, Errors: 0`.

---

## Environment & Infrastructure

- **Dev environment:** Devcontainer with PostgreSQL (`db:5432`) and H2 (tests only).
- **Active Spring profiles:**
  - `dev` (default) — uses PostgreSQL
  - `test` — uses H2 in-memory DB
- **Ports:** Backend on `4080`, Frontend dev/preview on `4000`.
- **Environment variables:**
  - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://db:5432/fastfood`
  - `SPRING_DATASOURCE_USERNAME`: `postgres`
  - `SPRING_DATASOURCE_PASSWORD`: `postgres`

### Service Management

```bash
./scripts/start-all.sh          # Start DB + Backend (4080) + Frontend (4000)
./scripts/stop-all.sh           # Stop all services
.gemini/service-check.sh status   # Check service health

# Individual services (background)
nohup ./scripts/start-backend.sh > backend.log 2>&1 &
nohup ./scripts/start-frontend.sh > frontend.log 2>&1 &
tail -f backend.log     # Verify startup
```

### Debugging with Spring Actuator

```bash
curl http://localhost:4080/actuator/health     # App health
curl http://localhost:4080/actuator/mappings   # All registered endpoints (debug 404s)
curl http://localhost:4080/actuator/beans      # All Spring beans (debug DI issues)
curl http://localhost:4080/actuator/env        # Active profile + datasource config
curl http://localhost:4080/actuator/loggers    # View/configure log levels at runtime
```

---

## Architecture

The project is split into two independent applications:

- **Backend:** Spring Boot 3.4.4 (`/src`) — REST API at `/api/v1`
- **Frontend:** Vite + React + TypeScript (`/frontend`) — communicates via HTTP/JSON

### Backend Layer Structure

Every domain follows the strict **Controller → Service → Repository** pattern:

```
src/main/java/net/leozeballos/FastFood/
├── {domain}/
│   ├── {Domain}RestController.java   # HTTP layer only, no business logic
│   ├── {Domain}Service.java          # All business logic and validation
│   ├── {Domain}Repository.java       # JPA data access
│   ├── {Domain}.java                 # JPA Entity
│   └── {Domain}DTO.java              # Data Transfer Object for API responses
```

Core domains: `product`, `menu`, `branch`, `foodorder`, `inventory`, `foodorderdetail`

Cross-cutting:
- `config/` — Security, CORS, OpenAPI, DataInitializer
- `mapper/` — Entity ↔ DTO mapping (manual mappers, no MapStruct)
- `error/` — `GlobalExceptionHandler` (`@ControllerAdvice`), `ResourceNotFoundException`
- `util/` — `FormattingUtils` (price/date/state formatting), `DevDbController` (dev profile only)
- `auth/` — `User`, `CustomUserDetails`, `CustomUserDetailsService`
- `foodorderstatemachine/` — Spring State Machine config, states, events, interceptor

### Frontend Structure

```
frontend/src/
├── api.ts                  # All API calls (axios) + TypeScript DTOs
├── App.tsx                 # Root: auth gate, view routing, layout
├── AuthContext.tsx          # Auth state: isAuthenticated, isAdmin, branchId
├── ThemeContext.tsx         # Light/dark theme
├── i18n.ts                 # i18next config
├── locales/
│   ├── en.json             # English translations
│   └── es.json             # Spanish translations
└── components/
    ├── TakeOrder.tsx        # POS terminal — select branch, browse items, place order
    ├── OrderList.tsx        # Kitchen KDS — real-time order board (5s polling)
    ├── AdminPanel.tsx       # Product/menu/branch management (admin only)
    ├── Navbar.tsx           # Top nav with view switching and auth
    ├── Login.tsx            # Login form
    └── ui/                 # Shadcn UI primitives (button, card, dialog, etc.)
```

---

## Coding Standards

### Backend

- **Constructor injection always.** Never use `@Autowired` on fields. Spring handles single-constructor classes automatically. Use `@RequiredArgsConstructor` (Lombok) where appropriate.
- **Business logic in services only.** Controllers handle HTTP mapping; repositories handle DB access.
- **DTOs for API responses.** Never expose JPA entities directly. Use DTOs and mappers.
- **`@Transactional` discipline.** Mark service classes `@Transactional(readOnly = true)` by default. Override individual write methods with `@Transactional`.
- **JPQL/native queries only when needed.** Use Spring Data method names for simple queries; JPQL for joins with `FETCH` (to avoid N+1); native SQL only as last resort.
- **Validation.** Annotate `@RequestBody` params with `@Valid`. Use Bean Validation annotations on DTOs/records.
- **Error handling.** Throw `ResourceNotFoundException` for 404s. All exceptions are caught by `GlobalExceptionHandler` which returns RFC 7807 problem details.

### Frontend

- **All API calls go through `api.ts`.** Use the `api` axios instance (auto-handles CSRF). Never use `fetch` directly.
- **State management via TanStack Query.** Use `useQuery` for data fetching, `useMutation` for writes. Invalidate relevant query keys on mutation success.
- **No hardcoded strings.** Every UI string must use `t('key')` from `useTranslation()`. Add to both `en.json` and `es.json`.
- **TypeScript DTOs must match backend.** When the backend `FoodOrderDTO` record changes, update the corresponding type in `api.ts`.
- **`(error as any)` over `@ts-ignore`.** Prefer type casts with a comment over suppression directives.

---

## Domain Model

### Order State Machine

```
CREATED ──STARTPREPARATION──► INPREPARATION ──FINISHPREPARATION──► DONE
   │                                │                                 │
   └──CANCEL──► CANCELLED           └──CANCEL──► CANCELLED           ├──CONFIRMPAYMENT──► PAID
                                                                      └──REJECT──► REJECTED
```

Valid `formattedState` values (used in frontend): `"Created"`, `"Inpreparation"`, `"Done"`, `"Paid"`, `"Cancelled"`, `"Rejected"`

State changes are persisted by `FoodOrderStateChangeInterceptor`. The `build()` method in `FoodOrderService` rehydrates the state machine from the DB before sending events.

### Pricing & Menus

- `Menu` price = `sum(product.price) * (1 - discountPercentage)`
- Discounts stored as `BigDecimal` (e.g., `0.15` = 15%)
- **Price formatting is centralized in `FormattingUtils.java`** using `Locale.forLanguageTag("es-ES")`
- `Item` inheritance uses JPA `SINGLE_TABLE` strategy (`Product` and `Menu` are both `Item` subtypes)

### Inventory

- Each `Inventory` record links an `Item` to a `Branch` with a `stockQuantity`
- Stock is decremented atomically in `FoodOrderService.createOrder()` before saving
- `isItemAvailable()` checks both `stockQuantity > 0` and `isAvailable` flag

### Access Control (Branch Scoping)

- Users have a `branchId` (or `null` for admins)
- Non-admin users can only see/modify data for their own branch
- `getEffectiveBranchId(userDetails)` in controllers returns `null` for admins (sees everything) or the user's `branchId`
- `checkBranchAccess()` in `FoodOrderService` enforces this with `AccessDeniedException`

---

## Localization (Bilingual Mandate)

The app MUST be fully bilingual (English + Spanish). This is non-negotiable.

- Every new UI string → add to **both** `en.json` and `es.json`
- Use `t('key')` in React components — never hardcode English text
- Use `t('key', { variable })` for interpolation — keys use `{{variable}}` syntax in JSON
- Currency/date formatting must respect `i18n.language`: use `Intl.NumberFormat` or `FormattingUtils`

---

## Testing Strategy

### Backend Tests (`src/test/`)

| Type | Location | Tools |
|------|----------|-------|
| Unit | `{Domain}ServiceTest.java`, `{Domain}Test.java` | JUnit 5, Mockito |
| Integration | `{Domain}ServiceIntegrationTest.java`, `*RepositoryTest.java` | `@SpringBootTest`, H2, `@ActiveProfiles("test")` |
| Security | `SecurityIntegrationTest.java` | `@SpringBootTest`, MockMvc |
| Controller | `*RestControllerTest.java` | MockMvc, `@WebMvcTest` |

**Integration tests MUST use:**
```java
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "SPRING_DATASOURCE_URL=jdbc:h2:mem:testdb",
    ...
})
```

Run with native Maven (not `./mvnw`): `mvn clean test`

### Frontend Tests

- Unit/component: Vitest + React Testing Library (`frontend/src/components/__tests__/`)
- E2E: Playwright (`frontend/tests/`)
- Run: `cd frontend && pnpm test` (unit) or `pnpm test:e2e` (E2E)

---

## Security Guidelines

### Authentication & Authorization

- Spring Security with form-based login (`/login`), session cookies
- CSRF protection via `CookieCsrfTokenRepository` (cookie readable by JS for SPA)
- CORS restricted to configured `allowed-origins` (default: `http://localhost:5173`)
- Role-based: `ROLE_ADMIN` bypasses branch scoping; `ROLE_USER` is branch-scoped

### OWASP Checklist (Quick Reference)

| Risk | What to Check |
|------|--------------|
| **A01 Broken Access Control** | Every endpoint uses `@AuthenticationPrincipal`; branch-scoped queries enforced |
| **A03 Injection** | Use parameterized JPQL (`?1` positional params), never string concat in queries |
| **A05 Misconfiguration** | Actuator endpoints `/env`, `/heapdump` must NOT be public; check `SecurityConfig` |
| **A06 Outdated Components** | Run `pnpm audit` + `mvn dependency-check:check` |
| **A07 Auth Failures** | Passwords hashed with `BCryptPasswordEncoder`; no plain-text credentials |

### Security Audit Commands

```bash
cd frontend && pnpm audit                  # Frontend dependency audit
mvn dependency-check:check                 # Backend dependency audit
grep -rE "password|secret|apiKey" src/ --include="*.java"   # Secrets scan
grep -rE "password|secret|apiKey" frontend/src/ --include="*.ts" --include="*.tsx"
```

---

## Common Pitfalls

- **N+1 queries:** Always use `LEFT JOIN FETCH` in JPQL when loading orders with their details and branches.
- **State machine rehydration:** Always call `build(id)` before `sendEvent()` — this resets the machine to the current DB state.
- **Missing i18n key:** Adding a key to only one locale file will cause `t()` to return the raw key string in the other language.
- **`FoodOrderDTO` type drift:** The TypeScript `FoodOrderDTO` in `api.ts` must include all fields the Java record serializes. Jackson serializes record components + public accessor methods.
- **Dev-only endpoints:** `DevDbController` is guarded by `@Profile("dev")` — never expose it in production.
- **Test profile isolation:** Integration tests must override `SPRING_DATASOURCE_URL` via `@TestPropertySource` or they'll try to connect to the Docker PostgreSQL service and fail.

<!-- gitnexus:start -->
# GitNexus — Code Intelligence

This project is indexed by GitNexus as **fast-food** (956 symbols, 2564 relationships, 63 execution flows). Use the GitNexus MCP tools to understand code, assess impact, and navigate safely.

> If any GitNexus tool warns the index is stale, run `npx gitnexus analyze` in terminal first.

## Always Do

- **MUST run impact analysis before editing any symbol.** Before modifying a function, class, or method, run `gitnexus_impact({target: "symbolName", direction: "upstream"})` and report the blast radius (direct callers, affected processes, risk level) to the user.
- **MUST run `gitnexus_detect_changes()` before committing** to verify your changes only affect expected symbols and execution flows.
- **MUST warn the user** if impact analysis returns HIGH or CRITICAL risk before proceeding with edits.
- When exploring unfamiliar code, use `gitnexus_query({query: "concept"})` to find execution flows instead of grepping. It returns process-grouped results ranked by relevance.
- When you need full context on a specific symbol — callers, callees, which execution flows it participates in — use `gitnexus_context({name: "symbolName"})`.

## When Debugging

1. `gitnexus_query({query: "<error or symptom>"})` — find execution flows related to the issue
2. `gitnexus_context({name: "<suspect function>"})` — see all callers, callees, and process participation
3. `READ gitnexus://repo/fast-food/process/{processName}` — trace the full execution flow step by step
4. For regressions: `gitnexus_detect_changes({scope: "compare", base_ref: "main"})` — see what your branch changed

## When Refactoring

- **Renaming**: MUST use `gitnexus_rename({symbol_name: "old", new_name: "new", dry_run: true})` first. Review the preview — graph edits are safe, text_search edits need manual review. Then run with `dry_run: false`.
- **Extracting/Splitting**: MUST run `gitnexus_context({name: "target"})` to see all incoming/outgoing refs, then `gitnexus_impact({target: "target", direction: "upstream"})` to find all external callers before moving code.
- After any refactor: run `gitnexus_detect_changes({scope: "all"})` to verify only expected files changed.

## Never Do

- NEVER edit a function, class, or method without first running `gitnexus_impact` on it.
- NEVER ignore HIGH or CRITICAL risk warnings from impact analysis.
- NEVER rename symbols with find-and-replace — use `gitnexus_rename` which understands the call graph.
- NEVER commit changes without running `gitnexus_detect_changes()` to check affected scope.

## Tools Quick Reference

| Tool | When to use | Command |
|------|-------------|---------|
| `query` | Find code by concept | `gitnexus_query({query: "auth validation"})` |
| `context` | 360-degree view of one symbol | `gitnexus_context({name: "validateUser"})` |
| `impact` | Blast radius before editing | `gitnexus_impact({target: "X", direction: "upstream"})` |
| `detect_changes` | Pre-commit scope check | `gitnexus_detect_changes({scope: "staged"})` |
| `rename` | Safe multi-file rename | `gitnexus_rename({symbol_name: "old", new_name: "new", dry_run: true})` |
| `cypher` | Custom graph queries | `gitnexus_cypher({query: "MATCH ..."})` |

## Impact Risk Levels

| Depth | Meaning | Action |
|-------|---------|--------|
| d=1 | WILL BREAK — direct callers/importers | MUST update these |
| d=2 | LIKELY AFFECTED — indirect deps | Should test |
| d=3 | MAY NEED TESTING — transitive | Test if critical path |

## Resources

| Resource | Use for |
|----------|---------|
| `gitnexus://repo/fast-food/context` | Codebase overview, check index freshness |
| `gitnexus://repo/fast-food/clusters` | All functional areas |
| `gitnexus://repo/fast-food/processes` | All execution flows |
| `gitnexus://repo/fast-food/process/{name}` | Step-by-step execution trace |

## Self-Check Before Finishing

Before completing any code modification task, verify:
1. `gitnexus_impact` was run for all modified symbols
2. No HIGH/CRITICAL risk warnings were ignored
3. `gitnexus_detect_changes()` confirms changes match expected scope
4. All d=1 (WILL BREAK) dependents were updated

## CLI

- Re-index: `npx gitnexus analyze`
- Check freshness: `npx gitnexus status`
- Generate docs: `npx gitnexus wiki`

<!-- gitnexus:end -->
