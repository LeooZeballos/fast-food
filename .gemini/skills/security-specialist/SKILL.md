---
name: security-specialist
description: Security auditing, vulnerability assessment, and secret detection for Spring Boot (Java) and React (TypeScript) projects. Use when the user requests a security audit, a vulnerability check, or a review for common flaws like the OWASP Top 10.
---

# Security Specialist Skill

This skill provides expert guidance for auditing and securing the FastFood application (Spring Boot + React).

## Core Responsibilities

1. **Dependency Auditing**: Identifying vulnerable third-party libraries.
2. **Static Code Analysis**: Reviewing code for common security pitfalls (Injection, Broken Access Control, etc.).
3. **Secret Detection**: Ensuring no credentials, keys, or sensitive data are exposed in source code or configuration.
4. **Compliance & Standards**: Evaluating the application against the [OWASP Top 10](references/owasp_top_10.md).

## Recommended Workflows

### 1. Perform a Full Security Audit
When asked to "audit the app" or "perform a security review":
1. **Analyze Dependencies**: Run `pnpm audit` (frontend) and `mvn dependency-check:check` (backend).
2. **Check for Hardcoded Secrets**: Search for "password", "secret", "key", "apiKey" in `src/` and `resources/`.
3. **Review Endpoints**: Manually audit `@RestController` methods for missing role checks or validation.
4. **Consult Checklist**: Follow the [Security Checklist](references/security_checklist.md).
5. **Report Findings**: Summarize vulnerabilities by severity and propose remediations.

### 2. Dependency Vulnerability Check
When asked to check for vulnerabilities in libraries:
1. **Frontend**: Go to `frontend/` and execute `pnpm audit`.
2. **Backend**: Use `mvn dependency-check:check`. Note: If the plugin is missing, propose adding it to `pom.xml`.

### 3. Review for OWASP Top 10
When evaluating a specific feature (e.g., a new API):
1. Use the [OWASP Top 10 Mapping](references/owasp_top_10.md) to identify relevant threats.
2. Specifically look for **A03: Injection** and **A01: Broken Access Control**.

## Common Tools & Commands

- **Backend (Maven)**:
  - `mvn dependency-check:check` (Requires `org.owasp:dependency-check-maven`)
  - `mvn checkstyle:check`
- **Frontend (npm/pnpm)**:
  - `pnpm audit`
  - `npm audit fix`
  - `eslint . --ext .ts,.tsx`
- **Generic Search**:
  - `grep -rE "password|secret|key|apiKey|token" .` (Exclude `node_modules` and `target`)

## Handling Findings

Always prioritize high/critical vulnerabilities. For each finding, provide:
- **Location**: File and line number.
- **Vulnerability**: Brief description (e.g., "SQL Injection").
- **Impact**: What an attacker could achieve.
- **Remediation**: Specific code changes or configuration updates.
