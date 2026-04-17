import { useEffect, useState, useContext } from "react";
import { useParams } from "react-router-dom";
import { getProductById } from "../api/productApi";
import { CartContext } from "../context/CartContext";

function Product() {
  const { id } = useParams();
  const { addToCart } = useContext(CartContext);

  const [product, setProduct] = useState({});
  const [qty, setQty] = useState(1);

  useEffect(() => {
    const fetchProduct = async () => {
      const data = await getProductById(id);
      setProduct(data);
    };

    fetchProduct();
  }, [id]);

  return (
    <div className="bg-gray-100 min-h-screen p-4">

      <div className="bg-white p-5 rounded shadow flex flex-col lg:flex-row gap-10">

        {/* 🔥 LEFT - IMAGE */}
        <div className="flex-1 flex justify-center">
          <img
            src={product.image || "http://localhost:8080/images/default.jpg"}
            className="w-full max-w-sm object-contain"
            alt={product.name}
          />
        </div>

        {/* 🔥 CENTER - DETAILS */}
        <div className="flex-1">

          <h1 className="text-xl lg:text-2xl font-semibold">
            {product.name}
          </h1>

          <p className="text-yellow-500 mt-2">⭐⭐⭐⭐☆</p>

          <hr className="my-3" />

          <p className="text-green-600 text-2xl font-bold">
            ₹{product.price}
          </p>

          <p className="mt-3 text-gray-600">
            {product.description}
          </p>

          <p className="mt-3 text-sm text-gray-500">
            Brand: {product.companyName}
          </p>

          <p className="mt-1 text-sm text-gray-500">
            Category: {product.category}
          </p>

          <p className="mt-1 text-sm text-green-600 font-semibold">
            {product.status}
          </p>
        </div>

        {/* 🔥 RIGHT - BUY BOX */}
        <div className="w-full lg:w-1/4 border p-4 rounded shadow h-fit">

          <p className="text-xl font-bold text-green-600">
            ₹{product.price}
          </p>

          <p className="text-sm text-gray-600 mt-2">
            FREE delivery
          </p>

          {/* QUANTITY */}
          <div className="mt-3">
            <label className="text-sm">Quantity:</label>
            <select
              value={qty}
              onChange={(e) => setQty(e.target.value)}
              className="border ml-2 p-1"
            >
              {[...Array(5)].map((_, i) => (
                <option key={i + 1}>{i + 1}</option>
              ))}
            </select>
          </div>

          {/* ADD TO CART */}
          <button
            onClick={() => addToCart({ ...product, qty })}
            className="mt-4 w-full bg-yellow-400 hover:bg-yellow-500 py-2 rounded"
          >
            Add to Cart
          </button>

          {/* BUY NOW */}
          <button className="mt-2 w-full bg-orange-500 hover:bg-orange-600 text-white py-2 rounded">
            Buy Now
          </button>

        </div>

      </div>
    </div>
  );
}

export default Product;