# ARCH-02 — Inconsistent Mapper Usage

**Priority:** LOW
**Domain:** Architect
**Effort:** Medium (2h)

---

## Summary

`FoodOrderService` and `ProductService` use dedicated mapper classes. `BranchService` and `MenuService` do inline object mapping inside service methods. This inconsistency makes the codebase harder to navigate and evolve.

---

## Files to Create / Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/mapper/MenuMapper.java` | Create (move inline mapping from MenuService) |
| `src/main/java/net/leozeballos/FastFood/mapper/BranchMapper.java` | Expand (partially exists) |
| `src/main/java/net/leozeballos/FastFood/menu/MenuService.java` | Use MenuMapper |
| `src/main/java/net/leozeballos/FastFood/branch/BranchService.java` | Use BranchMapper |

---

## Implementation Steps

### Step 1 — Read Existing Mappers

Read `ProductMapper.java` and `FoodOrderMapper.java` to understand the pattern. Read `BranchMapper.java` to see what already exists.

### Step 2 — Create or Expand MenuMapper

```java
@Component
public class MenuMapper {

    public MenuDTO toDTO(Menu menu) {
        if (menu == null) return null;
        return MenuDTO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .nameEs(menu.getNameEs())
                .price(menu.calculatePrice())
                .discountPercentage(menu.getDiscountPercentage().doubleValue())
                .productsList(buildProductsList(menu))
                .icon(menu.getIcon())
                .imageUrl(menu.getImageUrl())
                .active(menu.isActive())
                .build();
    }

    private String buildProductsList(Menu menu) {
        if (menu.getItems() == null) return "";
        return menu.getItems().stream()
                .map(Item::getName)
                .collect(Collectors.joining(", "));
    }
}
```

### Step 3 — Expand BranchMapper

Read `BranchMapper.java`. It likely has a `toDTO` but may be missing `toEntity`. Add `toEntity(BranchDTO dto)` if missing.

### Step 4 — Update MenuService

Replace inline mapping calls:
```java
// Before:
return MenuDTO.builder().id(menu.getId())...build();

// After:
return menuMapper.toDTO(menu);
```

Inject `MenuMapper` via constructor.

### Step 5 — Update BranchService

Same pattern — replace inline DTO construction with `branchMapper.toDTO(branch)`.

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] All four major domains (Product, Menu, Branch, FoodOrder) use dedicated mapper classes
- [ ] No inline `DTO.builder()` calls remain inside service methods
- [ ] Mapper classes are `@Component`s injected via constructor
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
