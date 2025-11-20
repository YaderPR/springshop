
import 'package:springshop/src/features/shipment/data/models/shipment_dto.dart';
import 'package:springshop/src/features/shipment/domain/repositories/shipment_service.dart';
import 'package:springshop/src/features/shipment/domain/services/shipment_repository.dart';

/// Implementación del servicio de envíos que utiliza el repositorio para acceder
/// a los datos.
///
/// Implementa el contrato [ShipmentService] y encapsula la lógica de negocio.
class ShipmentApiService implements ShipmentService {
  final ShipmentRepository _shipmentRepository;

  // Inyección de dependencia de la interfaz del repositorio
  ShipmentApiService(this._shipmentRepository);

  // ====================================================================
  // IMPLEMENTACIÓN DE getShipmentById
  // ====================================================================

  /// 🎯 Delega la obtención del envío por ID al repositorio.
  @override
  Future<ShipmentResponseDto> getShipmentById(int shipmentId) async {
    print('📦 [ShipmentApiService] Solicitando envío con ID: $shipmentId');
    try {
      // Aquí podrías añadir lógica de negocio (ej: caché, validaciones, etc.)
      final shipment = await _shipmentRepository.getShipmentById(shipmentId);
      print('✅ [ShipmentApiService] Envío $shipmentId obtenido.');
      return shipment;
    } catch (e) {
      print('❌ [ShipmentApiService] Error al obtener envío $shipmentId: $e');
      // Re-lanzar la excepción para que la capa de presentación la maneje.
      rethrow;
    }
  }

  // ====================================================================
  // IMPLEMENTACIÓN DE getLatestShipmentByOrderId
  // ====================================================================

  /// 🎯 Delega la obtención del último envío por Order ID al repositorio.
  @override
  Future<ShipmentResponseDto> getLatestShipmentByOrderId(int orderId) async {
    print('📦 [ShipmentApiService] Solicitando último envío para OrderId: $orderId');
    try {
      // Aquí podrías añadir lógica de negocio (ej: verificar permisos)
      final shipment = await _shipmentRepository.getLatestShipmentByOrderId(orderId);
      print('✅ [ShipmentApiService] Último envío para OrderId $orderId obtenido.');
      return shipment;
    } catch (e) {
      print('❌ [ShipmentApiService] Error al obtener último envío para OrderId $orderId: $e');
      rethrow;
    }
  }
}