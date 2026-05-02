import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const checkAuth = async () => {
      const token = localStorage.getItem('token');
      const displayName = localStorage.getItem('displayName');
      if (token) {
        try {
          setUser({ token, displayName });
        } catch (error) {
          localStorage.removeItem('token');
          localStorage.removeItem('displayName');
        }
      }
      setLoading(false);
    };
    checkAuth();
  }, []);

  const login = async (credentials) => {
    const res = await api.post('/auth/login', credentials);
    const { token, displayName } = res.data.data;
    localStorage.setItem('token', token);
    if (displayName) {
      localStorage.setItem('displayName', displayName);
    }
    setUser({ token, displayName });
    return res.data;
  };

  const register = async (data) => {
    const res = await api.post('/auth/register', data);
    return res.data;
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('displayName');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};
