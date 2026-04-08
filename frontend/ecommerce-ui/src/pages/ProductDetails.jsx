import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import API from "../api/api";
import "./ProductDetails.css";

function ProductDetails({ setCartCount }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [activeTab, setActiveTab] = useState("description");

  useEffect(() => {
    fetchProductDetails();
  }, [id]);

  const fetchProductDetails = async () => {
    try {
      setLoading(true);
      const res = await API.get(`/${id}`);
      setProduct(res.data.data || res.data);
      setError(null);
    } catch (err) {
      console.error("Error fetching product:", err);
      setError("Failed to load product details");
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = () => {
    const cart = JSON.parse(localStorage.getItem("cart") || "[]");
    const existingItem = cart.find((item) => item.id === product.id);

    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      cart.push({ ...product, quantity });
    }

    localStorage.setItem("cart", JSON.stringify(cart));
    setCartCount(cart.reduce((sum, item) => sum + item.quantity, 0));
    alert(`${quantity} item(s) added to cart!`);
  };

  const handleBuyNow = () => {
    handleAddToCart();
    navigate("/cart");
  };

  if (loading) {
    return <div className="product-details-container"><div className="loading-message">Loading product details...</div></div>;
  }

  if (error || !product) {
    return (
      <div className="product-details-container">
        <div className="error-message">{error || "Product not found"}</div>
        <button onClick={() => navigate("/")} className="back-btn">
          ← Back to Products
        </button>
      </div>
    );
  }

  const productImage = product.imageUrl || product.image || product.image_url || "https://via.placeholder.com/400x400?text=No+Image";

  return (
    <div className="product-details-container">
      <button onClick={() => navigate("/")} className="back-btn">
        ← Back to Products
      </button>

      <div className="product-details">
        <div className="product-image-section">
          <img
            src={productImage}
            alt={product.name || "Product image"}
            className="product-image-large"
          />
          <div className="image-gallery">
            <img
              src={productImage}
              alt="Thumbnail 1"
            />
            <img
              src={productImage}
              alt="Thumbnail 2"
            />
          </div>
        </div>

        <div className="product-details-section">
          <h1 className="product-title">{product.name}</h1>

          <div className="rating-section">
            <span className="stars">⭐ {product.rating?.toFixed(1) || "N/A"}</span>
            <span className="reviews">{product.reviews || 0} customer reviews</span>
            <span className="answered">10 answered questions</span>
          </div>

          <div className="price-section-details">
            <span className="price-label">Price:</span>
            <span className="price-large">₹{product.price?.toLocaleString()}</span>
            {product.originalPrice && (
              <span className="original-price-large">
                ₹{product.originalPrice?.toLocaleString()}
              </span>
            )}
            <span className="discount">
              {product.discount && `${product.discount}% OFF`}
            </span>
          </div>

          <div className="product-highlights">
            <h3>Key Features:</h3>
            <ul>
              {product.features ? (
                product.features.split(",").map((feature, index) => (
                  <li key={index}>{feature.trim()}</li>
                ))
              ) : (
                <li>Premium quality product</li>
              )}
            </ul>
          </div>

          <div className="purchase-options">
            <div className="quantity-selector">
              <label>Quantity:</label>
              <div className="quantity-controls">
                <button
                  onClick={() => setQuantity(Math.max(1, quantity - 1))}
                  className="qty-btn"
                >
                  −
                </button>
                <input
                  type="number"
                  value={quantity}
                  onChange={(e) =>
                    setQuantity(Math.max(1, parseInt(e.target.value) || 1))
                  }
                  min="1"
                  className="qty-input"
                />
                <button
                  onClick={() => setQuantity(quantity + 1)}
                  className="qty-btn"
                >
                  +
                </button>
              </div>
            </div>

            <div className="action-buttons">
              <button onClick={handleAddToCart} className="add-to-cart-btn-large">
                Add to Cart
              </button>
              <button onClick={handleBuyNow} className="buy-now-btn">
                Buy Now
              </button>
            </div>
          </div>

          <div className="delivery-info">
            <div className="info-item">
              <span className="info-icon">🚚</span>
              <span>Free delivery on orders over ₹500</span>
            </div>
            <div className="info-item">
              <span className="info-icon">↩️</span>
              <span>7-day returns policy</span>
            </div>
            <div className="info-item">
              <span className="info-icon">✓</span>
              <span>Secure checkout with multiple payment options</span>
            </div>
          </div>
        </div>
      </div>

      <div className="product-tabs">
        <div className="tabs-header">
          <button
            className={`tab-btn ${activeTab === "description" ? "active" : ""}`}
            onClick={() => setActiveTab("description")}
          >
            Description
          </button>
          <button
            className={`tab-btn ${activeTab === "specifications" ? "active" : ""}`}
            onClick={() => setActiveTab("specifications")}
          >
            Specifications
          </button>
          <button
            className={`tab-btn ${activeTab === "reviews" ? "active" : ""}`}
            onClick={() => setActiveTab("reviews")}
          >
            Reviews
          </button>
        </div>

        <div className="tabs-content">
          {activeTab === "description" && (
            <div className="tab-pane">
              <h3>Product Description</h3>
              <p>{product.description || "No description available"}</p>
            </div>
          )}
          {activeTab === "specifications" && (
            <div className="tab-pane">
              <h3>Specifications</h3>
              <table className="specs-table">
                <tbody>
                  <tr>
                    <td>Brand</td>
                    <td>{product.brand || "N/A"}</td>
                  </tr>
                  <tr>
                    <td>Category</td>
                    <td>{product.category || "N/A"}</td>
                  </tr>
                  <tr>
                    <td>Stock</td>
                    <td>{product.stock || "In Stock"}</td>
                  </tr>
                  <tr>
                    <td>SKU</td>
                    <td>{product.sku || "N/A"}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          )}
          {activeTab === "reviews" && (
            <div className="tab-pane">
              <h3>Customer Reviews</h3>
              <div className="reviews-section">
                <p>Overall Rating: ⭐ {product.rating?.toFixed(1) || "N/A"} ({product.reviews || 0} reviews)</p>
                <p>Share your feedback about this product</p>
                <button className="write-review-btn">Write a Review</button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProductDetails;
