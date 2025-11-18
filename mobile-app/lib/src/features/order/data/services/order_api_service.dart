// src/features/order/data/services/order_service_impl.dart (Actualizado)

import 'package:springshop/src/features/order/data/models/order_dto.dart';
import 'package:springshop/src/features/order/data/models/checkout_dto.dart';
import 'package:springshop/src/features/order/domain/repositories/order_repository.dart';
import 'package:springshop/src/features/order/domain/services/order_service.dart';

class OrderApiService implements OrderService {
 final OrderRepository _orderRepository;

 // Inyección de dependencia del repositorio
 OrderApiService(this._orderRepository);

 // ====================================================================
 // IMPLEMENTACIÓN DE CHECKOUT
 // ====================================================================

 @override
 Future<Map<String, dynamic>> processCheckout({
  required int cartId,
  required int userId,
  required int addressId,
    required String redirectUrl, // 🔑 Aceptamos el nuevo parámetro
 }) async {
  print('🛒 [OrderService] Preparando datos para checkout con redirectUrl: $redirectUrl');

  // 1. Crear el DTO de la solicitud (lógica de negocio/mapeo)
  final requestDto = CheckoutRequestDto(
   cartId: cartId,
   userId: userId,
   addressId: addressId,
      redirectUrl: redirectUrl, // 🔑 Lo pasamos al DTO
  );

  try {
   // 2. Llamar al repositorio para ejecutar la llamada API
   final paymentDetails = await _orderRepository.checkout(requestDto);
   
   print('✅ [OrderService] Checkout exitoso. Detalle de pago recibido.');
   
   return paymentDetails;
  } catch (e) {
   print('❌ [OrderService] Error durante el checkout: $e');
   rethrow;
  }
 }

 // ====================================================================
 // IMPLEMENTACIÓN DE OBTENER ORDEN POR ID (sin cambios)
 // ====================================================================

 @override
 Future<OrderResponseDto> fetchOrderById(int orderId) async {
  print('🔍 [OrderService] Solicitando orden con ID: $orderId');

  try {
   // Delegar la llamada al repositorio
   final order = await _orderRepository.getOrderById(orderId);
   print('✅ [OrderService] Orden $orderId obtenida correctamente.');
   return order;
  } catch (e) {
   print('❌ [OrderService] Error al obtener la orden $orderId: $e');
   rethrow;
  }
 }
}