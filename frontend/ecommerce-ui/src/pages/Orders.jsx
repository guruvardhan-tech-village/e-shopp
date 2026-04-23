import { useEffect, useState } from "react";
import axios from "../api/axios";

function Orders() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    axios.get("/orders")
      .then((res) => {
        const payload = Array.isArray(res.data)
          ? res.data
          : Array.isArray(res.data?.data)
            ? res.data.data
            : [];

        setOrders(payload);
        setError("");
      })
      .catch((err) => {
        console.error(err);
        setOrders([]);
        setError("Failed to load orders.");
      });
  }, []);

  return (
    <div className="bg-gray-100 min-h-screen p-5">

      <h1 className="text-2xl font-bold mb-5">My Orders</h1>

      {error && <p className="mb-4 text-red-600">{error}</p>}

      {Array.isArray(orders) && orders.length > 0 ? (

        orders.map(order => (
          <div key={order.id} className="bg-white p-4 mb-5 shadow rounded">

            {/* HEADER */}
            <div className="flex justify-between border-b pb-2 mb-3 text-sm">
              <div>
                <p className="font-semibold">Order ID: {order.id}</p>
                <p>Status: {order.status}</p>
              </div>

              <div className="text-green-600 font-bold">
                ₹{order.total || order.totalAmount}
              </div>
            </div>

            {/* ITEMS */}
            {order.items && order.items.length > 0 ? (
              <div className="space-y-3">
                {order.items.map((item, index) => (
                  <div key={index} className="flex gap-4">

                    <img
                      src={`http://localhost:8080/images/${item.image}`}
                      className="w-24 h-24 object-contain border rounded"
                      alt={item.name}
                    />

                    <div>
                      <p className="font-medium">{item.name}</p>
                      <p>Qty: {item.qty}</p>
                      <p className="text-green-600">₹{item.price}</p>
                    </div>

                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-500">No items found</p>
            )}

          </div>
        ))

      ) : (
        <p>No orders found</p>
      )}
    </div>
  );
}

export default Orders;

//{A B C D E F G H I J K L M N O P Q R S T U V W X Y Z}
//{a b c d e f g h i j k l m n o p q r s t u v w x y z}