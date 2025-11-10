// hooks/useCartManager.ts
import { useState, useEffect } from 'react';
import { cartService } from '../services/cart/cartService';
import { getUserById, createGuestUser, createTemporaryUser } from '../services/user/UserService'; 

export const useCartManager = () => {
  const [currentCartId, setCurrentCartId] = useState<number | null>(null);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [isUserCart, setIsUserCart] = useState<boolean>(false); 
  const [loading, setLoading] = useState<boolean>(true); 
  const [error, setError] = useState<string | null>(null);


  const initializeGuestSession = async () => {
    setLoading(true);
    setError(null);

    try {
      let userId: number;
      const storedGuestId = localStorage.getItem('guestUserId');

      if (storedGuestId) {

        userId = parseInt(storedGuestId, 10);
        console.log(`Usuario invitado existente: ${userId}`);
      } else {

        console.log("No se encontró usuario invitado. Creando uno nuevo...");
        const newGuestUser = await createTemporaryUser();

        if (!newGuestUser || !newGuestUser.id) {
          throw new Error("La respuesta de 'createTemporaryUser', no contenía un ID.");
        }
        
        userId = newGuestUser.id;
        localStorage.setItem('guestUserId', userId.toString());
        console.log(`Nuevo usuario invitado creado: ${userId}`);
      }

      userService

      setCurrentUserId(userId);
      setIsUserCart(false); // Es un invitado, no un usuario autenticado

      let cartId: number;
      const storedCartId = localStorage.getItem('guestCartId'); 

      if (storedCartId) {

        try {
          const existingCart = await cartService.getCartById(parseInt(storedCartId, 10));
          
          if (existingCart.userId === userId) {
            cartId = existingCart.id;
          } else {

            console.warn("El carrito guardado no pertenece al usuario invitado actual. Creando uno nuevo.");
            const newCart = await cartService.createUserCart(userId);
            cartId = newCart.id;
          }
        } catch (err) {
          // El 'getCartById' falló (ej. 404). El ID guardado es basura. Creamos uno nuevo.
          console.warn("El carrito guardado no se encontró en el backend. Creando uno nuevo.");
          const newCart = await cartService.createUserCart(userId);
          cartId = newCart.id;
        }
      } else {
        // No hay ID de carrito guardado. Creamos uno nuevo para nuestro usuario.
        console.log(`No hay carrito guardado para el usuario ${userId}. Creando uno nuevo.`);
        const newCart = await cartService.createUserCart(userId);
        cartId = newCart.id;
      }

      // Guardamos el ID del carrito
      localStorage.setItem('guestCartId', cartId.toString());
      setCurrentCartId(cartId);
      
    } catch (err: any) {
      console.error('Falló la inicialización de la sesión de invitado:', err);
      
      let errorMessage = "Error al inicializar sesión.";
      if (err.message.includes("invitado")) {
          errorMessage = "Error crítico al crear usuario invitado. El servicio de usuarios puede estar desconectado.";
      } else {
          errorMessage = err.response?.data?.message || 'Error al inicializar carrito';
      }
      
      setError(errorMessage);
      
      // Limpiamos el localStorage para reintentar desde cero la próxima vez
      localStorage.removeItem('guestUserId');
      localStorage.removeItem('guestCartId');
    } finally {
      setLoading(false);
    }
  };

  // Función para el futuro (cuando integres Keycloak)
  const convertToUserCart = async (registeredUserId: number) => {
    if (!currentCartId) return;
    try {
      await cartService.updateCart(currentCartId, registeredUserId);
      setCurrentUserId(registeredUserId);
      setIsUserCart(true);
      // Limpiar localStorage de invitado
      localStorage.removeItem('guestUserId');
      localStorage.removeItem('guestCartId');
      // (Aquí guardarías la info del usuario real)
    } catch (error) {
      console.error('Error converting to user cart:', error);
    }
  };

  // Efecto para inicializar al montar
  useEffect(() => {
    initializeGuestSession();
  }, []); // Se ejecuta solo una vez al cargar la app

  return {
    cartId: currentCartId,
    userId: currentUserId, // <-- ¡Este ID ahora es real (ej. 125)!
    isUserCart,
    loading,
    error,
    refreshCart: initializeGuestSession, 
    convertToUserCart
  };
};