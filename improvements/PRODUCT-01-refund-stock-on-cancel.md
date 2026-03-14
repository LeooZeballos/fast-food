# PRODUCT-01 — Refund Stock on Order Cancel / Reject

**Priority:** CRITICAL
**Domain:** Product / Data Integrity
**Effort:** Medium (1.5h)

---

## Summary

When an order is cancelled or rejected, the inventory stock that was decremented during order creation is **never restored**. Every cancelled or rejected order permanently removes stock from inventory, causing inaccurate counts over time.

---

## Context

In `FoodOrderService.createOrder()`, stock is decremented for each item:
```java
inventoryService.decrementStock(branchId, itemId, quantity);
```

In `FoodOrderService.cancel()` and `reject()`, only the state machine transition fires — no stock restoration. Stock should be refunded for all `FoodOrderDetail` items of the order.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryService.java` | Add `incrementStock()` method |
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryRepository.java` | Add increment query |
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderService.java` | Call increment in `cancel()` and `reject()` |
| `src/test/java/net/leozeballos/FastFood/foodorder/FoodOrderServiceIntegrationTest.java` | Add stock-restoration test |

---

## Implementation Steps

### Step 1 — Add incrementStock to Repository

```java
@Modifying
@Query("UPDATE Inventory i SET i.stockQuantity = i.stockQuantity + :quantity " +
       "WHERE i.branch.id = :branchId AND i.item.id = :itemId")
int incrementStock(@Param("branchId") Long branchId,
                   @Param("itemId") Long itemId,
                   @Param("quantity") int quantity);
```

### Step 2 — Add incrementStock to InventoryService

```java
@Transactional
public void incrementStock(Long branchId, Long itemId, int quantity) {
    int affected = inventoryRepository.incrementStock(branchId, itemId, quantity);
    if (affected == 0) {
        log.warn("Could not restore stock for item {} at branch {} — inventory record not found.", itemId, branchId);
    }
}
```

### Step 3 — Create Helper in FoodOrderService

```java
private void restoreStockForOrder(FoodOrder order) {
    Long branchId = order.getBranch().getId();
    for (FoodOrderDetail detail : order.getFoodOrderDetails()) {
        inventoryService.incrementStock(
            branchId,
            detail.getItem().getId(),
            detail.getQuantity()
        );
    }
}
```

### Step 4 — Call restoreStockForOrder in cancel() and reject()

```java
@Transactional
public StateMachine<FoodOrderState, FoodOrderEvent> cancel(Long id, Long branchId) {
    FoodOrder order = findById(id, branchId);
    restoreStockForOrder(order);                    // ← ADD THIS
    StateMachine<...> sm = build(id);
    sendEvent(id, sm, FoodOrderEvent.CANCEL);
    return sm;
}

@Transactional
public StateMachine<FoodOrderState, FoodOrderEvent> reject(Long id, Long branchId) {
    FoodOrder order = findById(id, branchId);
    restoreStockForOrder(order);                    // ← ADD THIS
    StateMachine<...> sm = build(id);
    sendEvent(id, sm, FoodOrderEvent.REJECT);
    return sm;
}
```

### Step 5 — Test

```java
@Test
void cancelOrder_restoresInventoryStock() {
    // Given: order placed for 2x Item A at branch 1 (stock was 5, now 3)
    // When: order is cancelled
    foodOrderService.cancel(orderId, branchId);
    // Then: stock at branch 1 for Item A is 5 again
    int stock = inventoryRepository.findByBranchIdAndItemId(branchId, itemId)
                                   .get().getStockQuantity();
    assertEquals(5, stock);
}
```

---

## Test Command

```bash
mvn clean test -Dtest=FoodOrderServiceIntegrationTest,InventoryServiceTest
```

---

## Acceptance Criteria

- [ ] Cancelling an order restores stock for all ordered items
- [ ] Rejecting an order restores stock for all ordered items
- [ ] Stock restoration is transactional with the state change (both succeed or both fail)
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
