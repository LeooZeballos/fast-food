# FastFood App Improvements

This document tracks the planned and implemented improvements for the FastFood application.

## 🍔 Product Specialist (Business & Domain Logic)

- [x] **Order Creation (Point of Sale View)**: Dedicated interface for staff to select branches, browse items, and submit new orders.
- [x] **Pricing Transparency**: 
    - [x] Show item subtotal in the cart (Quantity x Price).
    - [x] Show original menu subtotal vs discounted total (via Unit/Subtotal breakdown).
    - [x] Centralized price formatting to ensure consistent `es-ES` locale usage.
- [ ] **Inventory/Availability Awareness**: Validate item availability at the selected branch during order placement.
- [ ] **Kitchen Display System (KDS) Metrics**: Track and display time in preparation for each order.

## 🎨 Designer (UI/UX & Aesthetics)

- [x] **FastFood Color Palette**: High-impact brand colors (warm reds, yellows) and redesigned Navbar.
- [ ] **Kanban Board for Orders**: Visual workflow for kitchen staff using columns for different order states.
- [ ] **Enhanced Interactive Feedback**:
    - [x] **Loading Skeletons**: Integrated animated skeletons into Product and Menu lists.
    - [ ] **Micro-animations**: Pulse effects for active states and success animations.
- [x] **Visual Hierarchy on Cards**: Switch Product and Menu lists from tables to visually appealing grid cards.

## 🛡️ Security Specialist (Audit & Compliance)

### Critical & High Priority
- [x] **Implement Spring Security**: Added Spring Security with database-backed authentication, modern password hashing (BCrypt), and frontend login/logout flow.
- [x] **Restrict Actuator Endpoints**: Restricted exposure to `health,info` in `application.properties`.
- [x] **Secure Database Credentials**: Encrypted plain-text passwords in `application-dev.properties` and `docker-compose.yml` using Jasypt.

### Medium & Low Priority
- [x] **Update Frontend Dependencies**: Patched the `hono` vulnerability (Prototype Pollution) by updating dependencies. `pnpm audit` now reports no vulnerabilities.
- [x] **Enable CSRF Protection**: Implemented Cookie-based CSRF protection with full React/Axios integration.
- [x] **Audit Data Access**: Ensure branch-specific data is only accessible to authorized users of that branch.

## ⚙️ Backend Specialist (Performance & Architecture)

- [x] **Disable Open-Session-In-View (OSIV)**: Set `spring.jpa.open-in-view=false` to prevent N+1 query issues and improve connection management.

- [x] **Modernize Java & Concurrency**: Upgrade to **Java 21** and enable **Virtual Threads** (`spring.threads.virtual.enabled=true`) for better throughput.
- [x] **Implement Caching Layer**: Use `@Cacheable` with Caffeine or Redis for frequently accessed data like branches, product lists, and menus.
- [x] **Standardized Error Handling (RFC 7807)**: Refactor `GlobalExceptionHandler` to use Spring's `ProblemDetail` for standardized API error responses.
- [x] **Optimistic Locking**: Implement `@Version` in the `Inventory` entity to prevent race conditions during concurrent stock decrements.
- [x] **Database Migration Tooling**: Replace `hibernate.ddl-auto=update` with **Flyway** or **Liquibase** for version-controlled schema management.
- [x] **Externalize Configuration**: Move hardcoded CORS origins and other environment-specific settings to `application.properties` or environment variables.

- [ ] **Enhanced Observability**: Integrate **Micrometer** for metrics and ensure Actuator endpoints are properly secured with role-based access.
- [x] **API Documentation Refinement**: Ensure all REST controllers are fully documented with OpenAPI/Swagger annotations for better frontend integration.
