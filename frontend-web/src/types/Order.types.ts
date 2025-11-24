export interface CheckoutRequestDto {
  cartId: number;
  userId: number;
  addressId: number;
}

export interface OrderResponseDto {
  id: number;
  quantity: number;
  price: number;
  cartId: number;
  productId: number;
}

export interface CheckoutResponseDto {
  checkoutUrl: string;
  orderId: string;
}

export interface updateOrderStatus {
  status: string;
}
