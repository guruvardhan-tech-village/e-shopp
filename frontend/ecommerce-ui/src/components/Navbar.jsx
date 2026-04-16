import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Navbar() {
  const [query, setQuery] = useState("");
  const navigate = useNavigate();

  const handleSearch = () => {
    if (query.trim()) {
      navigate(`/?search=${query}`);
    }
  };

  return (
    <div className="bg-[#131921] text-white px-6 py-3 flex justify-between items-center">
      <h1 className="font-bold text-xl">AmazonClone</h1>

      <div className="flex w-1/2">
        <input
          className="w-full px-3 py-1 text-black"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search products..."
        />

        <button
          onClick={handleSearch}
          className="bg-yellow-400 px-4"
        >
          🔍
        </button>
      </div>
    </div>
  );
}

export default Navbar;