# TEST-01 — No Tests for CustomUserDetailsService

**Priority:** HIGH
**Domain:** Tester
**Effort:** Small (1h)

---

## Summary

`CustomUserDetailsService` is the Spring Security integration point that loads users from the DB for every authentication. It has zero tests — a regression here breaks all logins silently.

---

## Files to Create

| File | Change |
|------|--------|
| `src/test/java/net/leozeballos/FastFood/auth/CustomUserDetailsServiceTest.java` | New unit test class |

---

## Implementation Steps

### Step 1 — Create Test Class

```java
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;
```

### Step 2 — Test: User Found

```java
@Test
void loadUserByUsername_existingUser_returnsUserDetails() {
    User user = new User();
    user.setUsername("admin");
    user.setPassword("hashed");
    user.setRoles(Set.of("ROLE_ADMIN"));

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));

    UserDetails details = service.loadUserByUsername("admin");

    assertEquals("admin", details.getUsername());
    assertTrue(details.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
}
```

### Step 3 — Test: User Not Found

```java
@Test
void loadUserByUsername_unknownUser_throwsUsernameNotFoundException() {
    when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class,
        () -> service.loadUserByUsername("unknown"));
}
```

### Step 4 — Test: Branch-Scoped User

```java
@Test
void loadUserByUsername_branchUser_hasBranchId() {
    User user = new User();
    user.setUsername("staff1");
    user.setPassword("hashed");
    user.setBranchId(42L);
    user.setRoles(Set.of("ROLE_USER"));

    when(userRepository.findByUsername("staff1")).thenReturn(Optional.of(user));

    CustomUserDetails details = (CustomUserDetails) service.loadUserByUsername("staff1");

    assertEquals(42L, details.getBranchId());
}
```

---

## Test Command

```bash
mvn clean test -Dtest=CustomUserDetailsServiceTest
```

---

## Acceptance Criteria

- [ ] Test covers: user found, user not found, branch ID propagation
- [ ] `UsernameNotFoundException` is thrown for unknown usernames
- [ ] `CustomUserDetails` correctly carries `branchId`
- [ ] `mvn clean test` passes with `Failures: 0, Errors: 0`
