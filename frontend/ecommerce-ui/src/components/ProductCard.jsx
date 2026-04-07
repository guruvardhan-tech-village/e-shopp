import { useNavigate } from "react-router-dom";
import "./ProductCard.css";

function ProductCard({ product, onAddToCart }) {
  const navigate = useNavigate();

  const handleViewDetails = () => {
    navigate(`/product/${product.id}`);
  };

  return (
    <div className="product-card">
      <div className="product-image">
        <img
          src={product.image || "https://via.placeholder.com/200"}
          alt={product.name}
          onClick={handleViewDetails}
        />
      </div>
      <div className="product-info">
        <h3 className="product-name" onClick={handleViewDetails}>
          {product.name}
        </h3>
        <p className="product-description">{product.description?.substring(0, 60)}...</p>
        <div className="product-rating">
          <span className="stars">⭐ {product.rating?.toFixed(1) || "N/A"}</span>
          <span className="reviews">({product.reviews || 0} reviews)</span>
        </div>
        <div className="product-footer">
          <div className="price-section">
            <span className="price">₹{product.price?.toLocaleString()}</span>
            {product.originalPrice && (
              <span className="original-price">₹{product.originalPrice?.toLocaleString()}</span>
            )}
          </div>
          <button
            className="add-to-cart-btn"
            onClick={() => onAddToCart(product)}
          >
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
}

export default ProductCard;
