import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import ProductCard from "../components/ProductCard";
import API from "../api/api";
import "./Home.css";

function Home({ products, setProducts, cartCount, setCartCount }) {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [sortBy, setSortBy] = useState("relevance");
  const navigate = useNavigate();

  useEffect(() => {
    fetchAllProducts();
  }, []);

  const fetchAllProducts = async () => {
    try {
      setLoading(true);
      const res = await API.get("/");
      setProducts(res.data.data || res.data || []);
      setError(null);
    } catch (err) {
      console.error("Error fetching products:", err);
      setError("Failed to load products. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = (product) => {
    // Add product to cart
    const cart = JSON.parse(localStorage.getItem("cart") || "[]");
    const existingItem = cart.find((item) => item.id === product.id);

    if (existingItem) {
      existingItem.quantity += 1;
    } else {
      cart.push({ ...product, quantity: 1 });
    }

    localStorage.setItem("cart", JSON.stringify(cart));
    setCartCount(cart.reduce((sum, item) => sum + item.quantity, 0));
    alert("Product added to cart!");
  };

  const handleSort = (e) => {
    const selected = e.target.value;
    setSortBy(selected);

    let sorted = [...products];
    switch (selected) {
      case "price-low":
        sorted.sort((a, b) => a.price - b.price);
        break;
      case "price-high":
        sorted.sort((a, b) => b.price - a.price);
        break;
      case "rating":
        sorted.sort((a, b) => (b.rating || 0) - (a.rating || 0));
        break;
      case "newest":
        sorted.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        break;
      default:
        sorted = products;
    }
    setProducts(sorted);
  };

  if (loading) {
    return <div className="home-container"><div className="loading">Loading products...</div></div>;
  }

  return (
    <div className="home-container">
      <div className="home-header">
        <div className="breadcrumb">
          <span>Home</span>
          <span> / </span>
          <span>Products</span>
        </div>
        <div className="filter-section">
          <label htmlFor="sort">Sort by:</label>
          <select id="sort" value={sortBy} onChange={handleSort}>
            <option value="relevance">Relevance</option>
            <option value="price-low">Price: Low to High</option>
            <option value="price-high">Price: High to Low</option>
            <option value="rating">Highest Rated</option>
            <option value="newest">Newest</option>
          </select>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="products-grid">
        {products.length > 0 ? (
          products.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              onAddToCart={handleAddToCart}
            />
          ))
        ) : (
          <div className="no-products">No products found</div>
        )}
      </div>
    </div>
  );
}

export default Home;
