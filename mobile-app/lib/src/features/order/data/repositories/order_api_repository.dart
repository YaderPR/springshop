import 'package:dio/dio.dart';
import 'package:springshop/src/features/order/data/models/checkout_dto.dart';
import 'package:springshop/src/features/order/data/models/order_dto.dart';
import 'package:springshop/src/features/order/domain/repositories/order_repository.dart';

class OrderApiRepository implements OrderRepository {
  final Dio _dio;
  // Basado en el 'servers' de la especificación, usamos /api/v2/orders
  static const String _basePath = '/orders'; 

  OrderApiRepository(this._dio);

  // ====================================================================
  // IMPLEMENTACIÓN DE CHECKOUT
  // Endpoint: POST /api/v2/orders/checkout
  // ====================================================================

  /// 🎯 Realiza el proceso de checkout para el carrito especificado.
  /// Retorna un mapa que podría contener una URL de redirección de pago.
  @override
  Future<Map<String, dynamic>> checkout(CheckoutRequestDto request) async {
    print('🛒 [OrderApiRepository] Iniciando checkout para CartId: ${request.cartId}');
    // Endpoint asumido: POST /api/v2/orders/checkout
    final response = await _dio.post(
      '$_basePath/checkout',
      data: request.toJson(),
    );
    // La respuesta es un objeto, devuelto como Map<String, dynamic>
    // que contendrá datos de pago (ej: una url de pago).
    return response.data; 
  }

  // ====================================================================
  // IMPLEMENTACIÓN DE OBTENER ORDEN POR ID
  // Endpoint: GET /api/v2/orders/{id}
  // ====================================================================

  /// 🎯 Obtiene los detalles de una orden por su ID.
  @override
  Future<OrderResponseDto> getOrderById(int orderId) async {
    print('🔍 [OrderApiRepository] Buscando orden con Id: $orderId');
    // Endpoint asumido: GET /api/v2/orders/{id}
    final response = await _dio.get('$_basePath/$orderId');
    return OrderResponseDto.fromJson(response.data);
  }
}