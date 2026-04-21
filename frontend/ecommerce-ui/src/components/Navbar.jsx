import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { useCart } from "../context/CartContext";

function Navbar() {
  const [query, setQuery] = useState("");
  const [openMenu, setOpenMenu] = useState(false);
  const navigate = useNavigate();

  const { cart } = useCart(); // ✅ FIXED

  const token = localStorage.getItem("token");
  const userName = localStorage.getItem("userName");
  const [showDropdown, setShowDropdown] = useState(false);

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
        <div
          className="relative hidden md:block text-sm cursor-pointer"
          onMouseEnter={() => setShowDropdown(true)}
          onMouseLeave={() => setShowDropdown(false)}
        >
          {token ? (
            <>
              <p>Hello, {userName}</p>
              <p className="font-semibold">Account & Lists ▾</p>
            </>
          ) : (
            <>
              <p>Hello, sign in</p>
              <p className="font-semibold">Account & Lists ▾</p>
            </>
          )}

          {/* DROPDOWN */}
          {showDropdown && (
            <div className="absolute right-0 top-10 w-[500px] bg-white text-black shadow-lg p-5 z-50">
              <li onClick={() => navigate("/orders")}>Orders</li>
              {/* SIGN IN BUTTON */}
              {!token && (
                <div className="text-center mb-4">
                  <button 
                  onClick={() => navigate("/login")}
                  className="bg-yellow-400 px-6 py-2 rounded w-full"
                  >                  
                    Sign in
                  </button>
                  <p className="text-sm mt-2">
                    New customer?{" "}
                    <span 
                    onClick={() => navigate("/signup")}
                    className="text-blue-500 cursor-pointer">
                      Start here
                    </span>
                  </p>
                </div>
              )}

              <hr className="my-3" />

              {/* GRID */}
              <div className="grid grid-cols-2 gap-5">

                {/* LEFT */}
                <div>
                  <h3 className="font-bold mb-2">Your Lists</h3>
                  <ul className="space-y-1 text-sm">
                    <li>Create a List</li>
                    <li>Find a List or Registry</li>
                  </ul>
                </div>

                {/* RIGHT */}
                <div>
                  <h3 className="font-bold mb-2">Your Account</h3>
                  <ul className="space-y-1 text-sm">
                    <li>Account</li>
                    <li>Orders</li>
                    <li>Recommendations</li>
                    <li>Browsing History</li>
                    <li>Watchlist</li>
                    <li>Memberships</li>
                  </ul>
                </div>

              </div>
              {token && (
                <button
                  onClick={() => {
                    localStorage.clear();
                    window.location.reload();
                  }}
                  className="mt-4 text-red-500"
                >
                  Logout
                </button>
              )}

            </div>
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