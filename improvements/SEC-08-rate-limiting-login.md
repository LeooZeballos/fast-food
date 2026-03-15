# SEC-08 — No Rate Limiting on /login (Brute Force)

**Priority:** HIGH
**Domain:** Security
**Effort:** Medium (2h)

---

## Summary

The Spring Security `/login` endpoint has no rate limiting. An attacker can make thousands of authentication attempts per second to brute-force credentials. Addresses OWASP A07.

---

## Files to Modify / Create

| File | Change |
|------|--------|
| `pom.xml` | Add `bucket4j-spring-boot-starter` dependency |
| `src/main/java/net/leozeballos/FastFood/config/RateLimitingFilter.java` | New filter |
| `src/main/java/net/leozeballos/FastFood/config/SecurityConfig.java` | Register filter |
| `src/main/resources/application.properties` | Add rate-limit config |

---

## Implementation Steps

### Step 1 — Add Bucket4j Dependency

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
```

### Step 2 — Create RateLimitingFilter

```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        if (!"/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }
        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.get(ip, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
                .build());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many login attempts. Try again later.");
        }
    }
}
```

### Step 3 — Register Filter Before UsernamePasswordAuthenticationFilter

In `SecurityConfig.securityFilterChain()`:

```java
.addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
```

### Step 4 — Tune Limits via Properties

```properties
app.security.login.max-attempts=10
app.security.login.window-minutes=1
```

Inject these with `@Value` into the filter for configurability.

---

## Test Command

```bash
mvn clean test
```

---

## Acceptance Criteria

- [ ] After 10 failed login attempts from the same IP within 1 minute, subsequent attempts return HTTP 429
- [ ] Successful logins are counted and consume from the bucket
- [ ] After the window expires, the counter resets
- [ ] Normal frontend login flow (1 attempt) is unaffected
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
