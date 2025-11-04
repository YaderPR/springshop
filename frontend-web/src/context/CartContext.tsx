// context/CartContext.tsx
import React, { createContext, useContext, ReactNode } from 'react';
import { useCartManager } from '../hooks/useCartManager';
// ?? Importa el hook Y el nuevo TIPO de item
import { useCart as useCartHook } from '../hooks/useCart'; 
import type { FullCartItem } from '../hooks/useCart';
import type { Product } from '../types/Product';

interface CartContextType {
  // ?? CAMBIA EL TIPO AQUÍ
  cartItems: FullCartItem[]; // <-- CAMBIADO
  total: number;
  loading: boolean;
  error: string | null;
  
  // ... (el resto de la interfaz no cambia)
  addToCart: (product: Product) => Promise<void>;
  removeFromCart: (itemId: number) => Promise<void>;
  updateCartItem: (itemId: number, productId: number, quantity: number) => Promise<void>;
  clearCart: () => Promise<void>;
  cartId: number | null;
  userId: number | null;
  isUserCart: boolean;
  getCartItemCount: () => number;
  isProductInCart: (productId: number) => boolean;
  getProductQuantity: (productId: number) => number;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

interface CartProviderProps {
  children: ReactNode;
}

export const CartProvider: React.FC<CartProviderProps> = ({ children }) => {
  const { cartId, userId, isUserCart, loading: cartManagerLoading } = useCartManager();
  
  // ?? 'cartItems' aquí AHORA es de tipo FullCartItem[]
  const { 
    cartItems, 
    total, 
    loading: cartLoading, 
    error, 
    addItem, 
    removeItem, 
    updateItem, 
    clearCart 
  } = useCartHook(cartId || undefined);

  const loading = cartManagerLoading || cartLoading;

  const addToCart = async (product: Product) => {
    if (!cartId) {
      throw new Error('Carrito no inicializado');
    }
    // Asegúrate que el DTO sea correcto (productId y quantity)
    await addItem({
      productId: product.id,
      quantity: 1
    });
  };

  const removeFromCart = async (itemId: number) => {
    if (!cartId) return;
    await removeItem(itemId);
  };

  const updateCartItem = async (itemId: number, productId: number, quantity: number) => {
    if (!cartId) return;
    await updateItem(itemId, { productId, quantity });
  };

  // ?? Esta lógica ahora funciona con FullCartItem[]
  const getCartItemCount = (): number => {
    return cartItems.reduce((total, item) => total + item.quantity, 0);
  };

  const isProductInCart = (productId: number): boolean => {
    return cartItems.some(item => item.productId === productId);
  };

  const getProductQuantity = (productId: number): number => {
    const item = cartItems.find(item => item.productId === productId);
    return item ? item.quantity : 0;
  };

  // El 'value' ahora pasa el tipo de item correcto
  const value: CartContextType = {
    cartItems, // <-- Este ahora es FullCartItem[]
    total,
    loading,
    error,
    addToCart,
    removeFromCart,
    updateCartItem,
    clearCart,
    cartId,
    userId,
    isUserCart,
    getCartItemCount,
    isProductInCart,
    getProductQuantity
  };

  return (
    <CartContext.Provider value={value}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = (): CartContextType => {
  const context = useContext(CartContext);
  if (context === undefined) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};