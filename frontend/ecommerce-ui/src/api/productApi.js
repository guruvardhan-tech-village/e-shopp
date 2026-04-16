import axios from "axios";

const BASE_URL = "http://localhost:8080/api/products";

export const getAllProducts = async () => {
  const res = await axios.get(BASE_URL);
  return res.data.data;   // 🔥 IMPORTANT FIX
};

export const getProductById = async (id) => {
  const res = await axios.get(`${BASE_URL}/${id}`);
  return res.data.data;   // 🔥 IMPORTANT FIX
};