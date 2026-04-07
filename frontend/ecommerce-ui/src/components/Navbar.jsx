import React from "react";
import "./Navbar.css";

function Navbar() {
  return (
    <div className="navbar">
      
      {/* Logo */}
      <h2 className="logo">Amazon</h2>

      {/* Search Bar */}
      <input
        type="text"
        placeholder="Search products..."
        className="search"
      />

      {/* Cart */}
      <div className="cart">
        🛒 Cart
      </div>

    </div>
  );
}

export default Navbar;