# FastFood

[![Open in Visual Studio Code](https://img.shields.io/badge/Open%20in-Visual%20Studio%20Code-blue?logo=visual-studio-code)](https://open.vscode.dev/LeooZeballos/fast-food) [![GitHub issues](https://img.shields.io/github/issues/LeooZeballos/fast-food-spring)](https://github.com/LeooZeballos/fast-food-spring/issues) [![GitHub forks](https://img.shields.io/github/forks/LeooZeballos/fast-food-spring)](https://github.com/LeooZeballos/fast-food-spring/network) [![GitHub stars](https://img.shields.io/github/stars/LeooZeballos/fast-food-spring)](https://github.com/LeooZeballos/fast-food-spring/stargazers) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Table of contents
* [General info](#General-info)
* [How to Install and Run the Project](#How-to-Install-and-Run-the-Project)
* [Technologies](#Technologies)
  * [Backend](#Backend)
  * [Frontend](#Frontend)
* [Features](#Features)
* [License](#License)

## General info
FastFood is a fast food application designed for efficient order management and effective administration of products, menus, and branches. Its development served as an opportunity for me to gain proficiency in utilizing Spring Boot, Spring JPA, and  Thymeleaf frameworks.

To ensure a robust and scalable backend, I employed PostgreSQL as the chosen database management system, and Maven as the dependency manager for simplified project management. My primary objective in developing this application was to establish a codebase characterized by its simplicity, cleanliness, and adherence to sound architectural principles.

Throughout the codebase, I have diligently added comprehensive comments to enhance code comprehension and maintainability. I have strived to make the code as readable as possible, enabling easy collaboration and future development. In terms of testing, I employed JUnit and Mockito frameworks, conducting unit tests to validate the functionality of services, and integration tests to ensure smooth operation of controllers.

For the frontend aspect, Bootstrap was utilized, complemented by CSS for styling, resulting in a visually appealing and responsive user interface. By employing responsive design principles, I aimed to deliver an application that seamlessly adapts to different devices and screen sizes.

## How to Install and Run the Project

### Requirements

* Java 8 or higher
* Apache Maven
* PostgreSQL with a database named `fastfood`

### Steps
1. Clone the repository
```bash
git clone https://github.com/LeooZeballos/fast-food.git
```
2. Open the project in your terminal and run the following command
```bash
mvn spring-boot:run
```
3. Open your browser and go to http://localhost:8080/

### Docker

Alternatively, you can run the project using Docker. To do so, follow these steps:

1. Clone the repository
```bash
git clone https://github.com/LeooZeballos/fast-food.git
```
2. Build the image for the API.
```bash
docker build -t fastfood-api .
```
2. Open the project in your terminal and run the following command
```bash
docker-compose up -d
```
3. Open your browser and go to http://localhost:8080/

## Technologies

### Backend

[![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com)
[![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)](https://maven.apache.org)
[![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io)
[![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)](https://hibernate.org)
[![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org)

### Frontend

[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)](https://www.thymeleaf.org)
[![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/HTML5)
[![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white)](https://developer.mozilla.org/en-US/docs/Web/CSS)
[![Bootstrap](https://img.shields.io/badge/bootstrap-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white)](https://getbootstrap.com)

## Features

### Manage orders
![Manage orders](./images/Manage%20orders.png)

#### Take orders
![Take orders](./images/Take%20orders.png)

#### Update orders (only for orders that have not been prepared)
![Update orders](./images/Update%20orders.png)

#### Start preparing orders
![Start preparing orders](./images/Start%20preparing%20orders.png)

#### Finish preparing orders or cancel orders
![Finish preparing orders](./images/Finish%20preparing%20orders.png)

#### Confirm payment or reject orders
![Confirm payment](./images/Confirm%20payment.png)

### Manage products
![Manage products](./images/Manage%20products.png)
![Create new product](./images/Create%20new%20product.png)
![Edit product](./images/Edit%20product.png)

### Manage menus
![Manage menus](./images/Manage%20menus.png)
![Create new menu](./images/Create%20new%20menu.png)
![Edit menu](./images/Edit%20menu.png)

### Manage branches
![Manage branches](./images/Manage%20branches.png)
![Create new branch](./images/Create%20new%20branch.png)
![Edit branch](./images/Edit%20branch.png)

## License

MIT License

Copyright (c) 2023 Leonel Zeballos

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

