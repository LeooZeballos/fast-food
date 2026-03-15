# FastFood App Improvements

This document tracks planned and implemented improvements across all skill domains.

---

## 🍔 Product Specialist (Business & Domain Logic)

### Completed
- [x] **Order Creation (Point of Sale View)**: Dedicated interface for staff to select branches, browse items, and submit new orders.
- [x] **Pricing Transparency**:
    - [x] Show item subtotal in the cart (Quantity × Price).
    - [x] Show original menu subtotal vs discounted total (via Unit/Subtotal breakdown).
    - [x] Centralized price formatting using `FormattingUtils` with `es-ES` locale.
- [x] **Inventory/Availability Awareness**: Validate item availability at the selected branch during order placement.
- [x] **Kitchen Display System (KDS) Metrics**: Track and display time in preparation for each order (`preparationStartTimestamp`).

### Pending
- [ ] **Refund Stock on Cancel/Reject**: When an order is cancelled or rejected, the decremented inventory stock is **not restored**. `FoodOrderService.cancel()` and `reject()` must call `inventoryService.incrementStock()` for each order detail. This causes permanently inaccurate inventory counts over time. (`FoodOrderService.java:168-180`)
- [ ] **Order Auto-Expiry (SLA Timeout)**: No automatic state transitions exist. Orders stuck in `CREATED` for too long (e.g., 30 min) should auto-cancel via a `@Scheduled` task. Add to `StateMachineConfig`.
- [ ] **Low Stock Alerts / Reorder Thresholds**: `InventoryService.isItemAvailable()` checks stock but has no minimum-threshold warnings. Add a reorder-point field to `Inventory` and expose a low-stock notification endpoint.
- [ ] **Prevent Menu Modification Under Active Orders**: `ProductService.disableItem()` removes a product from menus but orders already referencing those items remain open. Add a guard: disabling a product should check for open (non-terminal) orders containing it and warn or block.
- [ ] **Discount Percentage Validation**: No validation that `discountPercentage` is between 0 and 100. A typo can create a menu with a 500% discount. Add `@DecimalMin("0.0") @DecimalMax("100.0")` to the menu DTO.
- [ ] **Missing Inventory Restock Endpoint**: There is no API to increase stock (e.g., when new inventory arrives). Add `POST /api/v1/inventory/{branchId}/restock` for branch managers.

---

## 🎨 Designer (UI/UX & Aesthetics)

### Completed
- [x] **FastFood Color Palette**: High-impact brand colors (warm reds, yellows) and redesigned Navbar.
- [x] **Kanban Board for Orders**: Visual 3-column workflow (`New → In Kitchen → Ready`) for kitchen staff.
- [x] **Loading Skeletons**: Animated skeletons in Product, Menu, and Order lists.
- [x] **Visual Hierarchy on Cards**: Product and Menu lists displayed as visually appealing grid cards.

### Pending
- [ ] **Confirmation Dialog for Destructive Actions**: Cancel, Reject, and Delete buttons have **no confirmation prompt**. A mis-click can irreversibly cancel a paid order or delete a product. Wrap these actions in an `AlertDialog` ("Are you sure?" pattern). Affects `OrderList.tsx:163-185` and all Admin panel delete buttons.
- [ ] **Loading States on Order Action Buttons**: Start, Ready, Pay, Cancel, and Reject buttons don't show a loading spinner while the mutation is in flight. Add `disabled={isPending}` and a spinner per each mutation's `isPending` state (`OrderList.tsx:220-227`).
- [ ] **Error Toasts for Failed Order Actions**: Order action mutations have `onSuccess` but **no `onError` handler**. Failed actions silently disappear. Add `onError: (e) => toast.error(e.message)` to all mutation options (`OrderList.tsx:214-219`).
- [ ] **Mobile Kanban Layout**: The 3-column Kanban uses `grid-cols-3`, which overflows on tablets and phones. Add a scrollable horizontal layout or a tab-based switcher on small screens (`OrderList.tsx:282`).
- [ ] **Optimistic Updates on Order Actions**: With 5-second polling, there is a visible lag between clicking an action and seeing the state change. Implement TanStack Query optimistic updates so the card moves columns immediately on click.
- [ ] **Pagination / Virtualization for Large Lists**: No pagination exists for orders, products, or menus. With 100+ items the page becomes sluggish. Add pagination or virtual scrolling (e.g., `@tanstack/react-virtual`).
- [ ] **Micro-animations**: Add staggered card entry on load, success animations (card slide-out on state change), and pulse effects for newly arrived orders in the KDS.
- [ ] **Accessibility (a11y)**:
    - `TimeAgo` component updates every 30s with no `aria-live` region — screen readers won't announce time changes.
    - Order action buttons lack descriptive `aria-label` attributes (e.g., `aria-label="Cancel order #42"`).
    - Keyboard navigation not implemented for Kanban board cards.
