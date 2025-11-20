
import 'package:springshop/src/features/shipment/data/models/shipment_dto.dart';

/// Contrato que define la lógica de negocio para gestionar los envíos (Shipments).
///
/// La implementación de esta interfaz será consumida por los blocs, providers o
/// controllers de la capa de presentación.
abstract class ShipmentService { // 🔑 Renombrado a ShipmentService
  /// Obtiene un envío específico usando su identificador único.
  ///
  /// Es una abstracción directa sobre el repositorio.
  Future<ShipmentResponseDto> getShipmentById(int shipmentId);

  /// Obtiene el último envío asociado a un ID de orden específico.
  ///
  /// Método principal para la funcionalidad de seguimiento de envío.
  Future<ShipmentResponseDto> getLatestShipmentByOrderId(int orderId);
}