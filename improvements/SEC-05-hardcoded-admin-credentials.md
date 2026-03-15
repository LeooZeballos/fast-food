# SEC-05 — Hardcoded Default Admin Credentials in DataInitializer

**Priority:** MEDIUM
**Domain:** Security
**Effort:** Small (45 min)

---

## Summary

`DataInitializer.java` seeds the admin user with a hardcoded bcrypt hash of `"admin"`. This is a publicly known hash. Anyone who can register or log in can try the default credentials against the app.

---

## Context

The hash in `DataInitializer.java` corresponds to `BCryptPasswordEncoder.encode("admin")`. Since bcrypt is deterministic-resistant but the password is trivially guessable, and the hash itself is in version control, the effective security is zero for the admin account on fresh deployments.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/java/net/leozeballos/FastFood/config/DataInitializer.java` | Read password from env var |
| `src/main/resources/application.properties` | Add `app.admin.initial-password` property placeholder |
| `.env.example` (create if missing) | Document required env vars |

---

## Implementation Steps

### Step 1 — Inject Password via @Value

```java
@Value("${app.admin.initial-password:#{null}}")
private String adminInitialPassword;
```

### Step 2 — Generate or Use the Password

```java
private String resolveAdminPassword() {
    if (adminInitialPassword != null && !adminInitialPassword.isBlank()) {
        log.info("Using configured ADMIN_INITIAL_PASSWORD from environment.");
        return passwordEncoder.encode(adminInitialPassword);
    }
    // Generate a random secure password on first run
    String generated = java.util.UUID.randomUUID().toString();
    log.warn("=======================================================");
    log.warn("  No ADMIN_INITIAL_PASSWORD set.");
    log.warn("  Generated admin password: {}", generated);
    log.warn("  Change this immediately after first login!");
    log.warn("=======================================================");
    return passwordEncoder.encode(generated);
}
```

Call this in `run()` instead of the hardcoded hash.

### Step 3 — Add Property Placeholder

In `application.properties`:
```properties
# Set via environment variable ADMIN_INITIAL_PASSWORD
app.admin.initial-password=${ADMIN_INITIAL_PASSWORD:}
```

### Step 4 — Document in .env.example

```bash
# Required: admin account initial password (only used on first DB seed)
ADMIN_INITIAL_PASSWORD=change-me-on-first-login
```

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] No hardcoded password hash exists in source code
- [ ] If `ADMIN_INITIAL_PASSWORD` env var is set, that password is used
- [ ] If env var is not set, a random password is generated and printed to logs at WARN level
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
