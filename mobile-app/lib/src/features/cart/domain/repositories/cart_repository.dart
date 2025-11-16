// lib/src/features/cart/domain/repositories/cart_repository.dart

import 'package:springshop/src/features/cart/data/models/cart_dto.dart';

abstract class CartRepository {
  Future<CartResponseDto> getOrCreateCart(int userId);
  Future<CartItemResponseDto> addItem(int cartId, CartItemCreateRequestDto item);
  Future<void> updateItemQuantity(int cartId, int itemId, int quantity);
  Future<void> removeItem(int cartId, int itemId);
  Future<void> clearCart(int cartId);
  Future<CartResponseDto> getCartById(int cartId);
}