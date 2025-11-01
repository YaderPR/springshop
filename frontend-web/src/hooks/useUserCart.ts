// hooks/useUserCart.ts
import { useState, useEffect } from 'react';
import { cartService } from '../services/cartService';

export const useUserCart = (userId: number) => {
  const [currentCartId, setCurrentCartId] = useState<number | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Buscar o crear carrito para el usuario
  const initializeUserCart = async () => {
    if (!userId) return;
    
    setLoading(true);
    setError(null);
    try {
      // En una implementación real, buscarías el carrito activo del usuario
      // Por ahora, creamos uno nuevo cada vez (puedes ajustar esta lógica)
      const newCart = await cartService.createCart(userId);
      setCurrentCartId(newCart.id);
      return newCart.id;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Error al inicializar carrito';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (userId) {
      initializeUserCart();
    }
  }, [userId]);

  return {
    cartId: currentCartId,
    loading,
    error,
    refreshCart: initializeUserCart
  };
};