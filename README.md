# FastFood 🍔

[![Open in Visual Studio Code](https://img.shields.io/badge/Open%20in-Visual%20Studio%20Code-blue?logo=visual-studio-code)](https://open.vscode.dev/LeooZeballos/fast-food) [![GitHub issues](https://img.shields.io/github/issues/LeooZeballos/fast-food)](https://github.com/LeooZeballos/fast-food/issues) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

**FastFood** is a modern, full-stack fast food management application designed for efficiency and scalability. It features a decoupled architecture with a robust Spring Boot backend and a high-performance React frontend.

## 🌟 Key Features

*   **📦 Decoupled Architecture**: Independent Spring Boot 3.4.4 backend and Vite + React 19 frontend.
*   **🌎 Bilingual Support**: Fully localized in **English** and **Spanish**.
*   **📊 Order Management**: Real-time order lifecycle tracking (Take, Prepare, Complete, Cancel).
*   **🏢 Multi-Branch Support**: Manage multiple restaurant locations.
*   **🍔 Menu & Inventory**: Flexible menu creation and inventory tracking.
*   **🎨 Modern UI**: Built with Tailwind CSS, Shadcn UI, and Lucide icons for a premium feel.
*   **⚡ High Performance**: Optimized with Spring Virtual Threads and Caffeine caching.

---

## 🛠️ Technology Stack

### Backend
*   **Java 17+** (with Virtual Threads support)
*   **Spring Boot 3.4.4**
*   **Spring Data JPA** & **Hibernate**
*   **PostgreSQL** (with Flyway migrations)
*   **Spring Actuator** (for monitoring)
*   **JUnit 5** & **Mockito** (Testing)

### Frontend
*   **React 19**
*   **TypeScript**
*   **Vite 6**
*   **Tailwind CSS** & **Shadcn UI**
*   **TanStack Query** (Data fetching)
*   **i18next** (Localization)
*   **Vitest** & **Playwright** (Testing)

---

## 🚀 Getting Started

### Prerequisites
*   **Java 17** or higher
*   **Node.js 18+** & **pnpm** (or npm/yarn)
*   **Docker** & **Docker Compose**
*   **PostgreSQL** (if running locally without Docker)

### Quick Start with Scripts
The project includes automated scripts to manage services in the background:

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/LeooZeballos/fast-food.git
    cd fast-food
    ```

2.  **Start all services** (Database, Backend, and Frontend):
    ```bash
    ./scripts/start-all.sh
    ```

3.  **Access the application**:
    *   **Frontend**: [http://localhost:4000](http://localhost:4000)
    *   **Backend API**: [http://localhost:4080](http://localhost:4080)
    *   **API Docs**: [http://localhost:4080/swagger-ui.html](http://localhost:4080/swagger-ui.html)

4.  **Stop all services**:
    ```bash
    ./scripts/stop-all.sh
    ```

### Manual Setup

#### Backend
```bash
mvn spring-boot:run
```
*Configurable via `src/main/resources/application.properties`.*

#### Frontend
```bash
cd frontend
pnpm install
pnpm dev
```

---

## 🤖 AI Agent Ready
This project is optimized for AI agents (like Gemini CLI or Claude). 
Check [**AGENTS.md**](./AGENTS.md) for mandatory workflows, service management rules, and debugging capabilities.

---

## 📸 Screenshots

### Manage Orders
![Manage orders](./images/Manage%20orders.png)

### Take Orders
![Take orders](./images/Take%20orders.png)

### Manage Products
![Manage products](./images/Manage%20products.png)

---

## 📜 License

MIT License - Copyright (c) 2023-2026 Leonel Zeballos

