import { useContext, useState } from "react";
import { CartContext } from "../context/CartContext";

function Checkout() {
  const { cart } = useContext(CartContext);

  const [address, setAddress] = useState("");

  const total = cart.reduce(
    (acc, item) => acc + item.price * item.qty,
    0
  );

  const handlePayment = () => {
    alert("Payment Successful ✅");
  };

  return (
    <div className="p-5">

      <h1 className="text-2xl font-bold mb-5">Checkout</h1>

      <div className="flex flex-col lg:flex-row gap-5">

        {/* ADDRESS */}
        <div className="flex-1 bg-white p-4 shadow rounded">
          <h2 className="font-semibold mb-3">Shipping Address</h2>

          <textarea
            className="w-full border p-2"
            placeholder="Enter address"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
          />
        </div>

        {/* SUMMARY */}
        <div className="w-full lg:w-1/3 bg-white p-4 shadow rounded">

          <h2 className="font-semibold mb-3">Order Summary</h2>

          <p>Total: ₹{total}</p>

          <button
            onClick={handlePayment}
            className="mt-4 w-full bg-orange-500 text-white py-2 rounded"
          >
            Pay Now
          </button>

        </div>

      </div>
    </div>
  );
}

export default Checkout;