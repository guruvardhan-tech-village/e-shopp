import { motion } from "framer-motion";
import { Link } from "react-router-dom";

function ProductCard({ product }) {
  return (
    <motion.div
      whileHover={{ scale: 1.05 }}
      className="bg-white p-3 rounded shadow hover:shadow-xl"
    >
      <img
        src={product.image}
        className="h-40 w-full object-cover"
      />

      <h3 className="font-semibold mt-2">{product.name}</h3>

      <p className="text-yellow-500">⭐⭐⭐⭐☆</p>

      <p className="text-green-600 font-bold text-lg">
        ₹{product.price}
      </p>

      <Link to={`/product/${product.id}`}>
        <button className="mt-2 bg-yellow-400 w-full py-1 rounded">
          View Product
        </button>
      </Link>
    </motion.div>
  );
}

export default ProductCard;