- [ ] **Session Expiry Feedback**: `AuthContext` checks auth on mount but not during long sessions. When the session expires mid-use, the next API call silently returns 401. Add a 401 interceptor in `api.ts` response interceptor to redirect to login with a toast notification.

---

## 🛡️ Security Specialist (Audit & Compliance)

### Completed
- [x] **Spring Security with BCrypt**: Database-backed authentication, modern password hashing, login/logout flow.
- [x] **Restrict Actuator Endpoints**: Restricted to `health` and `info` in `application.properties`.
- [x] **Secure Database Credentials**: Encrypted with Jasypt in `application-dev.properties` and `docker-compose.yml`.
- [x] **Frontend Dependency Audit**: Patched `hono` Prototype Pollution vulnerability. `pnpm audit` reports no vulnerabilities.
- [x] **CSRF Protection**: Cookie-based CSRF with `CookieCsrfTokenRepository` and full React/Axios integration.
- [x] **Branch-Scoped Data Access**: Non-admin users can only access their own branch's data.

### Pending — Critical
- [ ] **Missing `ROLE_ADMIN` Guard on DELETE Endpoints**: `ProductRestController.delete()`, `MenuRestController.delete()`, and `BranchRestController.delete()` have **no role check**. Any authenticated user (including branch-level staff) can delete any product, menu, or branch. Add `@PreAuthorize("hasRole('ADMIN')")` to all three endpoints.
- [ ] **CORS Bypass via Redundant `WebConfig`**: Two CORS configurations exist simultaneously — `SecurityConfig.corsConfigurationSource()` (explicit header allowlist) and `WebConfig` (sets `allowedHeaders("*")`). The `WebConfig` may silently override the header restrictions. Remove CORS from `WebConfig` and consolidate entirely in `SecurityConfig`.
- [ ] **Inventory Update Missing Authorization**: `InventoryRestController`'s update endpoint has no branch ownership check. Any authenticated user can modify inventory for any branch. Apply the same `getEffectiveBranchId` pattern used in `FoodOrderRestController`.
- [ ] **Race Condition on Stock Decrement (Overselling)**: In `FoodOrderService.createOrder()`, `isItemAvailable()` (L75) and `decrementStock()` (L93) are two separate DB calls. A concurrent request can consume the last unit between check and decrement. Resolve with a single atomic `UPDATE inventory SET stock = stock - ? WHERE branch_id = ? AND item_id = ? AND stock >= ?` query that checks affected rows.

### Pending — Medium
- [ ] **Hardcoded Default Admin Credentials in `DataInitializer`**: The admin password (bcrypt of `"admin"`) is committed in source code and is a publicly known hash. On first run, read from an environment variable (`ADMIN_INITIAL_PASSWORD`) or generate a random password and print it to the logs.
- [ ] **Unvalidated `Map<String, Object>` in `MenuRestController`**: Menu creation/update accepts a raw `Map<String, Object>` with no `@Valid` annotation. Replace with typed `MenuCreateDTO` / `MenuUpdateDTO` records with Bean Validation annotations (`@NotBlank`, `@Size`, `@DecimalMin`). Addresses OWASP A03.
- [ ] **Missing Audit Logging**: Critical actions (delete product, delete branch, cancel order) are not logged with who performed them. Add an `AuditService` or use Spring `@EventListener` on domain events to write audit entries. Addresses OWASP A09.
- [ ] **No Rate Limiting on `/login`**: The login endpoint can be brute-forced at full speed. Add rate limiting (e.g., Bucket4j) that throttles after N failed attempts per IP. Addresses OWASP A07.
- [ ] **`open-in-view` Profile Mismatch**: `application.properties` sets `spring.jpa.open-in-view=false` but `application-dev.properties` overrides it to `true`. Code that works in dev (lazy loading in views) will silently break in production. Set `false` in all profiles.
- [ ] **Flyway `repair-on-migrate=true` in Dev**: This silently fixes migration checksum failures, masking broken migrations that would only surface in production. Remove from `application-dev.properties`.

