
export interface OrderItemResponseDto {
  id: number;
  productId: number;
  quantity: number;
  price: number;
}

// La Orden completa
export interface OrderResponseDto {
  id: number;
  status: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED'; 
  totalAmount: number;
  userId: number;
  items: OrderItemResponseDto[]; 
  addressId: number;
  createAt: string; // Formato date-time
  updateAt?: string; // Puede ser opcional si no se ha actualizado
}

// Datos para iniciar el Checkout
export interface CheckoutRequestDto {
  cartId: number;
  userId: number;
  addressId: number;
  redirectUrl: string; // Agregado según api-docs
}

// Respuesta del Checkout (La API devuelve un Map<String, String>, asumimos esta estructura)
export interface CheckoutResponseDto {
  checkoutUrl?: string; // Opcional porque es un Map dinámico
  orderId?: string;
  [key: string]: string | undefined; // Firma de índice por si el API devuelve otras claves
}

// DTO para actualizar el estado
export interface UpdateOrderStatus {
  status: string;
}