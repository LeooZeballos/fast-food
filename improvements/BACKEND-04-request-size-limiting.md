# BACKEND-04 — No Request Size Limiting (DoS Risk)

**Priority:** MEDIUM
**Domain:** Backend / Security
**Effort:** Tiny (15 min)

---

## Summary

No `max-http-post-size` is configured. A malicious client can POST a multi-GB request body to any endpoint, causing OOM on the server or connection exhaustion.

---

## Files to Modify

| File | Change |
|------|--------|
| `src/main/resources/application.properties` | Add Tomcat request size limits |

---

## Implementation Steps

### Step 1 — Add Size Limits to application.properties

```properties
# Limit individual request body to 1 MB
server.tomcat.max-http-post-size=1048576
# Limit multipart file uploads (if used) to 512 KB
spring.servlet.multipart.max-file-size=512KB
spring.servlet.multipart.max-request-size=512KB
```

Adjust values based on expected payload sizes. Order creation payloads should be < 10 KB, so 1 MB is generous.

### Step 2 — Verify Error Handling

When the limit is exceeded, Spring Boot returns a 400 Bad Request. Ensure `GlobalExceptionHandler` catches `MaxUploadSizeExceededException` and returns an RFC 7807 response:

```java
@ExceptionHandler(MaxUploadSizeExceededException.class)
public ProblemDetail handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Payload Too Large");
    problem.setDetail("Request body exceeds the maximum allowed size.");
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

- [ ] Requests with body > 1 MB to any endpoint return 400 (or 413)
- [ ] Normal order creation (small payload) still works
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
