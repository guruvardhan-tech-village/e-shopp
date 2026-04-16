import { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import { getAllProducts, searchProducts } from "../api/productApi";
import ProductCard from "../components/ProductCard";

function Home() {
  const [products, setProducts] = useState([]);
  const location = useLocation();

  useEffect(() => {
    loadProducts();
  }, [location]);

  const loadProducts = async () => {
    const params = new URLSearchParams(location.search);
    const search = params.get("search");

    let data;

    if (search) {
      data = await searchProducts(search);
    } else {
      data = await getAllProducts();
    }

    setProducts(data);
  };

  return (
    <div className="p-5">
      {/* Banner */}
      <img
        src="https://images.unsplash.com/photo-1607082349566-187342175e2f"
        className="w-full h-60 object-cover rounded mb-5"
      />

      {/* Products */}
      <div className="grid grid-cols-4 gap-5">
        {products.map((p) => (
          <ProductCard key={p.id} product={p} />
        ))}
      </div>
    </div>
  );
}

export default Home;