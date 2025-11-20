import 'package:springshop/src/features/shipment/data/models/shipment_dto.dart';

/// Contrato que define las operaciones de acceso a datos para los envíos (Shipments).
///
/// La implementación de esta interfaz será responsable de comunicarse con la
/// fuente de datos (e.g., una API REST o una base de datos local).
abstract class ShipmentRepository {
  /// Obtiene un envío específico usando su identificador único.
  ///
  /// Retorna un [ShipmentResponseDto] si se encuentra, o lanza una excepción.
  Future<ShipmentResponseDto> getShipmentById(int shipmentId);

  /// Obtiene el último (más reciente) envío asociado a un ID de orden específico.
  ///
  /// Esta es la función clave para la pantalla de seguimiento de órdenes.
  /// Retorna un [ShipmentResponseDto] si se encuentra, o lanza una excepción.
  Future<ShipmentResponseDto> getLatestShipmentByOrderId(int orderId);
}