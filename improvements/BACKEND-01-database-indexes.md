# BACKEND-01 — Missing Database Indexes on High-Traffic Columns

**Priority:** HIGH
**Domain:** Backend / Performance
**Effort:** Small (45 min)

---

## Summary

PostgreSQL does not auto-create indexes on foreign key columns. `food_order.branch_id`, `food_order.state`, and `inventory(branch_id, item_id)` are used in every filtered query but have no indexes, causing full table scans as data grows.

---

## Files to Modify / Create

| File | Change |
|------|--------|
| `src/main/resources/db/migration/V{next}__add_performance_indexes.sql` | New Flyway migration |

---

## Implementation Steps

### Step 1 — Find the Next Migration Version

```bash
ls src/main/resources/db/migration/
```

Use the next sequential version number (e.g., `V5__` if last is `V4__`).

### Step 2 — Create Migration File

```sql
-- Performance indexes for high-traffic query patterns

-- Orders filtered by branch (KDS view, branch-scoped order lists)
CREATE INDEX IF NOT EXISTS idx_food_order_branch_id
    ON food_order(branch_id);

-- Orders filtered by state (KDS columns: Created, Inpreparation, Done)
CREATE INDEX IF NOT EXISTS idx_food_order_state
    ON food_order(state);

-- Combined: branch + state filter (most common KDS query)
CREATE INDEX IF NOT EXISTS idx_food_order_branch_state
    ON food_order(branch_id, state);

-- Inventory lookups by branch + item (every order creation)
CREATE INDEX IF NOT EXISTS idx_inventory_branch_item
    ON inventory(branch_id, item_id);
```

### Step 3 — Verify Flyway Picks Up the Migration

```bash
mvn clean test
# Flyway will apply the new migration to the H2 test DB automatically
```

### Step 4 — Check Query Plans (Optional, on Dev DB)

```sql
EXPLAIN ANALYZE
SELECT * FROM food_order WHERE branch_id = 1 AND state = 'CREATED';
-- Should show "Index Scan" instead of "Seq Scan"
```

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] Migration file exists with `IF NOT EXISTS` guards
- [ ] `mvn clean test` applies the migration successfully (Flyway log shows migration applied)
- [ ] No test failures introduced
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