---

## ⚙️ Backend Specialist (Performance & Architecture)

### Completed
- [x] **Disable Open-Session-In-View (OSIV)**: `spring.jpa.open-in-view=false` in `application.properties`.
- [x] **Java 21 + Virtual Threads**: `spring.threads.virtual.enabled=true` for improved throughput.
- [x] **Caching Layer**: `@Cacheable` with Caffeine for branches, products, and menus.
- [x] **RFC 7807 Error Handling**: `GlobalExceptionHandler` returns `ProblemDetail` for standardized error responses.
- [x] **Optimistic Locking on FoodOrder**: `@Version` field on `FoodOrder` entity.
- [x] **Flyway Database Migrations**: Version-controlled schema management replacing `ddl-auto=update`.
- [x] **Externalized CORS Configuration**: `app.security.cors.allowed-origins` in `application.properties`.
- [x] **OpenAPI/Swagger Documentation**: All REST controllers annotated with `@Operation`, `@ApiResponse`, `@Tag`.
- [x] **Eliminate In-Memory State Filtering**: `FoodOrderRepository` now has `findAllByStateWithDetails()` — state filtering moved to DB instead of Java streams.

### Pending
- [ ] **Missing Database Indexes on High-Traffic Columns**: PostgreSQL does not auto-create indexes on FK columns. Add a Flyway migration:
    ```sql
    CREATE INDEX idx_foodorder_branch_id ON food_order(branch_id);
    CREATE INDEX idx_foodorder_state ON food_order(state);
    CREATE INDEX idx_inventory_branch_item ON inventory(branch_id, item_id);
    ```
- [ ] **Async State Machine Error Handling**: `FoodOrderService.sendEvent()` calls `stateMachine.sendEvent(Mono.just(msg)).subscribe()` with no error callback. If a transition fails, the error is swallowed silently. Add `.subscribe(result -> {}, error -> { throw new ... })` or use `.block()` and handle exceptions synchronously.
- [ ] **Enhanced Observability (Micrometer + Tracing)**: No `@Timed` metrics on critical service methods. No distributed tracing correlation IDs. Add Micrometer timers to `FoodOrderService`, `InventoryService`, and `ProductService`. Integrate `micrometer-tracing` for request correlation.
- [ ] **Request Size Limiting**: No `max-http-post-size` configured. A malicious client could POST a 1 GB order payload causing OOM. Add `server.tomcat.max-http-post-size=1048576` (1 MB) to `application.properties`.
- [ ] **Cache Invalidation Strategy**: Current `@CacheEvict(allEntries = true)` clears the entire cache on every save/delete, causing cache thrashing under concurrent writes. Switch to key-based eviction: `@CacheEvict(value = "products", key = "#product.id")`.
- [ ] **Optimistic Locking on `Inventory` Entity**: `@Version` is on `FoodOrder` but **not on `Inventory`**. Concurrent stock decrements can still race. Add `@Version private Long version;` to `Inventory.java`.

---

## 🧪 Tester (Test Coverage & Quality)

### Completed
- [x] Unit tests: `BranchService`, `ProductService`, `MenuService`, `FoodOrderService`, `AddressService`, `FoodOrderDetailService`, `InventoryService`.
- [x] Integration tests: `FoodOrderServiceIntegrationTest`, `ProductFilterIntegrationTest`, `InventoryRepositoryTest`, `SecurityIntegrationTest`.
- [x] Repository tests: `FoodOrderRepositoryTest`.
- [x] Controller tests: `InventoryRestControllerTest`.
- [x] Frontend Vitest unit tests: `Navbar`, `OrderList`, `ProductList` components.
- [x] Playwright E2E framework configured.

