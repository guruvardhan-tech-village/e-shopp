import axios from "axios";

const BASE_URL = "http://localhost:8080/api/products";

export const getAllProducts = async () => {
  const res = await axios.get(BASE_URL);
  return res.data.data;
};

export const getProductById = async (id) => {
  const res = await axios.get(`${BASE_URL}/${id}`);
  return res.data.data;
};

// 🔥 ADD THIS (MISSING FUNCTION)
export const searchProducts = async (keyword) => {
  const res = await axios.get(`${BASE_URL}/search?keyword=${keyword}`);
  return res.data.data;
};

export const filterByPrice = async (min, max) => {
  const res = await axios.get(
    `http://localhost:8080/api/products/price-range?min=${min}&max=${max}`
  );
  return res.data.data;
};

export const filterByCategory = async (category) => {
  const res = await axios.get(
    `http://localhost:8080/api/products/recommend?category=${category}`
  );
  return res.data.data;
};