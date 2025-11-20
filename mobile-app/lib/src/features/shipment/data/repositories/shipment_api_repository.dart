import 'package:dio/dio.dart';
import 'package:springshop/src/features/shipment/data/models/shipment_dto.dart';
import 'package:springshop/src/features/shipment/domain/services/shipment_repository.dart'; // Asumiendo que has renombrado la interfaz a ShipmentRepository

/// Implementación del repositorio de envíos que interactúa con la API REST.
///
/// Implementa el contrato [ShipmentRepository] usando la librería [Dio].
class ShipmentApiRepository implements ShipmentRepository {
  final Dio _dio;
  // Basado en el path de la especificación, el path base es /api/v2/shipments
  static const String _basePath = '/shipments'; 

  ShipmentApiRepository(this._dio);

  // ====================================================================
  // IMPLEMENTACIÓN DE getShipmentById
  // Endpoint: GET /api/v2/shipments/{id}
  // ====================================================================

  /// 🎯 Obtiene un envío específico usando su identificador único.
  @override
  Future<ShipmentResponseDto> getShipmentById(int shipmentId) async {
    print('🔍 [ShipmentApiRepository] Buscando envío con ID: $shipmentId');
    try {
      // Llama a GET /api/v2/shipments/{id}
      final response = await _dio.get('$_basePath/$shipmentId');
      
      // Mapea la respuesta JSON al DTO
      return ShipmentResponseDto.fromJson(response.data);
    } on DioException catch (e) {
      // Manejo de errores específico de Dio (ej: 404, 500)
      print('❌ [ShipmentApiRepository] Error al obtener envío por ID: $e');
      rethrow;
    }
  }

  // ====================================================================
  // IMPLEMENTACIÓN DE getLatestShipmentByOrderId
  // Endpoint: GET /api/v2/shipments/orders/{orderId}/latest
  // ====================================================================

  /// 🎯 Obtiene el último envío asociado a un ID de orden.
  @override
  Future<ShipmentResponseDto> getLatestShipmentByOrderId(int orderId) async {
    print('🚚 [ShipmentApiRepository] Buscando último envío para OrderId: $orderId');
    try {
      // Llama a GET /api/v2/shipments/orders/{orderId}/latest
      final response = await _dio.get('$_basePath/orders/$orderId/latest');

      // Mapea la respuesta JSON al DTO
      return ShipmentResponseDto.fromJson(response.data);
    } on DioException catch (e) {
      // Manejo de errores específico de Dio
      print('❌ [ShipmentApiRepository] Error al obtener último envío por OrderId: $e');
      rethrow;
    }
  }
}