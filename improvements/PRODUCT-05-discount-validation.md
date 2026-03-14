# PRODUCT-05 — Discount Percentage Validation

**Priority:** MEDIUM
**Domain:** Product / Business Logic
**Effort:** Small (30 min)

---

## Summary

Menu `discountPercentage` has no range validation. A staff member entering `1.5` (intending 15%) instead of `0.15` creates a menu with a 150% discount, giving items away for a negative price.

---

## Context

The `Menu` entity stores discount as a `BigDecimal` between `0.0` (0%) and `1.0` (100%). Validation needs to be on the DTO (once SEC-06 is implemented) or directly on the entity until then.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/menu/MenuCreateDTO.java` | Add `@DecimalMin`/`@DecimalMax` (after SEC-06) |
| `src/main/java/net/leozeballos/FastFood/menu/Menu.java` | Add validation annotations on entity field as fallback |
| `src/main/java/net/leozeballos/FastFood/menu/MenuService.java` | Add business-level guard |

---

## Implementation Steps

### Step 1 — Validate on DTO (Primary, requires SEC-06)

In `MenuCreateDTO`:
```java
@NotNull
@DecimalMin(value = "0.0", message = "Discount must be at least 0%")
@DecimalMax(value = "1.0", message = "Discount cannot exceed 100%")
BigDecimal discountPercentage
```

### Step 2 — Validate on Entity (Defense in Depth)

In `Menu.java`:
```java
@Column(nullable = false)
@DecimalMin("0.0")
@DecimalMax("1.0")
private BigDecimal discountPercentage;
```

Note: JPA entity validation (`@Valid` on `@RequestBody`) triggers these annotations if Spring Boot's `javax.validation` is on the classpath.

### Step 3 — Business Guard in MenuService (without DTO)

If SEC-06 is not yet done, add a guard in `MenuService.save()`:

```java
public Menu save(Menu menu) {
    if (menu.getDiscountPercentage() != null) {
        BigDecimal d = menu.getDiscountPercentage();
        if (d.compareTo(BigDecimal.ZERO) < 0 || d.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                "Discount percentage must be between 0.0 and 1.0, got: " + d);
        }
    }
    return menuRepository.save(menu);
}
```

---

## Test Command

```bash
mvn clean test -Dtest=MenuServiceTest
```

---

## Acceptance Criteria

- [ ] Creating a menu with `discountPercentage = 1.5` returns HTTP 400 with validation error
- [ ] Creating a menu with `discountPercentage = -0.1` returns HTTP 400
- [ ] Creating a menu with `discountPercentage = 0.15` (15%) succeeds
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
