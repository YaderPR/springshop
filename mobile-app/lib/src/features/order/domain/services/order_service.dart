// src/features/order/domain/services/order_service.dart

import 'package:springshop/src/features/order/data/models/order_dto.dart';

abstract class OrderService {
  /// Procesa el checkout de un carrito a una nueva orden.
  /// Retorna un mapa que podría contener una URL de pago o redirección.
  Future<Map<String, dynamic>> processCheckout({
    required int cartId,
    required int userId,
    required int addressId,
    required String redirectUrl,
  });

  /// Obtiene una orden existente por su ID.
  Future<OrderResponseDto> fetchOrderById(int orderId);
}