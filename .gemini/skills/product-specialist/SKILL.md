---
name: product-specialist
description: FastFood domain expert and product manager. Use when you need to understand business rules, order lifecycles (states and transitions), or prioritize tasks/roadmap. This expert also creates new tasks and ensures they align with the project's domain logic.
---

# Product Specialist Skill

This skill provides expert domain knowledge for the FastFood application, ensuring all features and changes align with business requirements and logic.

## Domain Model

- **Order State Machine**:
  - `CREATED` -> `INPREPARATION` (`STARTPREPARATION`)
  - `INPREPARATION` -> `DONE` (`FINISHPREPARATION`)
  - `DONE` -> `PAID` (`CONFIRMPAYMENT`)
  - Transitions to terminal states: `CANCELLED` (from `CREATED`/`INPREPARATION`), `REJECTED` (from `DONE`), or `PAID` (from `DONE`).
- **Pricing & Menus**:
  - `Menu` prices are calculated as `sum(product.price) * (1 - discount)`.
  - Discounts are stored as `BigDecimal` (e.g., `0.1` for 10%).
  - **CRITICAL**: Price formatting must use `es-ES` locale (e.g., `10,50 €`).
- **Structure**:
  - Branches have addresses.
  - Menus are composed of products.
  - Orders are placed at branches and contain items (products or menus).

## Task Creation & Prioritization

When assisting with task management:
1. **Stability First**: Prioritize fixing broken tests or regressions (Mandatory as per `AGENTS.md`).
2. **Business Flow**: Prioritize completing the end-to-end order lifecycle (Creation -> Preparation -> Payment).
3. **Data Integrity**: Ensure relationships (Menu-Product, Order-Branch) are properly managed and validated.
4. **User Experience**: Prioritize features that improve the ordering process or branch management visibility.

## Workflow

1. **Review Requirements**: Match the request against the existing domain model.
2. **Identify Impact**: Determine how a change affects the state machine or pricing logic.
3. **Propose Tasks**: Break down complex requests into actionable sub-tasks.
4. **Validate Domain Logic**: Ensure new code respects constraints (e.g., valid state transitions).
