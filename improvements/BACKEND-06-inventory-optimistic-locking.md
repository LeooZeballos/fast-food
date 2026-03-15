# BACKEND-06 — Optimistic Locking Missing on Inventory Entity

**Priority:** HIGH
**Domain:** Backend / Data Integrity
**Effort:** Small (30 min)

---

## Summary

`FoodOrder` has `@Version` for optimistic locking, but `Inventory` does not. Concurrent stock decrements can race even within a single transaction, resulting in incorrect stock counts. This is the secondary defense after SEC-04 (atomic UPDATE query).

---

## Context

If SEC-04 (atomic `UPDATE` query) is implemented, this becomes a defense-in-depth measure. If SEC-04 is not yet done, this is critical. Both can coexist: the atomic query prevents overselling, and `@Version` prevents dirty writes on other `Inventory` fields (e.g., `isAvailable`).

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/inventory/Inventory.java` | Add `@Version` field |
| `src/main/resources/db/migration/V{next}__add_inventory_version.sql` | Add `version` column to table |

---

## Implementation Steps

### Step 1 — Read Inventory.java

Confirm the entity's current fields and that there is no `version` field yet.

### Step 2 — Add @Version to Inventory

```java
@Version
private Long version;
```

Add this field alongside existing fields. Lombok's `@Getter`/`@Setter` will expose it automatically.

### Step 3 — Create Flyway Migration

```sql
ALTER TABLE inventory ADD COLUMN IF NOT EXISTS version BIGINT DEFAULT 0 NOT NULL;
```

Use the next sequential version number for the migration file.

### Step 4 — Handle OptimisticLockingFailureException in FoodOrderService

When two concurrent requests try to update the same `Inventory` row, one will throw `OptimisticLockingFailureException`. Catch it at the service or controller level:

```java
// In GlobalExceptionHandler.java
@ExceptionHandler(OptimisticLockingFailureException.class)
public ProblemDetail handleOptimisticLock(OptimisticLockingFailureException ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    problem.setTitle("Concurrent Modification");
    problem.setDetail("Another request modified this resource. Please retry.");
    return problem;
}
```

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] `Inventory` entity has a `@Version Long version` field
- [ ] Flyway migration adds the `version` column
- [ ] Concurrent update of same `Inventory` row results in `OptimisticLockingFailureException` for the loser
- [ ] `GlobalExceptionHandler` returns HTTP 409 for `OptimisticLockingFailureException`
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
