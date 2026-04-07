import { useState, useEffect } from "react";
import API from "../api/api";
import "./Navbar.css";

function Navbar({ setProducts }) {
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState([]);

  const searchProducts = async () => {
    const res = await API.get(`/ai-search?query=${query}`);
    setProducts(res.data.data);
  };

  useEffect(() => {
    if (!query) {
      setSuggestions([]);
      return;
    }

    const timer = setTimeout(async () => {
      const res = await API.get(`/suggest?keyword=${query}`);
      setSuggestions(res.data.data);
    }, 300);

    return () => clearTimeout(timer);
  }, [query]);

  return (
    <div className="navbar">
          <div className="logo">Amazon</div>

          <div className="search-box">
              <input
                  value={query}
                  onChange={(e) => setQuery(e.target.value)}
                  placeholder="Search Amazon" />
              <button onClick={searchProducts}>🔍</button>
          </div>

          <div className="cart">🛒</div>
      </div>
  );
}

export default Navbar;