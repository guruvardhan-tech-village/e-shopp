import { createContext, useContext, useState } from "react";

export const CartContext = createContext();

// ✅ KEEP THIS (no need to move)
export const useCart = () => {
  return useContext(CartContext);
};

export function CartProvider({ children }) {
  const [cart, setCart] = useState([]);

  const addToCart = (product) => {
    const exists = cart.find((item) => item.id === product.id);
    const qtyToAdd = product.qty ? Number(product.qty) : 1;

    if (exists) {
      setCart(
        cart.map((item) =>
          item.id === product.id
            ? { ...item, qty: item.qty + 1 }
            ? { ...item, qty: item.qty + qtyToAdd }
            : item
        )
      );
    } else {
      setCart([...cart, { ...product, qty: 1 }]);
      setCart([...cart, { ...product, qty: qtyToAdd }]);
    }
  };

  const removeFromCart = (id) => {
    setCart(cart.filter((item) => item.id !== id));
  };

  const updateQty = (id, qty) => {
    setCart(
      cart.map((item) =>
        item.id === id ? { ...item, qty: Number(qty) } : item
      )
    );
  };

  return (
    <CartContext.Provider
      value={{ cart, addToCart, removeFromCart, updateQty }}
    >
      {children}
    </CartContext.Provider>
  );
}