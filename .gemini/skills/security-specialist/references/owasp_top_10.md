# OWASP Top 10 Reference for FastFood App

This guide maps the OWASP Top 10 vulnerabilities to the FastFood application's stack (Spring Boot + React).

## A01:2021-Broken Access Control
- **Context:** Ensure users cannot access other branches' data or admin panels without proper roles.
- **Check:** 
  - Review `BranchController` and `BranchRestController` for role-based access control (RBAC).
  - Verify that `OrderController` (if it exists) restricts order modifications to the relevant user/branch.

## A02:2021-Cryptographic Failures
- **Context:** Protect sensitive data (passwords, connection strings).
- **Check:**
  - Verify `jasypt-spring-boot-starter` is correctly encrypting properties in `application.properties`.
  - Ensure no secrets are logged or exposed in `Actuator` endpoints.

## A03:2021-Injection
- **Context:** SQL, NoSQL, and Template injection.
- **Check:**
  - FastFood uses Spring Data JPA, which mitigates SQL injection via parameterized queries.
  - Review manual JPQL/Native queries for string concatenation.
  - Check Thymeleaf templates for unescaped user input (using `[(${...})]` instead of `[[${...}]]`).

## A04:2021-Insecure Design
- **Context:** Architectural flaws.
- **Check:**
  - Verify that state transitions in the order state machine cannot be bypassed.

## A05:2021-Security Misconfiguration
- **Context:** Default configs, unnecessary features.
- **Check:**
  - Review `application.properties` for production-unsafe defaults.
  - Check `Actuator` configuration; ensure `/actuator/heapdump` or `/actuator/env` aren't public.

## A06:2021-Vulnerable and Outdated Components
- **Context:** Third-party libraries.
- **Check:**
  - Run `mvn dependency-check:check`.
  - Run `pnpm audit`.

## A07:2021-Identification and Authentication Failures
- **Context:** Session management, password policies.
- **Check:**
  - Review login logic and session timeout settings.

## A08:2021-Software and Data Integrity Failures
- **Context:** Untrusted deserialization, insecure CI/CD.
- **Check:**
  - Check usage of `ObjectInputStream` or libraries like FastJSON.

## A09:2021-Security Logging and Monitoring Failures
- **Context:** Lack of audit trails.
- **Check:**
  - Ensure critical actions (e.g., deleting a branch, creating a product) are logged.

## A10:2021-Server-Side Request Forgery (SSRF)
- **Context:** Fetching internal resources via user input.
- **Check:**
  - Review any functionality that takes a URL as input.
