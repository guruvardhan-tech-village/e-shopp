# Amazon E-Commerce UI - React Frontend

A modern, fully-featured e-commerce user interface built with React, Vite, and React Router, designed to integrate seamlessly with a Spring Boot backend.

## 📦 Features

### ✅ Implemented Features

1. **Navigation Bar**
   - Amazon-styled branding
   - Real-time search with autocomplete suggestions
   - Shopping cart badge with item count
   - Quick navigation to cart and home

2. **Home Page**
   - Product grid display with responsive layout
   - Sorting options (Relevance, Price Low-High, Price High-Low, Highest Rated, Newest)
   - Product filtering and search integration
   - Loading and error states
   - Add to cart functionality

3. **Product Card Component**
   - Product image with hover effects
   - Product name, description, and ratings
   - Price display with original price (if applicable)
   - Quick "Add to Cart" button
   - Click to view details

4. **Product Details Page**
   - Large product image with gallery
   - Detailed product information
   - Rating and reviews count
   - Key features and specifications
   - Quantity selector
   - "Add to Cart" and "Buy Now" buttons
   - Delivery information
   - Tabbed interface (Description, Specifications, Reviews)

5. **Shopping Cart**
   - View all cart items
   - Quantity adjustment controls
   - Remove items from cart
   - Order summary with pricing breakdown
   - Discount calculation
   - Tax estimation
   - Checkout button
   - Promotional offers display

6. **Local Storage Integration**
   - Cart persistence across sessions
   - Automatic cart count updates

## 🏗️ Project Structure

```
src/
├── components/
│   ├── Navbar.jsx           # Navigation bar with search
│   ├── Navbar.css           # Navbar styling
│   ├── ProductCard.jsx      # Product card component
│   └── ProductCard.css      # Product card styling
├── pages/
│   ├── Home.jsx             # Home/product listing page
│   ├── Home.css             # Home page styling
│   ├── ProductDetails.jsx   # Product detail page
│   ├── ProductDetails.css   # Product details styling
│   ├── Cart.jsx             # Shopping cart page
│   └── Cart.css             # Cart styling
├── api/
│   └── api.js               # API client configuration
├── App.jsx                  # Main app with routing
├── App.css                  # App-level styling
├── main.jsx                 # React entry point
├── index.css                # Global styles
```

## 🚀 Getting Started

### Prerequisites
- Node.js 16+ and npm
- Spring Boot backend running on `http://localhost:8080`

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

3. Open `http://localhost:5173` in your browser

### Build for Production

```bash
npm run build
```

### Linting

```bash
npm run lint
```

## 🔌 API Integration

The application expects a Spring Boot backend with the following endpoints:

### Products API
- `GET /products/` - Get all products
- `GET /products/{id}` - Get product details
- `GET /products/ai-search?query={query}` - AI-powered product search
- `GET /products/suggest?keyword={keyword}` - Search suggestions

### Cart API (future implementation)
- `GET /products/cart` - Get cart items
- `POST /products/add-to-cart` - Add product to cart

**Base URL:** `http://localhost:8080/products`

## 🎨 Styling & Design

- **Color Scheme:** Amazon-inspired (Navy, Orange, Red accents)
- **Responsive:** Mobile-first responsive design (XS to XXL screens)
- **CSS Grid & Flexbox:** Modern layout techniques
- **Hover Effects:** Interactive UI elements
- **Accessibility:** Semantic HTML and keyboard navigation

## 📱 Responsive Breakpoints

- **Desktop:** 1200px+
- **Tablet:** 768px - 1199px
- **Mobile:** 480px - 767px
- **Small Mobile:** < 480px

## 💾 Local Storage

The application uses browser localStorage to persist:
- **cart** - Array of cart items with quantities

Example structure:
```json
[
  {
    "id": 1,
    "name": "Product Name",
    "price": 999,
    "image": "url",
    "quantity": 2
  }
]
```

## 🔄 State Management

Using React hooks (useState, useEffect) for:
- Product data
- Cart count
- Loading states
- User interactions

## 🚦 Component Communication

- **App Component:** Root component managing products and cart state
- **Navbar:** Receives cartCount and provides navigation
- **Home:** Receives products, setProducts, cartCount, setCartCount
- **ProductDetails:** Receives setCartCount for updates
- **Cart:** Receives setCartCount for updates

## 🎯 Key Features to Complete

Future enhancements:
- [ ] User authentication & accounts
- [ ] Wishlist functionality
- [ ] Order history
- [ ] Payment gateway integration
- [ ] Product reviews & ratings submission
- [ ] Advanced filtering (category, brand, price range)
- [ ] Compare products
- [ ] Real-time inventory updates

## 🛠️ Technologies Used

- **React 19.2.4** - UI library
- **Vite 8.0.4** - Build tool & dev server
- **React Router DOM** - Client-side routing
- **Axios 1.14.0** - HTTP client
- **CSS3** - Styling with modern features

## 📝 Notes

- The app uses local storage for cart management. For production, implement a backend cart system.
- Search and suggestions use debouncing (300ms) to optimize API calls.
- Product images default to placeholder if not provided.
- Pricing includes support for discounts and original prices.

## 🎓 Learning Resources

- [React Docs](https://react.dev)
- [React Router Docs](https://reactrouter.com)
- [Vite Docs](https://vitejs.dev)
- [Amazon UI Design Patterns](https://www.amazon.com)

## 📄 License

This project is part of an educational e-commerce initiative.

---

**Server Running:** http://localhost:5173/
**Backend Expected:** http://localhost:8080/products
