import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import API from "../api/api";
import "./Navbar.css";

function Navbar({ cartCount }) {
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const navigate = useNavigate();

  const handleSearch = async (searchQuery = query) => {
    if (!searchQuery.trim()) return;
    try {
      const res = await API.get(`/ai-search?query=${searchQuery}`);
      // Navigate to home with search results (you'd need to update Home to accept this)
      navigate("/?search=" + searchQuery);
    } catch (err) {
      console.error("Search error:", err);
    }
    setShowSuggestions(false);
    setQuery("");
  };

  const handleSuggestionClick = (suggestion) => {
    setQuery(suggestion);
    handleSearch(suggestion);
  };

  useEffect(() => {
    if (!query) {
      setSuggestions([]);
      setShowSuggestions(false);
      return;
    }

    setShowSuggestions(true);
    const timer = setTimeout(async () => {
      try {
        const res = await API.get(`/suggest?keyword=${query}`);
        setSuggestions(res.data.data || []);
      } catch (err) {
        console.error("Suggestion error:", err);
        setSuggestions([]);
      }
    }, 300);

    return () => clearTimeout(timer);
  }, [query]);

  return (
    <div className="navbar">
      <Link to="/" className="logo">
        <span className="logo-text">Amazon</span>
      </Link>

      <div className="search-box">
        <div className="search-input-wrapper">
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyPress={(e) => e.key === "Enter" && handleSearch()}
            onFocus={() => query && setShowSuggestions(true)}
            onBlur={() => setTimeout(() => setShowSuggestions(false), 200)}
            placeholder="Search Amazon"
            className="search-input"
          />
          {showSuggestions && suggestions.length > 0 && (
            <div className="suggestions-dropdown">
              {suggestions.map((suggestion, index) => (
                <div
                  key={index}
                  className="suggestion-item"
                  onClick={() => handleSuggestionClick(suggestion)}
                >
                  🔍 {suggestion}
                </div>
              ))}
            </div>
          )}
        </div>
        <button onClick={() => handleSearch()} className="search-btn">
          🔍
        </button>
      </div>

      <Link to="/cart" className="cart">
        <span className="cart-icon">🛒</span>
        {cartCount > 0 && <span className="cart-count">{cartCount}</span>}
      </Link>
    </div>
  );
}

export default Navbar;