import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home";
import Product from "../pages/Product";
import Cart from "../pages/Cart";
import Checkout from "../pages/Checkout";
import Login from "../pages/Login";
import Signup from "../pages/Signup";
import ProtectedRoute from "../components/ProtectedRoute";
import Orders from "../pages/Orders";

function AppRoutes() {
  return (
    


    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/product/:id" element={<Product />} />

      {/* CART */}
      <Route path="/cart" element={<Cart />} />

      {/* 🔐 PROTECTED CHECKOUT */}
      <Route
        path="/checkout"
        element={
          <ProtectedRoute>
            <Checkout />
          </ProtectedRoute>
        }
      />

      {/* AUTH */}
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route path="/orders" element={<ProtectedRoute><Orders /></ProtectedRoute>} />
    </Routes>
  );
}

export default AppRoutes;