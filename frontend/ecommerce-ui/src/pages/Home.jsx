import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import {
  getAllProducts,
  searchProducts,
  filterByPrice,
  filterByCategory
} from "../api/productApi";
import ProductCard from "../components/ProductCard";

function Home() {
  const [products, setProducts] = useState([]);
  const [category, setCategory] = useState(null);
  const [price, setPrice] = useState({ min: "", max: "" });

  const location = useLocation();

  useEffect(() => {
    const fetchData = async () => {
      const params = new URLSearchParams(location.search);
      const search = params.get("search");

      let data;

      // 🔥 PRIORITY: SEARCH
      if (search) {
        data = await searchProducts(search);
      }
      // 🔥 CATEGORY FILTER
      else if (category) {
        data = await filterByCategory(category);
      }
      // 🔥 PRICE FILTER
      else if (price.min && price.max) {
        data = await filterByPrice(price.min, price.max);
      }
      // 🔥 DEFAULT
      else {
        data = await getAllProducts();
      }

      setProducts(data || []);
    };

    fetchData();
  }, [location, category, price]);

  return (
    <div className="bg-gray-100 min-h-screen">

      {/* HERO */}
      <img
        src="https://images.unsplash.com/photo-1607082349566-187342175e2f"
        className="w-full h-72 object-cover"
      />

      <div className="flex px-5 mt-5 gap-5">

        {/* SIDEBAR */}
        <div className="w-1/5 bg-white p-4 shadow rounded">

          {/* CATEGORY */}
          <h2 className="font-bold mb-2">Categories</h2>
          <ul className="space-y-2 text-sm mb-5">
            <li onClick={() => setCategory("Electronics")} className="cursor-pointer hover:text-yellow-600">Electronics</li>
            <li onClick={() => setCategory("Fashion")} className="cursor-pointer hover:text-yellow-600">Fashion</li>
            <li onClick={() => setCategory("Home")} className="cursor-pointer hover:text-yellow-600">Home</li>
            <li onClick={() => setCategory("Accessories")} className="cursor-pointer hover:text-yellow-600">Accessories</li>
          </ul>

          {/* PRICE FILTER */}
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
            onClick={() => setPrice({ ...price })}
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

        {/* PRODUCTS */}
        <div className="w-4/5 grid grid-cols-4 gap-5">
          {products.length > 0 ? (
            products.map((p) => (
              <ProductCard key={p.id} product={p} />
            ))
          ) : (
            <p>No products found</p>
          )}
        </div>

      </div>
    </div>
  );
}

export default Home;