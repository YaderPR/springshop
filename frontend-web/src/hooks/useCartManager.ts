// hooks/useCartManager.ts
import { useState, useEffect } from 'react';
import { cartService } from '../services/cart/cartService';
import { userService } from '../services/user/userService';

export const useCartManager = () => {
  const [currentCartId, setCurrentCartId] = useState<number | null>(null);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [isUserCart, setIsUserCart] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // Crear usuario temporal para carritos anónimos
  const createTemporaryUser = async (): Promise<number> => {
    try {
      // Crear un usuario temporal con un username único
      const tempUsername = `guest_${Date.now()}`;
      const user = await userService.createTemporaryUser(tempUsername);
      return user.id;
    } catch (err: any) {
      console.error('Error creating temporary user:', err);
      // Si falla la creación de usuario temporal, usar userId 0 como fallback
      return 0;
    }
  };

  // Inicializar carrito al montar el componente
  const initializeCart = async () => {
    setLoading(true);
    setError(null);

    try {
      let cartId: number;
      let userId: number;

      // Por ahora, todos los usuarios son anónimos
      // Más adelante puedes integrar la autenticación real aquí
      const tempUserId = await createTemporaryUser();
      userId = tempUserId;
      
      // Buscar si ya existe un carrito para este usuario
      const savedCartId = localStorage.getItem('anonymousCartId');
      
      if (savedCartId) {
        // Verificar que el carrito aún existe
        try {
          const existingCart = await cartService.getCartById(parseInt(savedCartId));
          cartId = existingCart.id;
          
          // Verificar que el carrito pertenece al usuario actual
          if (existingCart.userId !== userId) {
            // Si no pertenece, crear uno nuevo
            const newCart = await cartService.createUserCart(userId);
            cartId = newCart.id;
          }
        } catch {
          // Si el carrito no existe, crear uno nuevo
          const newCart = await cartService.createUserCart(userId);
          cartId = newCart.id;
        }
      } else {
        // Crear nuevo carrito
        const newCart = await cartService.createUserCart(userId);
        cartId = newCart.id;
      }

      // Guardar en localStorage
      localStorage.setItem('anonymousCartId', cartId.toString());
      localStorage.setItem('anonymousUserId', userId.toString());
      
      setCurrentCartId(cartId);
      setCurrentUserId(userId);
      setIsUserCart(false); // Por ahora todos son anónimos

      return cartId;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Error al inicializar carrito';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  // Convertir carrito anónimo a usuario registrado (para el futuro)
  const convertToUserCart = async (registeredUserId: number) => {
    if (!currentCartId) return;

    try {
      // Actualizar el carrito con el nuevo userId
      await cartService.updateCart(currentCartId, registeredUserId);
      
      setCurrentUserId(registeredUserId);
      setIsUserCart(true);
      
      // Limpiar localStorage ya que ahora es un carrito de usuario registrado
      localStorage.removeItem('anonymousCartId');
      localStorage.removeItem('anonymousUserId');
    } catch (error) {
      console.error('Error converting to user cart:', error);
    }
  };

  // Efecto para inicializar al montar
  useEffect(() => {
    initializeCart();
  }, []);

  return {
    cartId: currentCartId,
    userId: currentUserId,
    isUserCart,
    loading,
    error,
    refreshCart: initializeCart,
    convertToUserCart
  };
};