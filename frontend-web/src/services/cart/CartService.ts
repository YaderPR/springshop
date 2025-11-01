  import axios from 'axios';
  import type {
    CartRequestDto,
    CartItemCreateRequestDto,
    CartItemUpdateRequestDto,
    CartResponseDto,
    CartItemResponseDto
  } from '../../types/cart.types';

  const CART_API_BASE_URL = 'http://localhost:8086/api/v2/carts';

  class CartService {
    // ========== CARRITOS ANÓNIMOS ==========
    
    async createAnonymousCart(): Promise<CartResponseDto> {
      const { data } = await axios.post<CartResponseDto>(CART_API_BASE_URL, { userId: 0 });
      return data;
    }

    async createUserCart(userId: number): Promise<CartResponseDto> {
      const { data } = await axios.post<CartResponseDto>(CART_API_BASE_URL, { userId });
      return data;
    }

    // ========== OPERACIONES DEL CARRITO ==========

    async getCartById(cartId: number): Promise<CartResponseDto> {
      const { data } = await axios.get<CartResponseDto>(`${CART_API_BASE_URL}/${cartId}`);
      return data;
    }

    async getAllCarts(): Promise<CartResponseDto[]> {
      const { data } = await axios.get<CartResponseDto[]>(CART_API_BASE_URL);
      return data;
    }

    async updateCart(cartId: number, userId: number): Promise<CartResponseDto> {
      const { data } = await axios.put<CartResponseDto>(`${CART_API_BASE_URL}/${cartId}`, { userId }); //se esta enviando el userId con el valor de 3 para editar
      return data;
    }

    async deleteCart(cartId: number): Promise<void> {
      await axios.delete(`${CART_API_BASE_URL}/${cartId}`);
    }

    async clearCart(cartId: number): Promise<void> {
      await axios.delete(`${CART_API_BASE_URL}/${cartId}/items`);
    }

    // ========== OPERACIONES DE ITEMS DEL CARRITO ==========

    async getCartItems(cartId: number): Promise<CartItemResponseDto[]> {
      const { data } = await axios.get<CartItemResponseDto[]>(`${CART_API_BASE_URL}/${cartId}/items`);
      return data;
    }

    async addItemToCart(cartId: number, item: CartItemCreateRequestDto): Promise<CartItemResponseDto> {
      console.log(cartId, item);
      const { data } = await axios.post<CartItemResponseDto>(
        `${CART_API_BASE_URL}/${cartId}/items`,
        item
      );
      return data;
    }

    async updateCartItem(
      cartId: number,
      itemId: number,
      item: CartItemUpdateRequestDto
    ): Promise<CartItemResponseDto> {
      const { data } = await axios.put<CartItemResponseDto>(
        `${CART_API_BASE_URL}/${cartId}/items/${itemId}`,
        item
      );
      return data;
    }

    async removeItemFromCart(cartId: number, itemId: number): Promise<void> {
      await axios.delete(`${CART_API_BASE_URL}/${cartId}/items/${itemId}`);
    }

    async getCartTotal(cartId: number): Promise<number> {
      const { data } = await axios.get<number>(`${CART_API_BASE_URL}/${cartId}/items/total`);
      return data;
    }
  };

  export const cartService = new CartService();