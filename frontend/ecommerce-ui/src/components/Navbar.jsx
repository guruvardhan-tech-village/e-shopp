import { Link, useNavigate } from "react-router-dom";
import { useState, useContext } from "react";
import { CartContext } from "../context/CartContext";

function Navbar() {
  const [query, setQuery] = useState("");
  const [openMenu, setOpenMenu] = useState(false);
  const navigate = useNavigate();
  const { cart } = useContext(CartContext);
  const token = localStorage.getItem("token");

  const handleSearch = () => {
    if (query.trim()) {
      navigate(`/?search=${query}`);
    }
  };

  return (
    <>
      {/* 🔥 TOP NAVBAR */}
      <div className="bg-[#131921] text-white px-4 py-2 flex items-center gap-4">

        {/* LOGO */}
        <Link to="/" className="text-xl font-bold whitespace-nowrap">
          amazon
        </Link>

        {/* LOCATION */}
        <div className="hidden sm:block text-sm">
          <p>Deliver to</p>
          <p className="font-semibold">India</p>
        </div>

        {/* SEARCH */}
        <div className="flex flex-1">
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search Amazon"
            className="w-full px-3 py-2 text-black"
          />
          <button
            onClick={handleSearch}
            className="bg-yellow-400 px-4 text-black"
          >
            🔍
          </button>
        </div>

        {/* ACCOUNT */}
        <div className="hidden md:block text-sm">
          {token ? (
            <p className="text-sm">Logged In</p>
          ) : (
            <Link to="/login">Login</Link>
          )}
        </div>

        {/* ORDERS */}
        <div className="hidden md:block text-sm">
          <p>Returns</p>
          <p className="font-semibold">& Orders</p>
        </div>

        {/* CART */}
        <Link to="/cart" className="relative text-lg">
          🛒
          <span className="absolute -top-2 -right-3 bg-yellow-400 text-black text-xs px-1 rounded">
            {cart?.length || 0}
          </span>
        </Link>

      </div>

      {/* 🔥 SECOND NAVBAR */}
      <div className="bg-[#232f3e] text-white px-4 py-2 flex gap-4 text-sm">

        {/* MENU BUTTON */}
        <button onClick={() => setOpenMenu(true)}>
          ☰ All
        </button>

        <span className="hidden sm:block">Today's Deals</span>
        <span className="hidden sm:block">Customer Service</span>
        <span className="hidden sm:block">Electronics</span>
        <span className="hidden sm:block">Fashion</span>
      </div>

      {/* 🔥 SIDEBAR MENU */}
      {openMenu && (
        <div className="fixed inset-0 bg-black bg-opacity-50 z-50">

          <div className="w-64 bg-white h-full p-4">
            <button
              onClick={() => setOpenMenu(false)}
              className="mb-4"
            >
              ❌ Close
            </button>

            <h2 className="font-bold mb-3">Shop by Category</h2>

            <ul className="space-y-2">
              <li className="cursor-pointer">Electronics</li>
              <li className="cursor-pointer">Fashion</li>
              <li className="cursor-pointer">Home</li>
              <li className="cursor-pointer">Appliances</li>
            </ul>
          </div>

        </div>
      )}
    </>
  );
}

export default Navbar;