# 💰 Full-Stack Expense Tracker

![Expense Tracker Demo](https://images.unsplash.com/photo-1554224155-6726b3ff858f?q=80&w=2000&auto=format&fit=crop)

A modern, responsive, full-stack Expense Tracker web application built with **Spring Boot** and **React**. 
Track your daily expenses, manage categories, set monthly budgets, and visualize your spending habits through a beautiful, dark-mode ready dashboard!

---

## ✨ Features

- **🔐 Secure Authentication:** JWT-based user login and registration.
- **📊 Interactive Dashboard:** Visualize spending trends with dynamic area charts (Recharts).
- **💸 Expense Management:** Add, edit, and delete transactions with ease.
- **🏷️ Custom Categories:** Create unique categories for your spending (e.g., Groceries, Rent, Travel).
- **🎯 Budget Tracking:** Set monthly limits per category and track your progress with visual progress bars.
- **🌍 Multi-Currency Support:** Automatically formats amounts based on your preferred currency (USD, INR, EUR, etc.).
- **🌓 Theme Switching:** Fully baked Light and Dark mode UI powered by Tailwind CSS.

---

## 🛠️ Tech Stack

### Frontend
- **React.js** (Vite)
- **Tailwind CSS v4** (Styling & Dark Mode)
- **Lucide React** (Icons)
- **Recharts** (Data Visualization)
- **Axios** (API Client with Interceptors)
- **React Router Dom** (Navigation)

### Backend
- **Java 21**
- **Spring Boot 3** (REST APIs)
- **Spring Security & JWT** (Authentication)
- **Spring Data JPA / Hibernate** (ORM)
- **PostgreSQL** (Database)

---

## 🚀 Getting Started

Follow these instructions to get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
- Node.js (v18+)
- Java JDK 21+
- PostgreSQL installed and running
- Maven (optional, wrapper is included)

### 1. Database Setup
1. Open PostgreSQL and create a database named `expense_db`:
   ```sql
   CREATE DATABASE expense_db;
   ```
2. If your PostgreSQL username/password is different from the defaults, update the `application.properties` file located at `expensetracker/src/main/resources/application.properties`.

### 2. Running the Backend
1. Open a terminal and navigate to the backend directory:
   ```bash
   cd expensetracker
   ```
2. Run the Spring Boot application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   *The backend will start on `http://localhost:8080` and Hibernate will automatically generate all necessary database tables.*

### 3. Running the Frontend
1. Open a **new** terminal and navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install the Node dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
   *The frontend will be accessible at `http://localhost:5173`.*

---

## 📖 Documentation

For developers looking to understand, modify, or extend the code, I have created two extremely detailed, beginner-friendly explanations of the architecture:

- 📘 [Backend Architecture & Guide](./expensetracker/BACKEND_EXPLANATION.md)
- 📙 [Frontend Architecture & Guide](./frontend/FRONTEND_EXPLANATION.md)

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! Feel free to check the issues page.

## 📝 License
This project is open-source and available under the [MIT License](LICENSE).
