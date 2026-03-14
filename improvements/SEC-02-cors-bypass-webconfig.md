# SEC-02 — CORS Bypass via Redundant WebConfig

**Priority:** HIGH
**Domain:** Security
**Effort:** Small (20 min)

---

## Summary

Two CORS configurations coexist: `SecurityConfig.corsConfigurationSource()` (explicit header allowlist) and `WebConfig` which sets `allowedHeaders("*")`. The `WebConfig` registration may override SecurityConfig's restrictions, silently allowing any request header from any origin that matches the allowed list.

---

## Context

Spring MVC's `WebMvcConfigurer.addCorsMappings()` (in `WebConfig`) and Spring Security's `.cors(cors -> cors.configurationSource(...))` (in `SecurityConfig`) are two separate CORS processing mechanisms. When both are present, Security's config takes precedence for Spring Security-protected endpoints, but **unauthenticated preflight (`OPTIONS`) requests** may bypass Security and be handled by the MVC-level config first, leaking the permissive `allowedHeaders("*")`.

The single source of truth for CORS should be `SecurityConfig`.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/config/WebConfig.java` | Remove CORS mapping registration |

---

## Implementation Steps

### Step 1 — Read WebConfig

Read `WebConfig.java` in full before editing.

### Step 2 — Remove addCorsMappings

Delete or comment out the `addCorsMappings` override. If `WebConfig` only contains CORS config and nothing else, delete the file entirely. If it has other MVC config, keep the class but remove just the CORS method:

```java
// REMOVE the entire addCorsMappings method:
// @Override
// public void addCorsMappings(CorsRegistry registry) { ... }
```

### Step 3 — Verify SecurityConfig Covers All Paths

Confirm `SecurityConfig.corsConfigurationSource()` registers `/**`:

```java
source.registerCorsConfiguration("/**", configuration);
```

This should already be the case.

---

## Test Command

```bash
mvn clean test
# Then manually:
curl -X OPTIONS http://localhost:4080/api/v1/products \
  -H "Origin: http://evil.com" \
  -H "Access-Control-Request-Method: GET" -v
# Expected: no Access-Control-Allow-Origin header (origin not in allowlist)
```

---

## Acceptance Criteria

- [ ] Only one CORS configuration exists in the codebase (`SecurityConfig`)
- [ ] Preflight requests from unlisted origins receive no `Access-Control-Allow-Origin` header
- [ ] Frontend (`http://localhost:5173`) still works (allowed origin)
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
