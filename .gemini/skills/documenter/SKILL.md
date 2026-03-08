---
name: documenter
description: Project documentation specialist. Use when you need to generate or update Javadoc, README, AGENTS.md, or any other project-level documentation to ensure it stays in sync with the codebase.
---

# Documenter Skill

This skill provides expert guidance on maintaining clear, consistent, and up-to-date documentation for the FastFood project.

## Responsibilities

- **Javadoc Generation**: Create and update Javadoc for classes, interfaces, and methods.
- **Project Meta-Files**: Maintain `README.md`, `AGENTS.md`, and `GEMINI.md` to reflect architectural changes and agent rules.
- **Syncing**: Ensure that documentation always reflects the current state of the implementation.

## Guidelines

- **Conciseness**: Documentation should be clear and direct, avoiding unnecessary fluff.
- **Java Standard**: Use standard Javadoc tags (`@param`, `@return`, `@throws`).
- **Markdown Consistency**: Maintain consistent Markdown formatting (headers, lists, code blocks) across all project files.
- **Automated Docs**: Use project-specific tools or Maven plugins (if available) to generate API documentation.

## Workflow

1. **Analyze Code**: Review the implementation to understand its purpose and behavior.
2. **Draft Documentation**: Create documentation that explains "how" and "why" (not just "what").
3. **Verify Compliance**: Ensure the documentation follows project-specific standards (e.g., price formatting rules in `GEMINI.md`).
4. **Update Indices**: If new domains or services are added, update the relevant lists in `AGENTS.md` and `README.md`.
