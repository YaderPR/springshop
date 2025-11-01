export interface Cart {
  id: number;
  totalAmount: number;
  userId: number;
  items: CartItem[];
  createAt?: string;
  updateAt?: string;
}

export interface CartItem {
  id: number;
  quantity: number;
  price: number;
  cartId: number;
  productId: number;
  product?: Product; // Para información adicional del producto
}

// Usamos el Product que ya definiste
export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  color?: string;
  brand?: string;
  size?: string;
  categoryId?: number;
  imageUrl?: string;
}

export interface User {
  id: number;
  sub: string;
  username: string;
  profile?: UserProfile;
}

export interface UserProfile {
  id: number;
  firstName: string;
  lastName: string;
  profilePicUrl: string;
}

// DTOs para requests
export interface CartRequestDto {
  userId: number;
}

export interface CartItemCreateRequestDto {
  productId: number;
  quantity: number;
}

export interface CartItemUpdateRequestDto {
  productId: number;
  quantity: number;
}

// DTOs para responses
export interface CartResponseDto {
  id: number;
  totalAmount: number;
  userId: number;
  createAt?: string;
  updateAt?: string;
}

export interface CartItemResponseDto {
  id: number;
  quantity: number;
  price: number;
  productId: number;
  cartId: number;
}