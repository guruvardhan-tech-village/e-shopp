import { useState } from "react";
import { signupUser } from "../api/authApi";
import { useNavigate } from "react-router-dom";

function Signup() {
  const [form, setForm] = useState({
    name: "",
    phoneNumber:"",
    email: "",
    password: "",
  });

  const navigate = useNavigate();

  const handleSignup = async () => {
    try {
      await signupUser({ ...form, role: "USER" });

      alert("Signup successful ✅");

      navigate("/login");
    } catch (err) {
      console.error(err);
      alert("Signup failed ❌");
    }
  };

  return (
    <div className="flex justify-center items-center min-h-screen bg-gray-100">

      <div className="bg-white p-6 rounded shadow w-80">

        <h2 className="text-xl font-bold mb-4">Signup</h2>

        <input
          type="text"
          placeholder="Name"
          className="w-full mb-3 p-2 border"
          onChange={(e) =>
            setForm({ ...form, name: e.target.value })
          }
        />

        <input
        type="text"
        placeholder="Mobile Number"
        className="w-full mb-3 p-2 border"
        onChange={(e) =>
            setForm({ ...form, phoneNumber: e.target.value })
        }
        />

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
          onClick={handleSignup}
          className="w-full bg-yellow-400 py-2 rounded"
        >
          Signup
        </button>
      </div>
    </div>
  );
}

export default Signup;