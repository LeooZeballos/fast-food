# PRODUCT-06 — Missing Inventory Restock Endpoint

**Priority:** MEDIUM
**Domain:** Product / Operations
**Effort:** Medium (1.5h)

---

## Summary

There is no API endpoint to increase stock (e.g., when new inventory arrives). `InventoryRestController` has read and update endpoints but nothing purpose-built for restocking. Branch managers currently have no way to increment stock without directly editing the raw inventory record.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryRestController.java` | Add `POST /inventory/branch/{branchId}/item/{itemId}/restock` |
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryService.java` | Add `restock()` method (uses PRODUCT-01's `incrementStock`) |
| `frontend/src/api.ts` | Add `restockItem()` function |
| `frontend/src/locales/en.json` + `es.json` | Add i18n key `admin.inventory.restock` |

---

## Implementation Steps

### Step 1 — Create RestockDTO

```java
public record RestockDTO(
    @NotNull @Min(1) Integer quantity
) {}
```

### Step 2 — Add Service Method

```java
@Transactional
public Inventory restock(Long branchId, Long itemId, int quantity) {
    Inventory inventory = inventoryRepository.findByBranchIdAndItemId(branchId, itemId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Inventory not found for branch " + branchId + " and item " + itemId));
    inventory.setStockQuantity(inventory.getStockQuantity() + quantity);
    return inventoryRepository.save(inventory);
}
```

### Step 3 — Add Controller Endpoint

```java
@PostMapping("/branch/{branchId}/item/{itemId}/restock")
@Operation(summary = "Restock an item", description = "Increases stock quantity for an item at a branch")
public InventoryDTO restock(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @PathVariable Long branchId,
        @PathVariable Long itemId,
        @Valid @RequestBody RestockDTO dto) {

    Long effectiveBranchId = getEffectiveBranchId(userDetails);
    if (effectiveBranchId != null && !effectiveBranchId.equals(branchId)) {
        throw new AccessDeniedException("Access denied to this branch");
    }
    Inventory updated = inventoryService.restock(branchId, itemId, dto.quantity());
    return inventoryMapper.toDTO(updated);
}
```

### Step 4 — Frontend API Function

```typescript
export const restockItem = async (branchId: number, itemId: number, quantity: number) => {
  const response = await api.post<InventoryDTO>(
    `/inventory/branch/${branchId}/item/${itemId}/restock`,
    { quantity }
  );
  return response.data;
};
```

### Step 5 — Add i18n Keys

`en.json`:
```json
"restock": "Restock",
"restockSuccess": "Stock updated successfully"
```

`es.json`:
```json
"restock": "Reabastecer",
"restockSuccess": "Stock actualizado correctamente"
```

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] `POST /api/v1/inventory/branch/1/item/2/restock` with `{"quantity": 10}` increases stock by 10
- [ ] Non-admin user cannot restock a different branch (403)
- [ ] Restocking a non-existent inventory record returns 404
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
