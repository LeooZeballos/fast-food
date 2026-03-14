# ARCH-01 — Controllers Accepting JPA Entities Directly

**Priority:** MEDIUM
**Domain:** Architect
**Effort:** Large (3h)

---

## Summary

`ProductRestController.create()` and `ProductRestController.update()` accept `@RequestBody Product product` — a JPA entity — instead of a DTO. This leaks the entity contract to the API, allows clients to set internal fields (`id`, `version`), and couples the HTTP interface to the DB schema.

---

## Context

`FoodOrderRestController` already uses `CreateOrderDTO` as the request body. `ProductRestController` should follow the same pattern.

---

## Files to Create / Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/product/ProductCreateDTO.java` | New record |
| `src/main/java/net/leozeballos/FastFood/product/ProductUpdateDTO.java` | New record |
| `src/main/java/net/leozeballos/FastFood/mapper/ProductMapper.java` | Add `fromCreateDTO`, `fromUpdateDTO` |
| `src/main/java/net/leozeballos/FastFood/product/ProductRestController.java` | Use new DTOs |
| `src/main/java/net/leozeballos/FastFood/product/ProductService.java` | Accept DTO or entity |

---

## Implementation Steps

### Step 1 — Create ProductCreateDTO

```java
public record ProductCreateDTO(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 100) String nameEs,
    @NotNull @DecimalMin("0.0") BigDecimal price,
    @Size(max = 10) String icon,
    @Size(max = 500) String imageUrl
) {}
```

### Step 2 — Create ProductUpdateDTO

```java
public record ProductUpdateDTO(
    @NotBlank @Size(max = 100) String name,
    @Size(max = 100) String nameEs,
    @NotNull @DecimalMin("0.0") BigDecimal price,
    @Size(max = 10) String icon,
    @Size(max = 500) String imageUrl
) {}
```

### Step 3 — Add Mapper Methods

```java
public Product fromCreateDTO(ProductCreateDTO dto) {
    Product product = new Product();
    product.setName(dto.name());
    product.setNameEs(dto.nameEs());
    product.setPrice(dto.price());
    product.setIcon(dto.icon());
    product.setImageUrl(dto.imageUrl());
    return product;
}

public void applyUpdateDTO(ProductUpdateDTO dto, Product existing) {
    existing.setName(dto.name());
    existing.setNameEs(dto.nameEs());
    existing.setPrice(dto.price());
    existing.setIcon(dto.icon());
    existing.setImageUrl(dto.imageUrl());
}
```

### Step 4 — Update Controller

```java
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public ProductDTO create(@Valid @RequestBody ProductCreateDTO dto) {
    Product product = productMapper.fromCreateDTO(dto);
    return productMapper.toDTO(productService.save(product));
}

@PutMapping("/{id}")
public ProductDTO update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO dto) {
    Product existing = productService.findById(id);
    productMapper.applyUpdateDTO(dto, existing);
    return productMapper.toDTO(productService.save(existing));
}
```

### Step 5 — Verify Frontend Compatibility

Check `frontend/src/api.ts` `createProduct()` and `updateProduct()` — the fields sent must match `ProductCreateDTO`. The existing `Partial<ProductDTO>` type sent from the frontend should already match.

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] `ProductRestController.create()` accepts `ProductCreateDTO`, not `Product`
- [ ] `ProductRestController.update()` accepts `ProductUpdateDTO`, not `Product`
- [ ] Clients cannot set `id` or `version` via the API
- [ ] Invalid payloads (missing name) return 400 with validation details
- [ ] Existing product CRUD still works end-to-end
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
