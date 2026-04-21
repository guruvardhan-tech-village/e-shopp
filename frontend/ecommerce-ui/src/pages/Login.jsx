import { useState } from "react";
import { loginUser } from "../api/authApi";
import { useNavigate } from "react-router-dom";

function Login() {
  const [form, setForm] = useState({ email: "", password: "" });
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {

      // 🔐 STORE TOKEN
      const res = await loginUser(form);

      localStorage.setItem("token", res.token);
      localStorage.setItem("userName", res.name);
      localStorage.setItem("email", res.email);

      alert("Login successful ✅");

      navigate("/");
    } catch (err) {
      console.error(err);

      const msg = err.response?.data || "Login failed ❌";

      alert(msg);
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">

      <div className="bg-white p-6 rounded shadow w-80">

        <h2 className="text-xl font-bold mb-4">Sign In</h2>

        <input
          type="email"
          placeholder="Email"
          className="w-full mb-3 p-2 border"
          onChange={(e) =>
            setForm({ ...form, email: e.target.value })
          }
        />

        <input
          type="password"
          placeholder="Password"
          className="w-full mb-3 p-2 border"
          onChange={(e) =>
            setForm({ ...form, password: e.target.value })
          }
        />

        <button
          onClick={handleLogin}
          className="w-full bg-yellow-400 py-2 rounded"
        >
          Sign In
        </button>
      </div>
    </div>
  );
}

export default Login;