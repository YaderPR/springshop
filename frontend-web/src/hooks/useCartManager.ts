import { useState, useEffect } from 'react';
import { cartService } from '../services/cart/CartService';
import { createTemporaryUser, syncUser } from '../services/user/UserService'; 
import { useKeycloak } from '@react-keycloak/web'; 
// Asumimos que los tipos y servicios restantes son correctos

export const useCartManager = () => {
  const { keycloak, initialized } = useKeycloak();
  
  const [currentCartId, setCurrentCartId] = useState<number | null>(null);
  const [currentUserId, setCurrentUserId] = useState<number | null>(currentCartId);
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
        // 1. Usar ID Invitado existente
        userId = parseInt(storedGuestId, 10);
      } else {
        // 2. Crear nuevo usuario invitado
        const newGuestUser = await createTemporaryUser();
        if (!newGuestUser || !newGuestUser.id) throw new Error("Fallo al crear invitado");
        userId = newGuestUser.id;
        localStorage.setItem('guestUserId', userId.toString());
      }

      setCurrentUserId(userId);
      setIsUserCart(false);

      // 3. Inicializar o Reutilizar el Carrito
      // Asumimos que initializeCart maneja obtener un carrito existente (si lo hay) o crear uno nuevo.
      // También le pasamos el cartId almacenado localmente para que la API lo reutilice/valide.
      const storedCartId = localStorage.getItem('guestCartId');
      const cart = await cartService.initializeCart(userId);
      
      localStorage.setItem('guestCartId', cart.id.toString()); // Asegurar que el ID válido esté guardado
      setCurrentCartId(cart.id);

    } catch (err: any) {
      console.error('Error sesión invitado:', err);
      setError(err.message || 'Error al iniciar sesión de invitado');
    } finally {
      setLoading(false);
    }
  };

  // --- LÓGICA 2: USUARIO LOGUEADO (Fusión) ---
  const initializeUserSession = async () => {
    setLoading(true);
    try {
      const sub = keycloak.subject;
      if (!sub) throw new Error("No subject found in token");

      // 1. Obtener el ID real de base de datos y establecer el usuario
      const realUser = await syncUser();
      const realUserId = realUser.id;

      setCurrentUserId(realUserId);
      setIsUserCart(true);

      // 2. Determinar si hay un carrito para fusionar/obtener
      let cartIdToUse: number;
      const guestCartIdStr = localStorage.getItem('guestCartId');

      if (guestCartIdStr) {
        // Caso A: Hay carrito invitado. Asignarlo al usuario real.
        const guestCartId = parseInt(guestCartIdStr, 10);
        await cartService.updateCart(guestCartId, realUserId); // Asignación/Fusión
        cartIdToUse = guestCartId;
        
        // Limpiamos rastros del invitado
        localStorage.removeItem('guestUserId');
        localStorage.removeItem('guestCartId');
      } else {
        // Caso B: No hay carrito invitado. Obtener el carrito activo del usuario (o crear uno).
        // initializeCart se encarga de no crear duplicados y devolver el activo.
        const activeCart = await cartService.initializeCart(realUserId);
        cartIdToUse = activeCart.id;
      }

      setCurrentCartId(cartIdToUse);

    } catch (err: any) {
      console.error('Error sesión usuario:', err);
      setError(err.message || 'Error al iniciar sesión de usuario');
    } finally {
      setLoading(false);
    }
  };


  // --- EFECTO PRINCIPAL ---
  useEffect(() => {
    // Limpiamos el estado al inicio de la ejecución del efecto para evitar visualización incorrecta
    setCurrentCartId(null);
    setCurrentUserId(null);
    setLoading(true);

    if (!initialized) return;

    if (keycloak.authenticated) {
      initializeUserSession();
    } else {
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
    refreshCart: keycloak.authenticated ? initializeUserSession : initializeGuestSession
  };
};