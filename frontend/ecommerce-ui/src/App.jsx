import { useEffect, useState } from "react";
import API from "./api/api";

export default function App() {
  const [query, setQuery] = useState("");
  const [products, setProducts] = useState([]);
  const [suggestions, setSuggestions] = useState([]);

  // 🔍 Search
  const searchProducts = async () => {
    const res = await API.get(`/ai-search?query=${query}`);
    setProducts(res.data.data);
  };

  // ⚡ Suggestions (debounce)
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
    <div style={{ padding: "20px" }}>
      <h1>🛒 E-Commerce AI Search</h1>

      <input
        type="text"
        placeholder="Search..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        style={{ padding: "10px", width: "300px" }}
      />

      <button onClick={searchProducts} style={{ marginLeft: "10px" }}>
        Search
      </button>

      {/* Suggestions */}
      <div>
        {suggestions.map((s, i) => (
          <div 
              key={i} 
              onClick={() => {
                setQuery(s);
                setSuggestions([]);
              }}
              style={{ cursor: "pointer", padding: "5px" }}
            >
          {s}
        </div>

        ))}
      </div>

      <hr />

      {/* Products */}
      <div>
        {products.map((p) => (
          <div key={p.id} style={{ marginBottom: "10px" }}>
            <b>{p.name}</b> - ₹{p.price} ({p.companyName})
          </div>
        ))}
      </div>
    </div>
  );
}