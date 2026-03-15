# BACKEND-05 — Cache Invalidation Strategy (allEntries Thrashing)

**Priority:** MEDIUM
**Domain:** Backend / Performance
**Effort:** Medium (1.5h)

---

## Summary

All `@CacheEvict` annotations use `allEntries = true`, which clears the entire cache namespace on every write. Under concurrent product/menu updates, the cache is perpetually cold, providing no benefit and adding overhead.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/product/ProductService.java` | Switch to key-based eviction |
| `src/main/java/net/leozeballos/FastFood/menu/MenuService.java` | Switch to key-based eviction |
| `src/main/java/net/leozeballos/FastFood/branch/BranchService.java` | Switch to key-based eviction |

---

## Implementation Steps

### Step 1 — Read Each Service

Read `ProductService.java`, `MenuService.java`, and `BranchService.java` to identify all `@Cacheable` and `@CacheEvict` annotations and their current keys.

### Step 2 — Switch to Key-Based Eviction

**Before:**
```java
@CacheEvict(value = "products", allEntries = true)
public Product save(Product product) { ... }
```

**After:**
```java
@Caching(evict = {
    @CacheEvict(value = "products", key = "#result.id"),  // evict saved entity by its ID
    @CacheEvict(value = "products", key = "'all'")         // evict the "all products" list
})
public Product save(Product product) { ... }
```

### Step 3 — Use Consistent Cache Keys

Ensure `findAllDTO()` is cached with a predictable key:

```java
@Cacheable(value = "products", key = "'all'")
public List<ProductDTO> findAllDTO() { ... }

@Cacheable(value = "products", key = "#id")
public ProductDTO findDTOById(Long id) { ... }
```

### Step 4 — Repeat for Menu and Branch

Apply the same pattern to `MenuService` and `BranchService`.

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] Updating one product does not evict the entire product cache
- [ ] `findAllDTO()` still returns fresh data after a product is saved/deleted
- [ ] Cache hit rate improves under concurrent read/write load (verify via Actuator `/actuator/caches`)
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
