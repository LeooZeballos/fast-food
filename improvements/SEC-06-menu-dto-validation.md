# SEC-06 — Unvalidated Map in MenuRestController

**Priority:** HIGH
**Domain:** Security / Architecture
**Effort:** Medium (1.5h)

---

## Summary

`MenuRestController` accepts `@RequestBody Map<String, Object>` for create/update operations with no `@Valid` annotation. This bypasses Bean Validation entirely, allowing arbitrary fields, missing required fields, and out-of-range values (e.g., 500% discount).

---

## Context

All other controllers use typed DTOs with validation. `MenuRestController` is the exception. This also makes the API contract implicit (no OpenAPI schema for the request body), making it harder for frontend developers to know what fields are accepted.

---

## Files to Create / Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/menu/MenuCreateDTO.java` | New record |
| `src/main/java/net/leozeballos/FastFood/menu/MenuUpdateDTO.java` | New record |
| `src/main/java/net/leozeballos/FastFood/menu/MenuRestController.java` | Use new DTOs |
| `src/main/java/net/leozeballos/FastFood/menu/MenuService.java` | Accept DTO in create/update |
| `src/main/java/net/leozeballos/FastFood/mapper/MenuMapper.java` | Add DTO → entity mapping |

---

## Implementation Steps

### Step 1 — Create MenuCreateDTO

```java
package net.leozeballos.FastFood.menu;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MenuCreateDTO(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 100) String nameEs,
    @NotNull @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal discountPercentage,
    @Size(max = 10) String icon,
    @Size(max = 500) String imageUrl
) {}
```

### Step 2 — Create MenuUpdateDTO

Same fields as `MenuCreateDTO` but all optional (use `@Nullable`). Or reuse `MenuCreateDTO` and treat all fields as replacements.

### Step 3 — Update MenuMapper

```java
public Menu fromCreateDTO(MenuCreateDTO dto) {
    Menu menu = new Menu();
    menu.setName(dto.name());
    menu.setNameEs(dto.nameEs());
    menu.setDiscountPercentage(dto.discountPercentage());
    menu.setIcon(dto.icon());
    menu.setImageUrl(dto.imageUrl());
    return menu;
}
```

### Step 4 — Update MenuRestController

```java
@PostMapping
public MenuDTO create(@Valid @RequestBody MenuCreateDTO dto) {
    Menu menu = menuMapper.fromCreateDTO(dto);
    return menuMapper.toDTO(menuService.save(menu));
}

@PutMapping("/{id}")
public MenuDTO update(@PathVariable Long id, @Valid @RequestBody MenuUpdateDTO dto) {
    // find existing, apply changes, save
}
```

### Step 5 — Verify Frontend Compatibility

Check `frontend/src/api.ts` `createMenu()` / `updateMenu()` calls send fields matching the new DTO. The existing `MenuDTO` type should already cover the required fields.

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] `POST /api/v1/menus` with missing `name` returns 400 with validation error details
- [ ] `POST /api/v1/menus` with `discountPercentage = 1.5` (150%) returns 400
- [ ] Valid menu creation still works (201)
- [ ] OpenAPI docs show request body schema for menu endpoints
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
