import axios from "axios";

const BASE_URL = "http://localhost:8080/api/auth";

export const signupUser = async (user) => {
  const res = await axios.post(`${BASE_URL}/signup`, user);
  return res.data;
};

export const loginUser = async (user) => {
  const res = await axios.post(`${BASE_URL}/login`, user);
  return res.data; // JWT token
};