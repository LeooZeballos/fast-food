# SEC-10 — Flyway repair-on-migrate in Dev

**Priority:** LOW
**Domain:** Security / Reliability
**Effort:** Tiny (5 min)

---

## Summary

`spring.flyway.repair-on-migrate=true` in `application-dev.properties` silently auto-fixes migration checksum failures. This masks broken migrations that would fail in a production environment where repair is not enabled.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/resources/application-dev.properties` | Remove `spring.flyway.repair-on-migrate=true` |

---

## Implementation Steps

### Step 1 — Read application-dev.properties

Locate the `spring.flyway.repair-on-migrate` line.

### Step 2 — Remove the Property

Delete the line entirely. Flyway defaults to `false` for repair-on-migrate.

If you need to repair a corrupted history manually, run:
```bash
mvn flyway:repair
```

This makes the repair an explicit, deliberate action rather than a silent automatic one.

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] `spring.flyway.repair-on-migrate` is not set (defaults to `false`) in all profiles
- [ ] Flyway migration still runs successfully on fresh database
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
