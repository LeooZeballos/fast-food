# SEC-03 — Inventory Update Missing Branch Authorization

**Priority:** HIGH
**Domain:** Security
**Effort:** Small (30 min)

---

## Summary

`InventoryRestController`'s stock update endpoint has no branch ownership check. Any authenticated user can update inventory for any branch, bypassing the branch-scoping pattern enforced in every other controller.

---

## Context

`FoodOrderRestController`, `BranchRestController`, and others use `getEffectiveBranchId(userDetails)` to scope queries. `InventoryRestController` exposes a `PATCH` or `POST` to update stock quantities but does not verify that the requesting user's `branchId` matches the target branch. The fix follows the exact same pattern already established in the codebase.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryRestController.java` | Inject `@AuthenticationPrincipal`, add branch ownership check |
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryService.java` | Add `branchId` parameter to update method if needed |
| `src/test/java/net/leozeballos/FastFood/inventory/InventoryRestControllerTest.java` | Add authorization tests |

---

## Implementation Steps

### Step 1 — Read the Controller

Read `InventoryRestController.java` to identify the exact update endpoint signature.

### Step 2 — Add Authentication Principal

Add `@AuthenticationPrincipal CustomUserDetails userDetails` to the update endpoint parameter list.

### Step 3 — Implement Branch Check

Add the same helper methods used in `FoodOrderRestController`:

```java
private Long getEffectiveBranchId(CustomUserDetails userDetails) {
    if (isAdmin(userDetails)) return null;
    return userDetails != null ? userDetails.getBranchId() : null;
}

private boolean isAdmin(CustomUserDetails userDetails) {
    return userDetails != null && userDetails.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
}
```

In the update handler:

```java
public ResponseEntity<?> updateInventory(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long branchId,
        @RequestBody InventoryUpdateDTO dto) {

    Long effectiveBranchId = getEffectiveBranchId(userDetails);
    // Non-admins can only update their own branch
    if (effectiveBranchId != null && !effectiveBranchId.equals(branchId)) {
        throw new AccessDeniedException("User does not have access to this branch's inventory");
    }
    // proceed with update...
}
```

### Step 4 — Add Tests

```java
@Test
@WithMockUser(username = "staff", roles = "USER")
void updateInventory_wrongBranch_returns403() throws Exception {
    // user branchId = 1, trying to update branch 2
    mockMvc.perform(patch("/api/v1/inventory/branch/2/item/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"stockQuantity\": 10}"))
           .andExpect(status().isForbidden());
}
```

---

## Test Command

```bash
mvn clean test -Dtest=InventoryRestControllerTest
```

---

## Acceptance Criteria

- [ ] Non-admin user cannot update inventory for a branch they are not assigned to (403)
- [ ] Admin can update inventory for any branch (200)
- [ ] Non-admin can update inventory for their own branch (200)
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
