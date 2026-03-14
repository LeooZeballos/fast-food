# PRODUCT-03 — Low Stock Alerts / Reorder Thresholds

**Priority:** LOW
**Domain:** Product / Business Logic
**Effort:** Medium (2h)

---

## Summary

When stock falls below a minimum threshold, branch managers have no way to know. Add a `reorderPoint` field to `Inventory` and an endpoint that returns items below threshold, enabling proactive restocking.

---

## Files to Modify / Create

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/inventory/Inventory.java` | Add `reorderPoint` field |
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryRepository.java` | Add low-stock query |
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryService.java` | Add `findLowStock()` method |
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryRestController.java` | Add `GET /inventory/branch/{id}/low-stock` |
| `src/main/resources/db/migration/V{next}__add_reorder_point.sql` | Flyway migration |
| `frontend/src/api.ts` | Add `getLowStockByBranch()` function |

---

## Implementation Steps

### Step 1 — Add reorderPoint to Inventory Entity

```java
@Column(nullable = false)
private int reorderPoint = 0;  // 0 means no threshold set
```

### Step 2 — Flyway Migration

```sql
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS reorder_point INT DEFAULT 0 NOT NULL;
```

### Step 3 — Repository Query

```java
@Query("SELECT i FROM Inventory i " +
       "LEFT JOIN FETCH i.item " +
       "WHERE i.branch.id = :branchId " +
       "AND i.reorderPoint > 0 " +
       "AND i.stockQuantity <= i.reorderPoint")
List<Inventory> findLowStockByBranch(@Param("branchId") Long branchId);
```

### Step 4 — Service Method

```java
public List<Inventory> findLowStock(Long branchId) {
    return inventoryRepository.findLowStockByBranch(branchId);
}
```

### Step 5 — Controller Endpoint

```java
@GetMapping("/branch/{branchId}/low-stock")
@Operation(summary = "Get low-stock items", description = "Returns items at or below their reorder point")
public List<InventoryDTO> getLowStock(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long branchId) {
    Long effectiveBranchId = getEffectiveBranchId(userDetails);
    if (effectiveBranchId != null && !effectiveBranchId.equals(branchId)) {
        throw new AccessDeniedException("Access denied");
    }
    return inventoryService.findLowStock(branchId).stream()
            .map(inventoryMapper::toDTO)
            .toList();
}
```

### Step 6 — Frontend (optional badge in AdminPanel)

In `frontend/src/api.ts`:
```typescript
export const getLowStockByBranch = async (branchId: number) => {
  const response = await api.get<InventoryDTO[]>(`/inventory/branch/${branchId}/low-stock`);
  return response.data;
};
```

Show a warning badge on the Admin Panel inventory tab when items are low.

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] `reorderPoint` field exists on `Inventory` entity and DB table
- [ ] `GET /api/v1/inventory/branch/{id}/low-stock` returns only items at or below their reorder point
- [ ] Items with `reorderPoint = 0` are excluded (threshold not set)
- [ ] Branch-scoped access control applies to this endpoint
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
