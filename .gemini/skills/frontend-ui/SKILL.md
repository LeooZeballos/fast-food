---
name: frontend-ui
description: Specializes in HTML/CSS and Thymeleaf templates. Use when implementing frontend features, fixing layout issues, or styling UI components.
---

# Frontend UI

## Overview

This skill enables Gemini CLI to implement the UI for the FastFood project. It focuses on writing clean, semantic HTML and modular CSS to build responsive and visually appealing web pages using Thymeleaf templates.

## Guidelines

### 1. HTML & Thymeleaf
- **Fragments**: Use `th:replace` and `th:fragment` for reusable components like navigation bars, footers, and sidebars.
- **Iteration**: Use `th:each` for listing products, orders, and branches.
- **Conditional Styling**: Use `th:classappend` for dynamic classes based on order status or other logic.

### 2. Styling (CSS)
- **Vanilla CSS**: Prefer Vanilla CSS for maximum flexibility and performance.
- **File Structure**: Main styles should be in `src/main/resources/static/styles/css/main.css`.
- **Responsive Design**: Use media queries to ensure the UI works on mobile and desktop.

### 3. Component Design
- **Buttons**: Consistently styled buttons (e.g., `.btn`, `.btn-primary`, `.btn-danger`).
- **Forms**: User-friendly form layouts with clear labels and error messaging.

## Workflow

1. **Skeleton**: Define the HTML structure in the Thymeleaf template.
2. **Data Binding**: Map model attributes using Thymeleaf variables.
3. **Styling**: Add CSS rules in `main.css` to bring the design to life.
4. **Validation**: Test the layout in a browser environment to ensure it behaves as expected.
