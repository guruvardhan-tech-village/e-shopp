import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Cart.css";

function Cart({ setCartCount }) {
  const [cartItems, setCartItems] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    loadCartItems();
  }, []);

  const loadCartItems = () => {
    const cart = JSON.parse(localStorage.getItem("cart") || "[]");
    setCartItems(cart);
  };

  const updateQuantity = (productId, newQuantity) => {
    if (newQuantity <= 0) {
      removeItem(productId);
      return;
    }

    const updatedCart = cartItems.map((item) =>
      item.id === productId ? { ...item, quantity: newQuantity } : item
    );
    setCartItems(updatedCart);
    localStorage.setItem("cart", JSON.stringify(updatedCart));
    setCartCount(updatedCart.reduce((sum, item) => sum + item.quantity, 0));
  };

  const removeItem = (productId) => {
    const updatedCart = cartItems.filter((item) => item.id !== productId);
    setCartItems(updatedCart);
    localStorage.setItem("cart", JSON.stringify(updatedCart));
    setCartCount(updatedCart.reduce((sum, item) => sum + item.quantity, 0));
  };

  const getTotalPrice = () => {
    return cartItems.reduce((total, item) => total + item.price * item.quantity, 0);
  };

  const getTotalItems = () => {
    return cartItems.reduce((total, item) => total + item.quantity, 0);
  };

  const getTotalDiscount = () => {
    return cartItems.reduce((total, item) => {
      const discount = item.originalPrice
        ? (item.originalPrice - item.price) * item.quantity
        : 0;
      return total + discount;
    }, 0);
  };

  const handleCheckout = () => {
    if (cartItems.length === 0) {
      alert("Your cart is empty");
      return;
    }
    alert("Proceeding to checkout...\nThis would redirect to payment gateway");
    // Here you would redirect to payment gateway
  };

  if (cartItems.length === 0) {
    return (
      <div className="cart-container">
        <div className="empty-cart">
          <div className="empty-icon">🛒</div>
          <h2>Your cart is empty</h2>
          <p>Add items to your cart to get started</p>
          <button onClick={() => navigate("/")} className="continue-shopping-btn">
            Continue Shopping
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-container">
      <div className="cart-header">
        <h1>Shopping Cart ({getTotalItems()} items)</h1>
      </div>

      <div className="cart-content">
        <div className="cart-items-section">
          <div className="cart-items">
            {cartItems.map((item) => (
              <div key={item.id} className="cart-item">
                <div className="item-image">
                  <img
                    src={item.image || "https://via.placeholder.com/100"}
                    alt={item.name}
                  />
                </div>
                <div className="item-details">
                  <h3 className="item-name">{item.name}</h3>
                  <p className="item-description">{item.description?.substring(0, 50)}...</p>
                  <div className="item-rating">
                    ⭐ {item.rating?.toFixed(1) || "N/A"}
                  </div>
                </div>
                <div className="item-pricing">
                  <span className="item-price">₹{item.price?.toLocaleString()}</span>
                  {item.originalPrice && (
                    <span className="item-original-price">
                      M.R.P.: ₹{item.originalPrice?.toLocaleString()}
                    </span>
                  )}
                </div>
                <div className="item-quantity">
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity - 1)}
                    className="qty-btn"
                  >
                    −
                  </button>
                  <input
                    type="number"
                    value={item.quantity}
                    onChange={(e) =>
                      updateQuantity(item.id, Math.max(1, parseInt(e.target.value) || 1))
                    }
                    min="1"
                    className="qty-input"
                  />
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity + 1)}
                    className="qty-btn"
                  >
                    +
                  </button>
                </div>
                <div className="item-subtotal">
                  <span className="subtotal-label">Subtotal:</span>
                  <span className="subtotal-value">
                    ₹{(item.price * item.quantity)?.toLocaleString()}
                  </span>
                </div>
                <button
                  onClick={() => removeItem(item.id)}
                  className="remove-btn"
                  title="Remove from cart"
                >
                  ✕
                </button>
              </div>
            ))}
          </div>
        </div>

        <div className="cart-summary">
          <div className="summary-box">
            <h2>Order Summary</h2>

            <div className="summary-row">
              <span>Subtotal:</span>
              <span>₹{getTotalPrice()?.toLocaleString()}</span>
            </div>

            <div className="summary-row">
              <span>Discount:</span>
              <span className="discount-value">
                −₹{getTotalDiscount()?.toLocaleString()}
              </span>
            </div>

            <div className="summary-row">
              <span>Shipping:</span>
              <span className="free-shipping">FREE</span>
            </div>

            <div className="summary-row">
              <span>Estimated Tax:</span>
              <span>₹{(getTotalPrice() * 0.18)?.toLocaleString()}</span>
            </div>

            <div className="summary-divider"></div>

            <div className="summary-total">
              <span>Total:</span>
              <span className="total-amount">
                ₹{(getTotalPrice() + getTotalPrice() * 0.18)?.toLocaleString()}
              </span>
            </div>

            <button onClick={handleCheckout} className="checkout-btn">
              Proceed to Checkout
            </button>

            <button
              onClick={() => navigate("/")}
              className="continue-shopping-btn-secondary"
            >
              Continue Shopping
            </button>

            <div className="savings-info">
              <p>✓ You're saving ₹{getTotalDiscount()?.toLocaleString()}</p>
              <p>✓ Free shipping on this order</p>
              <p>✓ 7-day returns policy</p>
            </div>
          </div>
        </div>
      </div>

      <div className="cart-offers">
        <h3>Great offers you'll love:</h3>
        <div className="offers-grid">
          <div className="offer-card">
            <span className="offer-badge">FREE</span>
            <p>Free shipping on orders over ₹500</p>
          </div>
          <div className="offer-card">
            <span className="offer-badge">7 DAYS</span>
            <p>Easy returns and refunds</p>
          </div>
          <div className="offer-card">
            <span className="offer-badge">SECURE</span>
            <p>100% secure transactions</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Cart;
