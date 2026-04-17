import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import {
  getAllProducts,
  searchProducts,
  filterByPrice,
  filterByCategory
} from "../api/productApi";
import ProductCard from "../components/ProductCard";
import HeroSlider from "../components/HeroSlider";

function Home() {
  const [products, setProducts] = useState([]);
  const [category, setCategory] = useState(null);
  const [price, setPrice] = useState({ min: "", max: "" });

  const location = useLocation();

  // 🔥 DATA FETCH (CLEAN)
  useEffect(() => {
    const fetchData = async () => {
      const params = new URLSearchParams(location.search);
      const search = params.get("search");

      let data;

      if (search) {
        data = await searchProducts(search);
      } else if (category) {
        data = await filterByCategory(category);
      } else if (price.min && price.max) {
        data = await filterByPrice(price.min, price.max);
      } else {
        data = await getAllProducts();
      }

      setProducts(data || []);
    };

    fetchData();
  }, [location, category, price]);

  return (
    <div className="bg-gray-100 min-h-screen">

      {/* 🔥 HERO SLIDER */}
      <HeroSlider />

      {/* 🔥 OVERLAY CARDS (AMAZON STYLE) */}
      <div className="relative -mt-32 px-5 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 z-10">

        <div className="bg-white p-4 shadow rounded">
          <h2 className="font-bold mb-2">Gaming</h2>
          <img
            src="https://images.unsplash.com/photo-1606813907291-d86efa9b94db"
            className="h-40 w-full object-cover"
          />
        </div>

        <div className="bg-white p-4 shadow rounded">
          <h2 className="font-bold mb-2">Home Essentials</h2>
          <img
            src="https://images.unsplash.com/photo-1586023492125-27b2c045efd7"
            className="h-40 w-full object-cover"
          />
        </div>

        <div className="bg-white p-4 shadow rounded">
          <h2 className="font-bold mb-2">Kitchen</h2>
          <img
            src="https://images.unsplash.com/photo-1556911220-e15b29be8c8f"
            className="h-40 w-full object-cover"
          />
        </div>

        <div className="bg-white p-4 shadow rounded">
          <h2 className="font-bold mb-2">Fashion</h2>
          <img
            src="https://images.unsplash.com/photo-1521335629791-ce4aec67dd53"
            className="h-40 w-full object-cover"
          />
        </div>

      </div>

      {/* 🔥 MAIN CONTENT */}
      <div className="flex flex-col lg:flex-row px-5 mt-5 gap-5">

        {/* 🔥 SIDEBAR FILTERS */}
        <div className="w-full lg:w-1/5 bg-white p-4 shadow rounded h-fit">

          {/* CATEGORY */}
          <h2 className="font-bold mb-2">Categories</h2>
          <ul className="space-y-2 text-sm mb-5">
            <li onClick={() => setCategory("Electronics")} className="cursor-pointer hover:text-yellow-600">Electronics</li>
            <li onClick={() => setCategory("Fashion")} className="cursor-pointer hover:text-yellow-600">Fashion</li>
            <li onClick={() => setCategory("Home")} className="cursor-pointer hover:text-yellow-600">Home</li>
            <li onClick={() => setCategory("Accessories")} className="cursor-pointer hover:text-yellow-600">Accessories</li>
          </ul>

          {/* PRICE */}
          <h2 className="font-bold mb-2">Price</h2>

          <input
            type="number"
            placeholder="Min"
            className="w-full mb-2 p-1 border"
            value={price.min}
            onChange={(e) => setPrice({ ...price, min: e.target.value })}
          />

          <input
            type="number"
            placeholder="Max"
            className="w-full mb-2 p-1 border"
            value={price.max}
            onChange={(e) => setPrice({ ...price, max: e.target.value })}
          />

          <button
            className="bg-yellow-400 w-full py-1 rounded"
          >
            Apply
          </button>

          {/* RESET */}
          <button
            onClick={() => {
              setCategory(null);
              setPrice({ min: "", max: "" });
            }}
            className="mt-2 text-sm text-blue-500"
          >
            Reset Filters
          </button>

        </div>

        {/* 🔥 PRODUCTS GRID */}
        <div className="w-full lg:w-4/5 grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-5">
          {products.length > 0 ? (
            products.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))
          ) : (
            <p className="text-gray-500">No products found</p>
          )}
        </div>

      </div>
    </div>
  );
}

export default Home;