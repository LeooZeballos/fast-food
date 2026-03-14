# SEC-04 — Race Condition on Stock Decrement (Overselling)

**Priority:** CRITICAL
**Domain:** Security / Data Integrity
**Effort:** Medium (2h)

---

## Summary

`FoodOrderService.createOrder()` checks stock availability and then decrements it in two separate DB round-trips. A concurrent order creation request can pass the availability check and consume the last unit between the check and decrement, resulting in negative inventory (overselling).

---

## Context

```java
// Line 75 — checks if stock >= quantity
if (!inventoryService.isItemAvailable(...)) throw ...

// Line 93 — decrements stock (separate transaction call)
inventoryService.decrementStock(...);
```

Between these two calls, another thread can:
1. Also pass `isItemAvailable()` (still > 0)
2. Both threads then call `decrementStock()` — stock goes negative

The `@Version` optimistic lock on `FoodOrder` does **not** protect `Inventory`.

---

## Solution Options

**Option A (Recommended): Atomic UPDATE with affected-rows check**
Replace the two-step check+decrement with a single `UPDATE inventory SET stock_quantity = stock_quantity - :qty WHERE branch_id = :branchId AND item_id = :itemId AND stock_quantity >= :qty AND is_available = true`.
Check `affectedRows == 1`; if 0, stock was insufficient.

**Option B: Add `@Version` to `Inventory`** (also tracked in BACKEND-06)
Causes `OptimisticLockingFailureException` on concurrent updates, which the caller must retry. Less ideal UX under load.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryRepository.java` | Add atomic decrement query |
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryService.java` | Replace check+decrement with atomic call |
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderService.java` | Remove separate `isItemAvailable` loop |
| `src/test/java/net/leozeballos/FastFood/inventory/InventoryServiceTest.java` | Add concurrent decrement test |

---

## Implementation Steps

### Step 1 — Add Atomic Decrement Query to Repository

```java
@Modifying
@Query("UPDATE Inventory i SET i.stockQuantity = i.stockQuantity - :quantity " +
       "WHERE i.branch.id = :branchId AND i.item.id = :itemId " +
       "AND i.stockQuantity >= :quantity AND i.isAvailable = true")
int atomicDecrement(@Param("branchId") Long branchId,
                    @Param("itemId") Long itemId,
                    @Param("quantity") int quantity);
```

### Step 2 — Update InventoryService

Replace `isItemAvailable()` + `decrementStock()` with:

```java
@Transactional
public void atomicDecrementOrThrow(Long branchId, Long itemId, int quantity) {
    int affected = inventoryRepository.atomicDecrement(branchId, itemId, quantity);
    if (affected == 0) {
        throw new IllegalStateException(
            "Insufficient stock for item id: " + itemId + " at branch: " + branchId);
    }
}
```

### Step 3 — Update FoodOrderService.createOrder()

Remove the pre-check loop and replace decrementStock calls:

```java
@Transactional
public FoodOrder createOrder(CreateOrderDTO createOrderDTO) {
    FoodOrder order = new FoodOrder();
    order.setState(FoodOrderState.CREATED);
    order.setBranch(branchService.findById(createOrderDTO.branchId()));

    List<FoodOrderDetail> details = createOrderDTO.items().stream()
            .map(itemDTO -> {
                // Atomic: check AND decrement in one SQL statement
                inventoryService.atomicDecrementOrThrow(
                    createOrderDTO.branchId(), itemDTO.itemId(), itemDTO.quantity());

                FoodOrderDetail detail = new FoodOrderDetail();
                var item = itemService.findById(itemDTO.itemId());
                detail.setItem(item);
                detail.setQuantity(itemDTO.quantity());
                detail.setHistoricPrice(item.calculatePrice());
                return detail;
            })
            .collect(Collectors.toList());

    order.setFoodOrderDetails(details);
    return foodOrderRepository.save(order);
}
```

### Step 4 — Test

```java
@Test
void createOrder_withInsufficientStock_throwsIllegalStateException() {
    // inventory has 1 unit, order requests 5
    assertThrows(IllegalStateException.class, () ->
        foodOrderService.createOrder(createOrderDTOWithQuantity(5)));
}
```

---

## Test Command

```bash
mvn clean test -Dtest=FoodOrderServiceIntegrationTest,InventoryServiceTest
```

---

## Acceptance Criteria

- [ ] Stock availability check and decrement happen in a single atomic SQL statement
- [ ] Concurrent order creation for the last unit results in exactly one success and one failure
- [ ] `IllegalStateException` is thrown when stock is insufficient
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
