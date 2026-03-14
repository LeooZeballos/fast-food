# SEC-01 — Missing ROLE_ADMIN Guard on DELETE Endpoints

**Priority:** CRITICAL
**Domain:** Security
**Effort:** Small (30 min)

---

## Summary

`ProductRestController.delete()`, `MenuRestController.delete()`, and `BranchRestController.delete()` have no role check. Any authenticated session-cookie user — including branch-level staff — can delete any product, menu, or branch.

---

## Context

Spring Security is configured globally in `SecurityConfig.java` to require authentication (`.anyRequest().authenticated()`), but does **not** restrict by role at the config level. Role enforcement must be done per-endpoint via `@PreAuthorize`. Currently only `FoodOrderRestController` does branch-scoped access checks; the admin-only destructive endpoints are unguarded.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/product/ProductRestController.java` | Add `@PreAuthorize` to `delete()` |
| `src/main/java/net/leozeballos/FastFood/menu/MenuRestController.java` | Add `@PreAuthorize` to `delete()` |
| `src/main/java/net/leozeballos/FastFood/branch/BranchRestController.java` | Add `@PreAuthorize` to `delete()` |
| `src/main/java/net/leozeballos/FastFood/config/SecurityConfig.java` | Enable method security |
| `src/test/java/net/leozeballos/FastFood/config/SecurityIntegrationTest.java` | Add tests for 403 on delete |

---

## Implementation Steps

### Step 1 — Enable Method Security

Add `@EnableMethodSecurity` to `SecurityConfig.java`:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // ADD THIS
public class SecurityConfig {
```

### Step 2 — Guard DELETE in ProductRestController

```java
@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public void delete(@PathVariable Long id) {
    productService.deleteById(id);
}
```

Do the same for `enable`/`disable` endpoints if they should also be admin-only.

### Step 3 — Guard DELETE in MenuRestController

```java
@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public void delete(@PathVariable Long id) {
    menuService.deleteById(id);
}
```

### Step 4 — Guard DELETE in BranchRestController

```java
@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public void delete(@PathVariable Long id) {
    branchService.deleteById(id);
}
```

### Step 5 — Add Security Tests

In `SecurityIntegrationTest.java`, add test cases:

```java
@Test
@WithMockUser(roles = "USER")
void deleteProduct_asUser_returns403() throws Exception {
    mockMvc.perform(delete("/api/v1/products/1"))
           .andExpect(status().isForbidden());
}

@Test
@WithMockUser(roles = "ADMIN")
void deleteProduct_asAdmin_returns204() throws Exception {
    // stub service
    mockMvc.perform(delete("/api/v1/products/1"))
           .andExpect(status().isNoContent());
}
```

Repeat for menus and branches.

---

## Test Command

```bash
mvn clean test -Dtest=SecurityIntegrationTest
```

---

## Acceptance Criteria

- [ ] `DELETE /api/v1/products/{id}` returns 403 for `ROLE_USER`
- [ ] `DELETE /api/v1/menus/{id}` returns 403 for `ROLE_USER`
- [ ] `DELETE /api/v1/branches/{id}` returns 403 for `ROLE_USER`
- [ ] All three endpoints return 204 for `ROLE_ADMIN`
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
