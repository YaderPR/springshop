import { useState, useEffect } from 'react';
import { cartService } from '../services/cart/CartService';
import { getUserById, createGuestUser, createTemporaryUser, syncUser } from '../services/user/UserService'; 
import type { UserProfileRequest } from '../types/User.types';
import { useKeycloak } from '@react-keycloak/web'; 

export const useCartManager = () => {
  const { keycloak, initialized } = useKeycloak();
  
  const [currentCartId, setCurrentCartId] = useState<number | null>(null);
  const [currentUserId, setCurrentUserId] = useState<number | null>(null);
  const [isUserCart, setIsUserCart] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // --- LÓGICA 1: USUARIO INVITADO (Sin sesión) ---
  const initializeGuestSession = async () => {
    setLoading(true);
    try {
      let userId: number;
      const storedGuestId = localStorage.getItem('guestUserId');

      if (storedGuestId) {
        userId = parseInt(storedGuestId, 10);
      } else {
        // Crear usuario invitado nuevo
        // Usamos un objeto simple ya que syncUser no necesita body,
        // pero si usas createGuestUser, mantén la estructura que definimos antes.
        // Para simplificar y usar la lógica robusta de 'syncUser' (header),
        // aquí usamos createTemporaryUser que ya arreglamos.
        const newGuestUser = await createTemporaryUser();
        
        if (!newGuestUser || !newGuestUser.id) throw new Error("Fallo al crear invitado");
        userId = newGuestUser.id;
        localStorage.setItem('guestUserId', userId.toString());
      }

      setCurrentUserId(userId);
      setIsUserCart(false);

      // Gestión del Carrito Invitado
      let cartId: number;
      const storedCartId = localStorage.getItem('guestCartId');

      if (storedCartId) {
        try {
          const existingCart = await cartService.getCartById(parseInt(storedCartId, 10));
          if (existingCart.userId === userId) {
            cartId = existingCart.id;
          } else {
            const newCart = await cartService.createUserCart(userId);
            cartId = newCart.id;
          }
        } catch {
          const newCart = await cartService.createUserCart(userId);
          cartId = newCart.id;
        }
      } else {
        const newCart = await cartService.createUserCart(userId);
        cartId = newCart.id;
      }

      localStorage.setItem('guestCartId', cartId.toString());
      setCurrentCartId(cartId);

    } catch (err: any) {
      console.error('Error sesión invitado:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- LÓGICA 2: USUARIO LOGUEADO (Fusión) ---
  const initializeUserSession = async () => {
    setLoading(true);
    try {
      // 1. Obtener el ID real de base de datos del usuario logueado
      // Usamos syncUser para asegurarnos de que existe en nuestra BD local
      const sub = keycloak.subject;
      if (!sub) throw new Error("No subject found in token");

      const realUser = await syncUser(sub);
      const realUserId = realUser.id;

      setCurrentUserId(realUserId);
      setIsUserCart(true);

      // 2. Verificar si hay un carrito de invitado pendiente de fusionar
      const guestCartIdStr = localStorage.getItem('guestCartId');

      if (guestCartIdStr) {
        const guestCartId = parseInt(guestCartIdStr, 10);
        console.log(`Fusionando carrito invitado ${guestCartId} al usuario ${realUserId}...`);
        
        try {
          // ¡AQUÍ OCURRE LA MAGIA! Asignamos el carrito al usuario real
          await cartService.updateCart(guestCartId, realUserId);
          
          // Fusión exitosa: usamos este carrito
          setCurrentCartId(guestCartId);
          
          // Limpiamos rastros del invitado
          localStorage.removeItem('guestUserId');
          localStorage.removeItem('guestCartId');
          
        } catch (err) {
          console.error("Error al fusionar carrito, buscando carrito existente del usuario...", err);
          // Si falla la fusión (ej. conflicto), intentamos buscar un carrito existente del usuario
          // Esto requeriría un endpoint tipo getCartByUserId, o crear uno nuevo.
          // Por simplicidad, creamos uno nuevo si falla la fusión.
          const newCart = await cartService.createUserCart(realUserId);
          setCurrentCartId(newCart.id);
        }
      } else {
        // 3. No hay carrito invitado. ¿El usuario ya tiene uno?
        // Como tu API actual no tiene "getCartByUser", asumimos crear uno nuevo o 
        // idealmente el backend debería manejar "obtener mi carrito activo".
        // Por ahora, creamos uno nuevo asociado a su ID.
        // (Nota: En un futuro, tu backend debería devolver el carrito activo si existe)
        const newCart = await cartService.createUserCart(realUserId);
        setCurrentCartId(newCart.id);
      }

    } catch (err: any) {
      console.error('Error sesión usuario:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };


  // --- EFECTO PRINCIPAL: DECIDE QUÉ CAMINO TOMAR ---
  useEffect(() => {
    if (!initialized) return;

    if (keycloak.authenticated) {
      // Camino A: Usuario Registrado
      initializeUserSession();
    } else {
      // Camino B: Usuario Invitado
      initializeGuestSession();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [initialized, keycloak.authenticated]);


  return {
    cartId: currentCartId,
    userId: currentUserId,
    isUserCart,
    loading,
    error,
    // Exponemos una función para forzar recarga si es necesario
    refreshCart: keycloak.authenticated ? initializeUserSession : initializeGuestSession
  };
};