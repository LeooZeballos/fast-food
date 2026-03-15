# SEC-09 — Open-In-View Profile Mismatch

**Priority:** MEDIUM
**Domain:** Security / Reliability
**Effort:** Tiny (10 min)

---

## Summary

`application.properties` sets `spring.jpa.open-in-view=false` but `application-dev.properties` overrides it to `true`. Code that relies on lazy loading in views (e.g., accessing a `@OneToMany` collection outside a transaction) will work in dev but throw `LazyInitializationException` in production.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/resources/application-dev.properties` | Remove or change `spring.jpa.open-in-view` override |

---

## Implementation Steps

### Step 1 — Read application-dev.properties

Locate the `spring.jpa.open-in-view=true` line.

### Step 2 — Remove or Override to false

```properties
# REMOVE this line entirely, or explicitly set to false:
spring.jpa.open-in-view=false
```

The base `application.properties` already sets `false`, so removing the override is sufficient.

### Step 3 — Run Tests to Find Lazy Loading Regressions

```bash
mvn clean test
```

If any `LazyInitializationException` surfaces, the fix is to add `LEFT JOIN FETCH` to the relevant JPQL query — not to re-enable OSIV.

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] `spring.jpa.open-in-view=false` is effective in all Spring profiles
- [ ] No `LazyInitializationException` in tests
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
