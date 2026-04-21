import { useEffect, useState } from "react";
import axios from "../api/axios";

function Orders() {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    axios.get("/orders")
      .then(res => setOrders(res.data))
      .catch(err => console.error(err));
  }, []);

  return (
    <div className="p-5">
      <h1 className="text-2xl font-bold mb-5">My Orders</h1>

      {orders.length === 0 ? (
        <p>No orders found</p>
      ) : (
        orders.map(order => (
          <div key={order.id} className="bg-white p-4 mb-3 shadow rounded">
            <p><b>Order ID:</b> {order.id}</p>
            <p><b>Status:</b> {order.status}</p>
            <p><b>Total:</b> ₹{order.totalAmount}</p>
          </div>
        ))
      )}
    </div>
  );
}

export default Orders;