### Pending
- [ ] **No Tests for `CustomUserDetailsService`**: The auth service that loads users from the DB has no test. A regression here breaks all logins. Add a unit test with a mocked `UserRepository`.
- [ ] **No Controller Tests for `ProductRestController`, `MenuRestController`, `BranchRestController`**: Only service-level tests exist. Add `@WebMvcTest` tests that verify HTTP status codes, authorization (unauthenticated → 401, wrong role → 403), and request validation errors (→ 400).
- [ ] **Missing Edge Cases in `FoodOrderServiceTest`**:
    - Creating an order with insufficient stock (should throw `IllegalStateException`)
    - Creating an order when item doesn't exist in inventory (should throw `ResourceNotFoundException`)
    - Cancelling an order and verifying stock is restored (currently stock is **not** restored — test would document and enforce the fix)
- [ ] **No State Machine Transition Tests**: `StateMachineConfig` defines which transitions are valid, but there are no tests verifying invalid transitions (e.g., `PAID → CREATED`) are rejected. Add integration tests for every valid transition and every invalid one.
- [ ] **Frontend: `TakeOrder` Component Tests**: The most complex frontend component has zero tests. Add Vitest tests for:
    - Cart add/remove/quantity update logic
    - Branch selection pre-population from localStorage
    - Out-of-stock items cannot be added to cart
    - Checkout button is disabled when cart is empty or no branch is selected
- [ ] **Frontend: `AdminPanel` Authorization Tests**: Verify that non-admin users see the "Access Denied" state and that admin-only UI elements are not rendered.
- [ ] **E2E Smoke Tests (Playwright)**: No E2E tests exist despite Playwright being configured. Add at minimum:
    - Login → Place order → Verify order appears in KDS columns
    - Login as admin → Create product → Verify it appears in POS terminal
- [ ] **Accessibility Tests**: Add `@axe-core/playwright` to E2E suite to catch a11y regressions automatically on every run.

---

## 🏛️ Architect (Structure & Design)

### Completed
- [x] **Controller → Service → Repository** separation enforced across all domains.
- [x] **DTOs for API responses** in orders, products, menus, branches (via dedicated mappers).
- [x] **Constructor injection** consistently used (Lombok `@RequiredArgsConstructor`).
- [x] **Single-table JPA inheritance** for `Item` hierarchy (`Product` + `Menu`).
- [x] **State machine pattern** for order lifecycle via Spring State Machine.
- [x] **`getEffectiveBranchId` extracted to variable** in controller action methods (no longer called twice).

### Pending
- [ ] **Controllers Accepting JPA Entities Directly**: `ProductRestController.create()` accepts `@RequestBody Product product` instead of a DTO. This leaks the entity contract to the API and allows clients to set internal fields (e.g., `id`). Create `ProductCreateDTO` / `ProductUpdateDTO` and map in the service layer — matching the pattern already used for `FoodOrder`.
- [ ] **Inconsistent Mapper Usage**: `FoodOrderService` and `ProductService` use dedicated mapper classes, while `BranchService` and `MenuService` do inline mapping. `BranchMapper` partially exists. Create `MenuMapper` and use dedicated mappers everywhere for consistency.
- [ ] **`FoodOrderService` Responsibility Split**: At 220+ lines, `FoodOrderService` mixes order creation, inventory management, and state machine orchestration. Extract a `FoodOrderStateMachineService` to own `build()` and `sendEvent()` logic, keeping `FoodOrderService` focused on business rules.
- [ ] **Domain Events for Loose Coupling**: Order state changes (created, cancelled, paid) are handled inline in service methods. Adding notifications, audit logs, or webhooks requires modifying the service. Use `ApplicationEventPublisher` to emit `OrderCreatedEvent`, `OrderCancelledEvent`, etc., handled in dedicated `@EventListener` beans.
- [ ] **`ItemService` as Unified Item Interface**: `ProductService` and `MenuService` implement overlapping item-level behavior independently. As item-level queries grow, consolidate shared concerns through `ItemService` to avoid duplication across both services.
