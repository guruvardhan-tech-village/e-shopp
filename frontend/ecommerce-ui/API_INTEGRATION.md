# API Integration Guide

## Backend Integration with Spring Boot

This React application is designed to work with a Spring Boot backend. Below is the expected API structure.

## 📋 Expected API Endpoints

### Base URL
```
http://localhost:8080/products
```

### 1. Get All Products
**Endpoint:** `GET /products/` or `GET /products`

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "Product Name",
      "description": "Product description",
      "price": 999.99,
      "originalPrice": 1499.99,
      "image": "https://image-url.com/product.jpg",
      "rating": 4.5,
      "reviews": 234,
      "brand": "Brand Name",
      "category": "Category",
      "stock": "In Stock",
      "sku": "SKU123",
      "features": "Feature 1, Feature 2, Feature 3",
      "discount": 33,
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ]
}
```

### 2. Get Product Details by ID
**Endpoint:** `GET /products/{id}`

**Path Parameters:**
- `id` (number) - Product ID

**Response:**
```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "Product Name",
    "description": "Detailed product description",
    "price": 999.99,
    "originalPrice": 1499.99,
    "image": "https://image-url.com/product.jpg",
    "rating": 4.5,
    "reviews": 234,
    "brand": "Brand Name",
    "category": "Category",
    "stock": 50,
    "sku": "SKU123",
    "features": "Feature 1, Feature 2, Feature 3",
    "discount": 33,
    "createdAt": "2024-01-15T10:30:00Z"
  }
}
```

### 3. AI-Powered Search
**Endpoint:** `GET /products/ai-search?query={searchQuery}`

**Query Parameters:**
- `query` (string) - Search query

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "Matching Product",
      "price": 999.99,
      "rating": 4.5,
      // ... other product fields
    }
  ]
}
```

**Note:** This endpoint should be placed AFTER specific ID routes in your Spring Boot controller to avoid routing conflicts.

### 4. Search Suggestions
**Endpoint:** `GET /products/suggest?keyword={keyword}`

**Query Parameters:**
- `keyword` (string) - Search keyword

**Response:**
```json
{
  "status": "success",
  "data": [
    "laptop",
    "laptop bag",
    "laptop stand",
    "laptop charger"
  ]
}
```

## ⚙️ Configuration

### API Client Configuration
File: `src/api/api.js`

```javascript
import axios from "axios";

const API = axios.create({
  baseURL: "http://localhost:8080/products",
});

export default API;
```

### CORS Configuration (Spring Boot)
If you encounter CORS errors, configure CORS in your Spring Boot application:

```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/products/**")
                    .allowedOrigins("http://localhost:5173")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }
}
```

## 📱 Frontend Data Usage

### Home Page - Fetching All Products
```javascript
const res = await API.get("/");
// Expected: Array of products in res.data.data or res.data
```

### Product Details Page - Fetching Single Product
```javascript
const res = await API.get(`/${id}`);
// Expected: Product object in res.data.data or res.data
```

### Search - AI-Powered Search
```javascript
const res = await API.get(`/ai-search?query=${query}`);
// Expected: Array of matching products in res.data.data
```

### Autocomplete - Suggestions
```javascript
const res = await API.get(`/suggest?keyword=${keyword}`);
// Expected: Array of suggestion strings in res.data.data
```

## 🔄 Implementing Backend Cart (Optional)

For future cart API integration:

### Add to Cart
**Endpoint:** `POST /products/cart/add`

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

### Get Cart Items
**Endpoint:** `GET /products/cart`

**Response:**
```json
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "name": "Product Name",
      "price": 999.99,
      "quantity": 2
    }
  ]
}
```

### Remove from Cart
**Endpoint:** `DELETE /products/cart/{productId}`

## ✅ Testing the Integration

1. **Start Spring Boot Backend:**
   ```bash
   mvn spring-boot:run
   # Backend runs on http://localhost:8080
   ```

2. **Start Frontend:**
   ```bash
   npm run dev
   # Frontend runs on http://localhost:5173
   ```

3. **Test API Calls:**
   - Open browser DevTools Console
   - Check Network tab when loading products
   - Verify API responses match expected structure

## 🐛 Common Issues & Solutions

### Issue: "Cannot GET /products/"
**Solution:** Ensure your Spring Boot has the GET endpoint `/products/` or `/products` that returns all products.

### Issue: CORS Error
**Solution:** Implement CORS configuration in Spring Boot (see above).

### Issue: "Cannot read property 'data' of undefined"
**Solution:** Check API response structure. Update the code to match your actual response format:
```javascript
// If response is directly an array:
setProducts(res.data || []);

// If response has nested structure:
setProducts(res.data.data || res.data || []);
```

### Issue: Search results not showing
**Solution:** Verify debounce timing (300ms) and ensure `/suggest` endpoint is placed AFTER specific ID routes.

## 📊 Data Model Example

### Product Schema (Expected)
```typescript
interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  originalPrice?: number;
  image: string;
  rating?: number;
  reviews?: number;
  brand?: string;
  category?: string;
  stock?: string | number;
  sku?: string;
  features?: string;
  discount?: number;
  createdAt?: string;
}
```

## 🔒 Security Considerations

- Validate all user inputs on the backend
- Use HTTPS in production
- Implement user authentication for cart operations
- Add CSRF protection
- Sanitize product descriptions to prevent XSS

## 📚 Additional Resources

- Spring Boot CORS: https://spring.io/guides/gs/rest-service-cors/
- Axios Error Handling: https://axios-http.com/docs/handling_errors
- React Router API: https://reactrouter.com/docs

---

**Note:** Update this guide as your API structure evolves.
