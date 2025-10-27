import type { CartItem } from "../context/CartContext";
import type { Product } from "../types/Product";

const USER_CART_ID = 2;
const API_BASE_URL = `http://localhost:8085/api/carts/${USER_CART_ID}/items`;

interface AddItemRequest {
  productId: number;
  quantity: number;
}

interface UpdateItemRequest {
  quantity: number;
}

export async function getCartItems(): Promise<CartItem[]> {
  const response = await fetch(API_BASE_URL);
  if (!response.ok) {
    throw new Error("Error al obtener los items del carrito");
  }
  return response.json();
}

export async function addItemToCart(
  productId: number,
  quantity: number = 1
): Promise<CartItem> {
  const payload: AddItemRequest = { productId, quantity };
  const response = await fetch(API_BASE_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });
  if (!response.ok) {
    throw new Error("Error al añadir item al carrito");
  }
  return response.json();
}

export async function updateCartItemQuantity(
  cartItemId: number,
  productId: number,
  quantity: number
): Promise<CartItem> {
  const response = await fetch(`${API_BASE_URL}/${cartItemId}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ productId, quantity }),
  });

  if (!response.ok) {
    throw new Error("Error al actualizar cantidad del item");
  }

  return response.json();
}

export async function removeCartItem(cartItemId: number): Promise<void> {
  const response = await fetch(`${API_BASE_URL}/${cartItemId}`, {
    method: "DELETE",
  });
  if (!response.ok) {
    throw new Error("Error al eliminar item del carrito");
  }
}
