# PRODUCT-04 — Prevent Menu/Product Modification Under Active Orders

**Priority:** MEDIUM
**Domain:** Product / Data Integrity
**Effort:** Medium (1.5h)

---

## Summary

`ProductService.disableItem()` removes a product from menus and marks it inactive, but does not check whether any open orders (in `CREATED` or `INPREPARATION` state) contain that product. Disabling a product mid-order creates inconsistency between what the kitchen received and what the order record shows.

---

## Business Rule

> A product cannot be disabled if it appears in any order currently in `CREATED` or `INPREPARATION` state.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/product/ProductService.java` | Add active-order check before disable |
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderRepository.java` | Add query to find open orders containing an item |
| `src/test/java/net/leozeballos/FastFood/product/ProductServiceTest.java` | Add test for blocked disable |

---

## Implementation Steps

### Step 1 — Add Repository Query for Open Orders with Item

```java
@Query("SELECT COUNT(fo) FROM FoodOrder fo " +
       "JOIN fo.foodOrderDetails fod " +
       "WHERE fod.item.id = :itemId " +
       "AND fo.state IN ('CREATED', 'INPREPARATION')")
long countOpenOrdersContainingItem(@Param("itemId") Long itemId);
```

### Step 2 — Inject FoodOrderRepository into ProductService

```java
private final FoodOrderRepository foodOrderRepository;
```

### Step 3 — Guard disableItem()

```java
@Transactional
public void disableItem(Long id) {
    Product product = findById(id);

    long openOrders = foodOrderRepository.countOpenOrdersContainingItem(id);
    if (openOrders > 0) {
        throw new IllegalStateException(
            "Cannot disable product '" + product.getName() + "': it is part of " +
            openOrders + " active order(s). Complete or cancel those orders first.");
    }

    // Existing logic
    List<Menu> menus = new ArrayList<>(menuRepository.findAll());
    for (Menu menu : menus) {
        menu.getItems().remove(product);
    }
    product.disable();
    productRepository.save(product);
}
```

### Step 4 — The Exception Will Surface as 400

`GlobalExceptionHandler` should already handle `IllegalStateException`. Verify it returns HTTP 400 with an RFC 7807 `ProblemDetail`.

### Step 5 — Test

```java
@Test
void disableItem_withOpenOrders_throwsIllegalStateException() {
    // Given: product is in an open order
    when(foodOrderRepository.countOpenOrdersContainingItem(1L)).thenReturn(2L);
    // When/Then:
    assertThrows(IllegalStateException.class, () -> productService.disableItem(1L));
}

@Test
void disableItem_withNoOpenOrders_succeeds() {
    when(foodOrderRepository.countOpenOrdersContainingItem(1L)).thenReturn(0L);
    // Should complete without exception
    productService.disableItem(1L);
    verify(productRepository).save(any());
}
```

---

## Test Command

```bash
mvn clean test -Dtest=ProductServiceTest
```

---

## Acceptance Criteria

- [ ] Attempting to disable a product in an active order returns HTTP 400 with a descriptive message
- [ ] Disabling a product not in any active order still works correctly
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
