# FastFood App Improvements

This document tracks the planned and implemented improvements for the FastFood application.

## 🍔 Product Specialist (Business & Domain Logic)

- [x] **Order Creation (Point of Sale View)**: Dedicated interface for staff to select branches, browse items, and submit new orders.
- [x] **Pricing Transparency**: 
    - [x] Show item subtotal in the cart (Quantity x Price).
    - [x] Show original menu subtotal vs discounted total (via Unit/Subtotal breakdown).
    - [x] Centralized price formatting to ensure consistent `es-ES` locale usage.
- [ ] **Inventory/Availability Awareness**: Validate item availability at the selected branch during order placement.
- [ ] **Kitchen Display System (KDS) Metrics**: Track and display time in preparation for each order.

## 🎨 Designer (UI/UX & Aesthetics)

- [x] **FastFood Color Palette**: High-impact brand colors (warm reds, yellows) and redesigned Navbar.
- [ ] **Kanban Board for Orders**: Visual workflow for kitchen staff using columns for different order states.
- [ ] **Enhanced Interactive Feedback**:
    - [ ] **Loading Skeletons**: Replace "Loading..." text with animated grey blocks.
    - [ ] **Micro-animations**: Pulse effects for active states and success animations.
- [ ] **Visual Hierarchy on Cards**: Switch Product and Menu lists from tables to visually appealing grid cards.
