# TEST-02 — Controller Tests for Product, Menu, and Branch

**Priority:** HIGH
**Domain:** Tester
**Effort:** Large (3h)

---

## Summary

`ProductRestController`, `MenuRestController`, and `BranchRestController` have no `@WebMvcTest` tests. Without them, authorization gaps (see SEC-01), validation errors, and HTTP status codes are never verified at the HTTP layer.

---

## Files to Create

| File | What to Test |
|------|--------------|
| `src/test/java/net/leozeballos/FastFood/product/ProductRestControllerTest.java` | GET (200), POST (201/400), DELETE (204/403) |
| `src/test/java/net/leozeballos/FastFood/menu/MenuRestControllerTest.java` | GET, POST (validation), DELETE (403 for USER) |
| `src/test/java/net/leozeballos/FastFood/branch/BranchRestControllerTest.java` | GET, POST, DELETE (403 for USER) |

---

## Implementation Steps

### Step 1 — Create ProductRestControllerTest

```java
@WebMvcTest(ProductRestController.class)
@Import(SecurityConfig.class)
class ProductRestControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean ProductService productService;
    @MockBean CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser
    void getAll_authenticated_returns200() throws Exception {
        when(productService.findAllDTO(null, null, null)).thenReturn(List.of());
        mockMvc.perform(get("/api/v1/products"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_asUser_returns403() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1"))
               .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_asAdmin_returns204() throws Exception {
        doNothing().when(productService).deleteById(1L);
        mockMvc.perform(delete("/api/v1/products/1"))
               .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withInvalidBody_returns400() throws Exception {
        String badJson = "{\"price\": -5}";  // missing name, negative price
        mockMvc.perform(post("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
               .andExpect(status().isBadRequest());
    }
}
```

### Step 2 — Create MenuRestControllerTest

Same structure as above, focused on:
- `GET /api/v1/menus` → 200 (authenticated)
- `DELETE /api/v1/menus/{id}` → 403 for USER, 204 for ADMIN
- `POST /api/v1/menus` with invalid discount → 400

### Step 3 — Create BranchRestControllerTest

Same structure. Additional test:
- `GET /api/v1/branches/me` → 200 with correct branchId for USER

---

## Test Command

```bash
mvn clean test -Dtest=ProductRestControllerTest,MenuRestControllerTest,BranchRestControllerTest
```

---

## Acceptance Criteria

- [ ] All three controller test files exist with at minimum 4 tests each
- [ ] Unauthenticated requests return 401
- [ ] `ROLE_USER` DELETE requests return 403 (requires SEC-01 to be implemented first)
- [ ] `ROLE_ADMIN` DELETE requests return 204
- [ ] Invalid request bodies return 400
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
