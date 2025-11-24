import { useState, useEffect } from 'react';

import { getProductById } from '../services/product/ProductService'; 
import type { Product } from '../types/Product';
import type { CartItemResponseDto, CartItemCreateRequestDto, CartItemUpdateRequestDto } from '../types/cart.types';
import { cartService } from '../services/cart/CartService';


// (Esto es lo que tu CartDrawer espera)
export interface FullCartItem extends CartItemResponseDto {
  product: Product; 
}

export const useCart = (cartId?: number) => {
  // ?? 3. CAMBIA EL TIPO DE ESTADO
  const [cartItems, setCartItems] = useState<FullCartItem[]>([]);
  const [total, setTotal] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  // ?? 4. REESCRIBE loadCartItems PARA FUSIONAR
  const loadCartItems = async (id: number) => {
    if (!id) return;

    setLoading(true);
    setError(null);
    try {
      // a. Obtener items básicos y total
      const [basicItems, cartTotal] = await Promise.all([
        cartService.getCartItems(id),
        cartService.getCartTotal(id)
      ]);
      
      setTotal(cartTotal);
      
      if (basicItems.length === 0) {
         setCartItems([]); // Carrito vacío
         setLoading(false);
         return;
      }

      // b. Obtener detalles de los productos (en paralelo)
      const productDetailsPromises = basicItems.map(item => 
        getProductById(item.productId).catch(err => {
          console.error(`Fallo al cargar producto ${item.productId}:`, err);
          return null; // Si un producto falla, no romper todo
        })
      );
      const productDetails = (await Promise.all(productDetailsPromises)).filter(p => p !== null) as Product[];
      
      // c. Mapear para búsqueda fácil
      const productMap = new Map<number, Product>();
      productDetails.forEach(p => productMap.set(p.id, p));

      // d. Fusionar datos
      const mergedItems: FullCartItem[] = basicItems.map(item => {
        const product = productMap.get(item.productId);
        return {
          ...item, // (id, cartId, productId, quantity, price)
          product: product || { // Fallback por si getProductById falló
            id: item.productId,
            name: "Producto no encontrado",
            price: item.price, // Usa el precio del carrito como fallback
            imageUrl: "/placeholder.png" // Imagen de placeholder
            // ... (otros campos de Product con valores por defecto)
          } as Product
        };
      });

      setCartItems(mergedItems); // <-- Guardar items fusionados

    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al cargar el carrito');
      console.error('Error loading cart:', err);
    } finally {
      setLoading(false);
    }
  };

  // 5. addItem, updateItem, removeItem, clearCart
  //    ¡NO NECESITAN CAMBIOS!
  //    Todos llaman a loadCartItems() al final,
  //    que ahora se encarga de la fusión automáticamente.

  const addItem = async (item: CartItemCreateRequestDto) => {
    if (!cartId) return;
    setLoading(true);
    setError(null);
    try {
      const newItem = await cartService.addItemToCart(cartId, item);
      await loadCartItems(cartId); // Recargará y fusionará
      return newItem;
    } catch (err: any) {
      // ... (manejo de errores)
      const errorMessage = err.response?.data?.message || 'Error al agregar item al carrito';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const updateItem = async (itemId: number, item: CartItemUpdateRequestDto) => {
    if (!cartId) return;
    setLoading(true);
    setError(null);
    try {
      const updatedItem = await cartService.updateCartItem(cartId, itemId, item);
      await loadCartItems(cartId); // Recargará y fusionará
      return updatedItem;
    } catch (err: any) {
      // ... (manejo de errores)
      const errorMessage = err.response?.data?.message || 'Error al actualizar item';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const removeItem = async (itemId: number) => {
    if (!cartId) return;
    setLoading(true);
    setError(null);
    try {
      await cartService.removeItemFromCart(cartId, itemId);
      await loadCartItems(cartId); // Recargará y fusionará
    } catch (err: any) {
      // ... (manejo de errores)
      const errorMessage = err.response?.data?.message || 'Error al remover item';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const clearCart = async () => {
    if (!cartId) return;
    setLoading(true);
    setError(null);
    try {
      await cartService.clearCart(cartId);
      setCartItems([]);
      setTotal(0);
    } catch (err: any) {
      // ... (manejo de errores)
      const errorMessage = err.response?.data?.message || 'Error al limpiar carrito';
      setError(errorMessage);
      throw new Error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  // Efecto (sin cambios)
  useEffect(() => {
    if (cartId) {
      loadCartItems(cartId);
    } else {
      // Si no hay cartId (ej. al inicio), limpia el estado
      setCartItems([]);
      setTotal(0);
      setLoading(false);
    }
  }, [cartId]);

  return {
    cartItems,
    total,
    loading,
    error,
    addItem,
    updateItem,
    removeItem,
    clearCart,
    refreshCart: () => cartId ? loadCartItems(cartId) : Promise.resolve()
  };
};