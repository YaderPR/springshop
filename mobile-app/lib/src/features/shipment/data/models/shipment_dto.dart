import 'package:flutter/foundation.dart';

// ====================================================================
// ENUM: ShipmentStatus
// Representa los estados posibles de un envío, como se define en la API.
// ====================================================================

/// Enum que mapea los posibles estados del envío desde el backend.
enum ShipmentStatus {
  /// Envío creado, pendiente de ser empacado.
  created('CREATED', 'Confirmada'),
  /// El pedido ha sido empacado y está listo para ser recogido por el transportista.
  shipped('SHIPPED', 'En Preparación'),
  /// El envío está en movimiento hacia el destino.
  inTransit('IN_TRANSIT', 'En Ruta'),
  /// El envío ha sido entregado al cliente.
  delivered('DELIVERED', 'Entregada'),
  /// El envío ha sido devuelto o rechazado.
  returned('RETURNED', 'Devuelta');

  /// Valor exacto del String que viene de la API.
  final String apiValue;
  /// Descripción en español para mostrar al usuario.
  final String displayValue;

  const ShipmentStatus(this.apiValue, this.displayValue);
  
  // Método auxiliar para obtener el enum a partir del string de la API
  static ShipmentStatus fromApiValue(String apiValue) {
    return ShipmentStatus.values.firstWhere(
      (e) => e.apiValue == apiValue.toUpperCase(),
      // Caso por defecto si el estado no se encuentra, útil para manejo de errores
      orElse: () {
        debugPrint('Advertencia: Estado de envío desconocido recibido: $apiValue. Usando CREATED por defecto.');
        return ShipmentStatus.created;
      },
    );
  }
}

// ====================================================================
// DTO: ShipmentResponseDto
// Modelo de datos para la respuesta del detalle de un envío.
// ====================================================================

class ShipmentResponseDto {
  final int id;
  final String trackingNumber;
  final String carrier;
  final ShipmentStatus status; // Usamos el enum tipado
  final DateTime? shippedAt;
  final DateTime? deliveredAt;
  final int orderId;

  ShipmentResponseDto({
    required this.id,
    required this.trackingNumber,
    required this.carrier,
    required this.status,
    required this.orderId,
    this.shippedAt,
    this.deliveredAt,
  });

  // Factory constructor para mapear desde JSON (API Response)
  factory ShipmentResponseDto.fromJson(Map<String, dynamic> json) {
    // Helper para parsear fechas o devolver null si la clave no existe o es nula
    DateTime? parseDate(String? dateString) {
      if (dateString == null) return null;
      try {
        return DateTime.parse(dateString);
      } catch (e) {
        debugPrint('Error al parsear la fecha: $dateString. Error: $e');
        return null;
      }
    }

    return ShipmentResponseDto(
      id: json['id'] as int,
      trackingNumber: json['trackingNumber'] as String,
      carrier: json['carrier'] as String,
      // Mapeamos el string de la API al enum tipado
      status: ShipmentStatus.fromApiValue(json['status'] as String), 
      orderId: json['orderId'] as int,
      shippedAt: parseDate(json['shippedAt'] as String?),
      deliveredAt: parseDate(json['deliveredAt'] as String?),
    );
  }

  // Método auxiliar para imprimir
  @override
  String toString() {
    return 'Shipment(ID: $id, Tracking: $trackingNumber, Status: ${status.displayValue})';
  }
}