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
    <div className="p-5">
      <h1 className="text-2xl font-bold mb-5">My Orders</h1>

      {error && <p className="mb-4 text-red-600">{error}</p>}

      {Array.isArray(orders) && orders.length > 0 ? (
        orders.map(order => (
          <div key={order.id} className="bg-white p-4 mb-3 shadow rounded">
            <p><b>Order ID:</b> {order.id}</p>
            <p><b>Status:</b> {order.status}</p>
            <p><b>Total:</b> ₹{order.totalAmount}</p>
          </div>
        ))
      ) : (
        <p>No orders found</p>
      )}
    </div>
  );
}

export default Orders;
