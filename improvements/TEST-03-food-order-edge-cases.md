# TEST-03 — Missing Edge Cases in FoodOrderServiceTest

**Priority:** HIGH
**Domain:** Tester
**Effort:** Medium (2h)

---

## Summary

`FoodOrderServiceTest.java` does not test: (1) order creation with insufficient stock, (2) order creation for items missing from inventory, (3) stock restoration on cancel/reject (the current bug documented in PRODUCT-01).

---

## Files to Modify

| File | Change |
|------|--------|
| `src/test/java/net/leozeballos/FastFood/foodorder/FoodOrderServiceTest.java` | Add edge case tests |
| `src/test/java/net/leozeballos/FastFood/foodorder/FoodOrderServiceIntegrationTest.java` | Add integration tests using H2 |

---

## Implementation Steps

### Step 1 — Read Existing Test File

Read `FoodOrderServiceTest.java` to understand current test structure, mocking setup, and helper methods.

### Step 2 — Add: Create Order with Insufficient Stock

```java
@Test
void createOrder_insufficientStock_throwsIllegalStateException() {
    // Given: inventory has 0 units (or atomicDecrement returns 0)
    when(inventoryService.atomicDecrementOrThrow(anyLong(), anyLong(), anyInt()))
        .thenThrow(new IllegalStateException("Insufficient stock"));

    CreateOrderDTO dto = new CreateOrderDTO(1L, List.of(new CreateOrderDTO.ItemOrder(1L, 5)));

    assertThrows(IllegalStateException.class, () -> foodOrderService.createOrder(dto));
    // Verify order was NOT saved
    verify(foodOrderRepository, never()).save(any());
}
```

### Step 3 — Add: Create Order for Item Not in Inventory

```java
@Test
void createOrder_itemNotInInventory_throwsResourceNotFoundException() {
    when(inventoryService.atomicDecrementOrThrow(anyLong(), anyLong(), anyInt()))
        .thenThrow(new ResourceNotFoundException("Item not found in branch inventory"));

    CreateOrderDTO dto = new CreateOrderDTO(1L, List.of(new CreateOrderDTO.ItemOrder(999L, 1)));

    assertThrows(ResourceNotFoundException.class, () -> foodOrderService.createOrder(dto));
}
```

### Step 4 — Add: Cancel Restores Stock (Integration Test)

This should be an integration test with H2 to verify the full transaction:

```java
@Test
@Transactional
void cancelOrder_restoresInventoryStock() {
    // Given: a saved order in CREATED state
    FoodOrder order = createAndSaveTestOrder(branchId, itemId, quantity = 3);
    int stockBefore = getStock(branchId, itemId);

    // When: cancelled
    foodOrderService.cancel(order.getId(), null);

    // Then: stock is restored
    int stockAfter = getStock(branchId, itemId);
    assertEquals(stockBefore + 3, stockAfter);
}
```

### Step 5 — Add: State Machine Transition Tests

```java
@Test
void startPreparation_onCreatedOrder_succeeds() { ... }

@Test
void startPreparation_onAlreadyPaidOrder_throwsException() {
    // Should throw IllegalStateException (invalid transition)
}
```

---

## Test Command

```bash
mvn clean test -Dtest=FoodOrderServiceTest,FoodOrderServiceIntegrationTest
```

---

## Acceptance Criteria

- [ ] Test for insufficient stock: verifies `IllegalStateException` and no DB save
- [ ] Test for missing inventory item: verifies `ResourceNotFoundException`
- [ ] Integration test for cancel: verifies stock restored in DB
- [ ] Integration test for reject: verifies stock restored in DB
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
