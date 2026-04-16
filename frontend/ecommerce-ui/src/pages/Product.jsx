import { useEffect, useState, useContext } from "react";
import { useParams } from "react-router-dom";
import { getProductById } from "../api/productApi";
import { CartContext } from "../context/CartContext";

function Product() {
  const { id } = useParams();
  const [product, setProduct] = useState({});
  const { addToCart } = useContext(CartContext);

  useEffect(() => {
    loadProduct();
  }, []);

  const loadProduct = async () => {
    const data = await getProductById(id);
    setProduct(data);
  };

  return (
    <div className="p-10 flex gap-10">
      <img src={product.image} className="w-80 rounded" />

      <div>
        <h1 className="text-2xl font-bold">{product.name}</h1>
        <p className="my-3">{product.description}</p>

        <h2 className="text-green-600 text-xl font-semibold">
          ₹{product.price}
        </h2>

        <button
          onClick={() => addToCart(product)}
          className="mt-4 bg-yellow-400 px-4 py-2 rounded"
        >
          Add to Cart
        </button>
      </div>
    </div>
  );
}

export default Product;