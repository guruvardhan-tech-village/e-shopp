// eslint-disable-next-line
import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { useCart } from "../context/CartContext";

function ProductCard({ product }) {
  const { addToCart } = useCart();

  return (
    <motion.div
      whileHover={{ scale: 1.05 }}
      className="bg-white p-3 rounded shadow hover:shadow-xl"
    >
      <img
        src={product.image || "http://localhost:8080/images/default.jpg"}
        className="h-40 w-full object-cover"
        alt={product.name}
      />

      <h3 className="font-semibold mt-2 line-clamp-2">
        {product.name}
      </h3>

      <p className="text-yellow-500">⭐⭐⭐⭐☆</p>

      <p className="text-green-600 font-bold text-lg">
        ₹{product.price}
      </p>

      <Link to={`/product/${product.id}`}>
        <button className="mt-2 bg-yellow-400 w-full py-1 rounded">
          View Product
        </button>
      </Link>

      <button
        onClick={() => addToCart(product)}
        className="mt-2 bg-yellow-500 w-full py-1 rounded"
      >
        Add to Cart
      </button>
    </motion.div>
  );
}

export default ProductCard;