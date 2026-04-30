# AI E-Commerce Platform

A full-stack Amazon-inspired e-commerce application with AI-powered product search, JWT authentication, and order management.

---

## Tech Stack

**Backend**
- Java 21 + Spring Boot 4.0.5
- Spring Security + JWT (JJWT 0.11.5)
- Spring Data JPA + Hibernate
- MySQL
- Maven

**Frontend**
- React 19 + Vite 8
- React Router DOM 7
- Axios, Tailwind CSS, Framer Motion

---

## Features

- User signup/login with JWT-based authentication
- Product browsing with filtering, sorting, and pagination
- AI-powered natural language search (e.g. "phones under 20000", "cheap laptops")
- Search suggestions and category recommendations
- Shopping cart with localStorage persistence
- Order placement and order history (authenticated)

---

## Project Structure

```
├── Backend/ecommerce/          # Spring Boot API
│   └── src/main/java/com/ai_ecommerce/ecommerce/
│       ├── controller/         # AuthController, ProductController, OrderController
│       ├── service/            # Business logic + AISearchService + JwtUtil
│       ├── model/              # User, Product, Orders, OrderItem
│       ├── repository/         # JPA repositories
│       ├── dto/                # Request DTOs
│       ├── config/             # SecurityConfig, JwtFilter
│       └── response/           # ApiResponse wrapper
│
└── Frontend/ecommerce-ui/      # React SPA
    └── src/
        ├── components/         # Navbar, ProductCard, SearchBar, etc.
        ├── pages/              # Home, ProductDetails, Cart, Checkout, Login, Signup, Orders
        ├── api/                # Axios client
        ├── context/            # State management
        ├── hooks/              # Custom hooks
        └── routes/             # App routing
```

---

## Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- MySQL running locally

### 1. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE ai_ecommerce;
```

### 2. Backend

Create a `.env` file in `Backend/ecommerce/`:

```env
DB_URL=jdbc:mysql://localhost:3306/ai_ecommerce
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
OPENAI_API_KEY=your_openai_api_key
```

Then run:

```bash
cd Backend/ecommerce
mvn clean install
mvn spring-boot:run
```

API runs at `http://localhost:8080`

### 3. Frontend

```bash
cd Frontend/ecommerce-ui
npm install
npm run dev
```

UI runs at `http://localhost:5173`

---

## API Endpoints

### Auth — `/api/auth`
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/signup` | Register a new user |
| POST | `/login` | Login and receive JWT token |

### Products — `/api/products`
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all products |
| GET | `/{id}` | Get product by ID |
| POST | `/` | Add a product |
| POST | `/bulk` | Bulk add products |
| PUT | `/{id}` | Update a product |
| DELETE | `/{id}` | Delete a product |
| GET | `/search?keyword=` | Keyword search |
| GET | `/ai-search?query=` | AI-powered natural language search |
| GET | `/suggest?keyword=` | Search suggestions |
| GET | `/recommend?category=` | Category recommendations |
| GET | `/company?companyName=` | Filter by company |
| GET | `/price-range?min=&max=` | Filter by price range |
| GET | `/page?page=&size=` | Paginated products |
| GET | `/page-sort?page=&size=&sortBy=` | Paginated + sorted |

### Orders — `/api/orders` *(requires JWT)*
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/` | Place an order |
| GET | `/` | Get current user's orders |

---

## Environment Variables

| Variable | Description |
|----------|-------------|
| `DB_URL` | MySQL JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `OPENAI_API_KEY` | OpenAI API key (for AI search) |

---

## Database Schema

| Model | Key Fields |
|-------|-----------|
| User | id, name, email, password, createdAt |
| Product | id, name, description, price, category, companyName, imageUrl, rating, stock |
| Orders | id, userId, totalAmount, status, createdAt |
| OrderItem | id, orderId, productId, quantity, price |
