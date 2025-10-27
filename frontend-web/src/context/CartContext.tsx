import React, { createContext, useContext, useState, useEffect } from "react";
import type { Product } from "../types/Product";
import {
  getCartItems,
  addItemToCart,
  updateCartItemQuantity,
  removeCartItem,
} from "../services/CartService";



interface CartContextType {
  cartItems: CartItem[];
  addToCart: (product: Product, quantity?: number) => Promise<void>;
  removeFromCart: (cartItemId: number) => Promise<void>;
  updateQuantity: (cartItemId: number, productId: number, quantity: number) => Promise<void>;
  clearCart: () => void;
  totalItems: number;
  totalPrice: number;
  isLoading: boolean;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [cartItems, setCartItems] = useState<CartItem[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);

  
  useEffect(() => {
    const fetchCart = async () => {
      try {
        setIsLoading(true);
        const data = await getCartItems();
        setCartItems(data);
      } catch (error) {
        console.error("Error cargando carrito:", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchCart();
  }, []);

  const addToCart = async (product: Product, quantity: number = 1) => {
    try {
      const existingItem = cartItems.find((item) => item.productId === product.id);
      if (existingItem) {
        const newQuantity = existingItem.quantity + quantity;
        const updatedItem = await updateCartItemQuantity(existingItem.id, product.id, newQuantity);

        setCartItems((prev) =>
          prev.map((item) =>
            item.id === updatedItem.id ? { ...item, quantity: updatedItem.quantity } : item
          )
        );
      } else {

        const newItem = await addItemToCart(product.id, quantity);
        setCartItems((prev) => [...prev, { ...newItem, product }]);
      }
    } catch (error) {
      console.error("Error al añadir producto al carrito:", error);
    }
  };

  // ?? Actualizar cantidad manualmente
  const updateQuantity = async (cartItemId: number, productId: number, quantity: number) => {
    try {
      const updatedItem = await updateCartItemQuantity(cartItemId, productId, quantity);
      setCartItems((prev) =>
        prev.map((item) =>
          item.id === updatedItem.id ? { ...item, quantity: updatedItem.quantity } : item
        )
      );
    } catch (error) {
      console.error("Error al actualizar cantidad:", error);
    }
  };

  // ?? Eliminar item del carrito
  const removeFromCart = async (cartItemId: number) => {
    try {
      await removeCartItem(cartItemId);
      setCartItems((prev) => prev.filter((item) => item.id !== cartItemId));
    } catch (error) {
      console.error("Error al eliminar item del carrito:", error);
    }
  };

  // ?? Vaciar carrito (solo localmente)
  const clearCart = () => {
    setCartItems([]);
  };

  // ?? Calcular totales
  const totalItems = cartItems.reduce((sum, item) => sum + item.quantity, 0);
  const totalPrice = cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0);

  return (
    <CartContext.Provider
      value={{
        cartItems,
        addToCart,
        removeFromCart,
        updateQuantity,
        clearCart,
        totalItems,
        totalPrice,
        isLoading,
      }}
    >
      {children}
    </CartContext.Provider>
  );
};

// Hook para usar el contexto fácilmente
export const useCart = (): CartContextType => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error("useCart debe usarse dentro de un CartProvider");
  }
  return context;
};
