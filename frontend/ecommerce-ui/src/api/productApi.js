import axios from "axios";

const BASE_URL = "http://localhost:8080/products";

export const getAllProducts = async () => {
  const res = await axios.get(`${BASE_URL}`);
  return res.data;
};

export const getProductById = async (id) => {
  const res = await axios.get(`${BASE_URL}/${id}`);
  return res.data;
};