// domain/repositories/order_repository.dart
import 'package:springshop/src/features/order/data/models/checkout_dto.dart';
import 'package:springshop/src/features/order/data/models/order_dto.dart';

abstract class OrderRepository {
  /// 🎯 Realiza el proceso de checkout para un carrito.
  /// Retorna un mapa de strings con la información de la URL de pago.
  Future<Map<String, dynamic>> checkout(CheckoutRequestDto request);

  /// 🎯 Obtiene una orden por su ID.
  Future<OrderResponseDto> getOrderById(int orderId);

  /// Obtiene las órdenes de un usuario por su ID.
  Future<List<OrderResponseDto>> getOrdersByUserId(int userId);
}