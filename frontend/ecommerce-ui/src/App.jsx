import { useState, useEffect } from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import ProductDetails from "./pages/ProductDetails";
import Cart from "./pages/Cart";
import "./App.css";

export default function App() {
  const [products, setProducts] = useState([]);
  const [cartCount, setCartCount] = useState(0);

  useEffect(() => {
    // Initialize cart count from localStorage
    const cart = JSON.parse(localStorage.getItem("cart") || "[]");
    setCartCount(cart.reduce((sum, item) => sum + item.quantity, 0));
  }, []);

  return (
    <Router>
      <Navbar cartCount={cartCount} />
      <Routes>
        <Route
          path="/"
          element={
            <Home
              products={products}
              setProducts={setProducts}
              cartCount={cartCount}
              setCartCount={setCartCount}
            />
          }
        />
        <Route
          path="/product/:id"
          element={<ProductDetails setCartCount={setCartCount} />}
        />
        <Route
          path="/cart"
          element={<Cart setCartCount={setCartCount} />}
        />
      </Routes>
    </Router>
  );
}