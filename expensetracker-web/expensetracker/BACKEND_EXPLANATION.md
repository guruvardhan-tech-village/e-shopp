# Backend Architecture & Code Explanation

Welcome to the Spring Boot backend of the Expense Tracker! This document is written for beginners to understand exactly how the code works and how to modify it.

## 📁 How the Folders are Organized

Spring Boot applications follow a very specific "Layered Architecture". Think of it like a restaurant:
1. **Controller** (The Waiter): Takes requests from the internet (frontend) and returns responses.
2. **Service** (The Chef): Contains the "Business Logic". It processes the data, calculates things, and checks rules.
3. **Repository** (The Pantry Manager): Talks directly to the PostgreSQL database to save or find data.
4. **Model/Entity** (The Ingredients): The exact blueprint of how a table in the database looks.
5. **DTO (Data Transfer Object)** (The Menu): We don't send raw database Models to the internet. We use DTOs to control exactly what data goes in and out.

---

## 🏗️ 1. The Models (`src/main/java/.../model`)

Models represent your Database Tables. Each file here creates a table in PostgreSQL.

### `User.java`
- Represents a person using the app.
- Contains `id`, `email`, `password` (hashed for security), and `displayName`.
- **How to modify:** If you want users to have a "Phone Number", you would add `private String phoneNumber;` here.

### `Category.java`
- Represents an expense category (e.g., Groceries, Rent).
- It has a `ManyToOne` relationship with `User`. This means "Many categories belong to One user".

### `Expense.java`
- Represents a single transaction.
- Contains `amount`, `description`, `expenseDate`, and `paymentMethod`.
- Links to both a `User` (who bought it) and a `Category` (what kind of expense it is).

### `Budget.java`
- Represents a monthly spending limit for a specific category.
- Contains `limitAmount`, `month`, and `year`.

---

## 🗄️ 2. The Repositories (`src/main/java/.../repository`)

Repositories are interfaces that extend `JpaRepository`. 
- **What they do:** They magically generate SQL queries for you. You don't need to write `SELECT * FROM users`. You just call `userRepository.findAll()`.
- **Custom Queries:** If you look in `ExpenseRepository.java`, you'll see a method like `findByUserIdOrderByExpenseDateDesc`. Spring Boot literally reads the name of this method and automatically writes the SQL query to find expenses by user ID and sort them by date!

---

## 🍳 3. The Services (`src/main/java/.../service`)

This is where the actual "thinking" happens. 
- Example in `ExpenseService.java`: When you try to delete an expense, the service first checks: "Does this expense actually belong to the user who is logged in?" If not, it throws an error.
- **How to modify:** If you want to add a rule like "No expense can be over $10,000", you would add an `if (expense.getAmount() > 10000)` check inside the `createExpense` method in `ExpenseService.java`.

---

## 📡 4. The Controllers (`src/main/java/.../controller`)

Controllers define the URLs (Endpoints) that the React frontend calls.
- `AuthController.java`: Handles `/api/v1/auth/login` and `/register`.
- `ExpenseController.java`: Handles `/api/v1/expenses`. It uses annotations like `@GetMapping` (to read data) and `@PostMapping` (to create data).
- **Authentication:** Notice the `@AuthenticationPrincipal` tag in the methods? That magically grabs the currently logged-in user from the JWT token so you know exactly who is making the request!

---

## 🔒 5. Security (`src/main/java/.../security`)

Security in this app is handled via **JWT (JSON Web Tokens)**.
1. When a user logs in, `JwtTokenProvider.java` generates a long, encrypted string (the token).
2. The frontend saves this token and sends it with every future request.
3. `JwtAuthenticationFilter.java` intercepts every incoming request, checks if the token is valid, and if it is, allows the request to reach the Controller.

---

## 🛠️ How to Add a Completely New Feature

Let's say you want to add an **Income** tracking feature (Salary, Freelance, etc.). Here is the exact order you should create the files:

1. **Create the Model:** Create `Income.java` in the `model` folder. Define `amount`, `source`, `date`, and link it to `User`.
2. **Create the Repository:** Create `IncomeRepository.java` (Interface extending `JpaRepository`).
3. **Create the DTOs:** In `dto/request`, create `IncomeRequest.java`. In `dto/response`, create `IncomeDto.java`.
4. **Create the Service:** Create `IncomeService.java`. Write methods for `createIncome()`, `getIncomesForUser()`, etc.
5. **Create the Controller:** Create `IncomeController.java`. Add `@RestController` and map it to `/api/v1/incomes`. Call your Service methods here!
6. **Restart your server:** Spring Boot will automatically create the `incomes` table in PostgreSQL!
