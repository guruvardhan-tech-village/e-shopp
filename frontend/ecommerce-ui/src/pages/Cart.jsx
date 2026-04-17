import { useContext } from "react";
import { CartContext } from "../context/CartContext";
import { useNavigate } from "react-router-dom";

function Cart() {
  const { cart, removeFromCart, updateQty } = useContext(CartContext);

  const total = cart.reduce(
    (acc, item) => acc + item.price * item.qty,
    0
  );

  const navigate = useNavigate();

  <button
    onClick={() => navigate("/checkout")}
    className="mt-4 w-full bg-yellow-400 py-2 rounded"
  >
    Proceed to Checkout
  </button>

  return (
    <div className="bg-gray-100 min-h-screen p-4">

      <h1 className="text-2xl font-bold mb-5">Shopping Cart</h1>

      <div className="flex flex-col lg:flex-row gap-5">

        {/* LEFT - ITEMS */}
        <div className="flex-1 space-y-4">
          {cart.length > 0 ? (
            cart.map((item) => (
              <div
                key={item.id}
                className="bg-white p-4 rounded shadow flex gap-4"
              >
                <img
                  src={item.image}
                  className="w-24 h-24 object-contain"
                />

                <div className="flex-1">
                  <h2 className="font-semibold">{item.name}</h2>

                  <p className="text-green-600 font-bold">
                    ₹{item.price}
                  </p>

                  {/* QTY */}
                  <select
                    value={item.qty}
                    onChange={(e) =>
                      updateQty(item.id, e.target.value)
                    }
                    className="border mt-2 p-1"
                  >
                    {[...Array(5)].map((_, i) => (
                      <option key={i + 1}>{i + 1}</option>
                    ))}
                  </select>

                  {/* REMOVE */}
                  <button
                    onClick={() => removeFromCart(item.id)}
                    className="block text-red-500 text-sm mt-2"
                  >
                    Remove
                  </button>
                </div>
              </div>
            ))
          ) : (
            <p>Your cart is empty</p>
          )}
        </div>

        {/* RIGHT - TOTAL */}
        <div className="w-full lg:w-1/4 bg-white p-4 rounded shadow h-fit">

          <h2 className="text-lg font-semibold mb-3">
            Subtotal ({cart.length} items)
          </h2>

          <p className="text-xl font-bold text-green-600">
            ₹{total}
          </p>

          <button className="mt-4 w-full bg-yellow-400 py-2 rounded">
            Proceed to Checkout
          </button>

        </div>

      </div>
    </div>
  );
}

export default Cart;