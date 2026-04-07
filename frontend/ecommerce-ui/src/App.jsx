import { useState } from "react";
import Navbar from "./components/Navbar";

export default function App() {
  const [products, setProducts] = useState([]);

  return (
    <div>
      <Navbar setProducts={setProducts} />

      <h2>Products</h2>

      {products.map((p) => (
        <div key={p.id}>
          {p.name} - ₹{p.price}
        </div>
      ))}
    </div>
  );
}