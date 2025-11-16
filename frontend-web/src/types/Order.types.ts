export interface CheckoutRequestDto {
  cartId: number;
  userId: number;
  addressId: number;
}

export interface CheckoutResponseDto {
  checkoutUrl: string;
  orderId: string;
}

export interface updateOrderStatus {
  status: string;
}