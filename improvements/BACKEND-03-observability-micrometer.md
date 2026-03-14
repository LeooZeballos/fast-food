# BACKEND-03 — Enhanced Observability (Micrometer + Tracing)

**Priority:** MEDIUM
**Domain:** Backend / Observability
**Effort:** Large (3h)

---

## Summary

No custom metrics or distributed tracing exist. Critical service methods (`createOrder`, `decrementStock`, `startPreparation`) have no timing data. Without this, it is impossible to diagnose latency issues or correlate requests in production logs.

---

## Files to Modify

| File | Change |
|------|--------|
| `pom.xml` | Add `micrometer-tracing-bridge-brave` + `zipkin-reporter-brave` |
| `src/main/java/net/leozeballos/FastFood/foodorder/FoodOrderService.java` | Add `@Timed` or manual timer |
| `src/main/java/net/leozeballos/FastFood/inventory/InventoryService.java` | Add `@Timed` |
| `src/main/java/net/leozeballos/FastFood/product/ProductService.java` | Add `@Timed` |
| `src/main/resources/application.properties` | Enable Actuator metrics endpoint |

---

## Implementation Steps

### Step 1 — Add Dependencies

```xml
<!-- Micrometer tracing (already included via spring-boot-actuator) -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
<dependency>
    <groupId>io.zipkin.reporter2</groupId>
    <artifactId>zipkin-reporter-brave</artifactId>
</dependency>
```

### Step 2 — Enable Metrics Actuator Endpoint

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.metrics.enabled=true
management.endpoint.prometheus.enabled=true
```

**Ensure `/actuator/metrics` and `/actuator/prometheus` require authentication** by updating `SecurityConfig`:

```java
.requestMatchers("/actuator/health", "/actuator/info").permitAll()
.requestMatchers("/actuator/**").hasRole("ADMIN")  // all other actuator endpoints
```

### Step 3 — Add @Timed to Critical Methods

```java
// Enable @Timed AOP support in a config class:
@Bean
public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
}
```

Then annotate service methods:

```java
@Timed(value = "foodorder.create", description = "Time to create a food order")
@Transactional
public FoodOrder createOrder(CreateOrderDTO dto) { ... }

@Timed(value = "inventory.decrement", description = "Time to atomically decrement stock")
@Transactional
public void atomicDecrementOrThrow(...) { ... }
```

### Step 4 — Add Correlation ID Logging

In `application.properties`:
```properties
logging.pattern.console=%d{HH:mm:ss} [%X{traceId}/%X{spanId}] %-5level %logger{36} - %msg%n
```

With `micrometer-tracing-bridge-brave`, trace/span IDs are automatically injected into MDC.

### Step 5 — Configure Sampling

```properties
management.tracing.sampling.probability=1.0   # 100% in dev; set to 0.1 in prod
spring.zipkin.base-url=http://localhost:9411   # optional: Zipkin collector
```

---

## Test Command

```bash
mvn clean test
# Then verify metrics endpoint:
curl http://localhost:4080/actuator/metrics/foodorder.create
```

---

## Acceptance Criteria

- [ ] `/actuator/metrics` is accessible only to `ROLE_ADMIN`
- [ ] `/actuator/prometheus` exposes `foodorder_create_seconds_*` metrics
- [ ] Application logs include `traceId` and `spanId` fields
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
