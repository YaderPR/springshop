import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:dio/dio.dart';
import 'package:springshop/src/features/shipment/data/models/shipment_dto.dart';
import 'package:springshop/src/features/shipment/domain/repositories/shipment_service.dart';


/// Excepción personalizada para manejar el caso de Envío no encontrado (404).
class ShipmentNotFoundException implements Exception {
  final int orderId;
  const ShipmentNotFoundException(this.orderId);

  @override
  String toString() => 'ShipmentNotFoundException: No se encontró envío para la Orden #$orderId.';
}

/// Pantalla que rastrea el último envío asociado a un OrderId.
class ShipmentTrackerScreen extends StatelessWidget {
  final int orderId;
  
  const ShipmentTrackerScreen({required this.orderId, super.key});

  /// Función que llama al servicio y maneja la lógica de error,
  /// mapeando el 404 a una excepción amigable.
  Future<ShipmentResponseDto> _fetchShipment(BuildContext context) async {
    final service = context.read<ShipmentService>();
    try {
      // Llama al método para obtener el último envío para esta orden.
      return await service.getLatestShipmentByOrderId(orderId);
    } on DioException catch (e) {
      // Manejo específico del 404: No hay un envío disponible/creado para la orden.
      if (e.response?.statusCode == 404) {
        throw ShipmentNotFoundException(orderId);
      }
      // Re-lanzar cualquier otro error (500, problemas de red, etc.).
      rethrow;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Seguimiento de Envío'),
      ),
      body: FutureBuilder<ShipmentResponseDto>(
        future: _fetchShipment(context),
        builder: (context, snapshot) {
          // 1. ESTADO DE CARGA
          if (snapshot.connectionState == ConnectionState.waiting) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const CircularProgressIndicator(),
                  const SizedBox(height: 16),
                  Text('Buscando estado del envío para Orden #${orderId}...'),
                ],
              ),
            );
          }

          // 2. ESTADO DE ERROR (Incluyendo 404 amigable)
          if (snapshot.hasError) {
            final error = snapshot.error;

            if (error is ShipmentNotFoundException) {
              // Manejo del 404: Mensaje amigable y "bonito"
              return _buildFallbackStatus(context, error.orderId);
            }

            // Manejo de otros errores (500, desconexión, etc.)
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(32.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(Icons.error_outline, color: Colors.red, size: 60),
                    const SizedBox(height: 16),
                    const Text(
                      'Ocurrió un error inesperado al rastrear el envío.',
                      textAlign: TextAlign.center,
                      style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
                    ),
                    const SizedBox(height: 8),
                    Text(
                      'Detalle: ${error.toString().replaceAll("Exception:", "")}',
                      textAlign: TextAlign.center,
                      style: TextStyle(color: Colors.grey.shade600),
                    ),
                  ],
                ),
              ),
            );
          }

          // 3. ESTADO EXITOSO
          final shipment = snapshot.data!;
          return _buildShipmentDetails(context, shipment);
        },
      ),
    );
  }

  /// Construye el widget para mostrar el estado del envío exitoso.
  Widget _buildShipmentDetails(BuildContext context, ShipmentResponseDto shipment) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(20.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeaderCard(
            context,
            title: 'Envío de Orden #${shipment.orderId}',
            subtitle: 'ID de Seguimiento: ${shipment.trackingNumber}',
            icon: Icons.local_shipping,
          ),
          const SizedBox(height: 24),
          Text(
            'Estado Actual del Envío',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: Theme.of(context).colorScheme.primary,
            ),
          ),
          const SizedBox(height: 16),
          _buildStatusCard(
            context,
            status: shipment.status.toString(),
            date: shipment.deliveredAt.toString(), // Usamos la fecha estimada
          ),
          const SizedBox(height: 24),
          const Text(
            'Detalles del Progreso',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
          ),
          const SizedBox(height: 10),
          // Simulación de una línea de tiempo para un tracking
          _buildTimeline(context, shipment.status.toString()),
        ],
      ),
    );
  }

  /// Construye la tarjeta principal con la información de la orden.
  Widget _buildHeaderCard(BuildContext context, {required String title, required String subtitle, required IconData icon}) {
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(15)),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Row(
          children: [
            Icon(icon, size: 40, color: Theme.of(context).colorScheme.secondary),
            const SizedBox(width: 15),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    subtitle,
                    style: TextStyle(color: Colors.grey.shade600),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  /// Construye la tarjeta que muestra el estado actual del envío.
  Widget _buildStatusCard(BuildContext context, {required String status, required String date}) {
    Color statusColor = Colors.orange;
    String statusDisplay = status;

    if (status.toLowerCase().contains('shipped')) {
      statusColor = Colors.green;
      statusDisplay = '¡Entregado!';
    } else if (status.toLowerCase().contains('en camino')) {
      statusColor = Colors.blue.shade600;
      statusDisplay = 'En Camino';
    } else if (status.toLowerCase().contains('procesando')) {
      statusColor = Colors.amber.shade700;
      statusDisplay = 'Procesando Envío';
    }

    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: statusColor.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: statusColor, width: 2),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            statusDisplay,
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.w900,
              color: statusColor,
            ),
          ),
          const Divider(height: 20, thickness: 1),
          Row(
            children: [
              const Icon(Icons.calendar_today, size: 18, color: Colors.grey),
              const SizedBox(width: 8),
              Text(
                'Entrega Estimada\n $date',
                style: const TextStyle(fontSize: 16),
              ),
            ],
          ),
        ],
      ),
    );
  }

  /// Muestra el estado por defecto cuando la orden no tiene un envío asociado (404).
  Widget _buildFallbackStatus(BuildContext context, int orderId) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32.0),
        child: Container(
          padding: const EdgeInsets.all(30),
          decoration: BoxDecoration(
            color: Colors.blue.shade50,
            borderRadius: BorderRadius.circular(20),
            border: Border.all(color: Colors.blue.shade300, width: 1.5),
            boxShadow: [
              BoxShadow(
                color: Colors.blue.shade100.withOpacity(0.5),
                spreadRadius: 3,
                blurRadius: 7,
                offset: const Offset(0, 3),
              ),
            ],
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                Icons.mark_email_read_outlined,
                color: Colors.blue.shade700,
                size: 80,
              ),
              const SizedBox(height: 20),
              const Text(
                'Orden en Proceso',
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  color: Colors.black87,
                ),
              ),
              const SizedBox(height: 10),
              Text(
                'La Orden #$orderId se ha recibido con éxito.',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16, color: Colors.grey.shade700),
              ),
              const SizedBox(height: 5),
              const Text(
                'Aún estamos preparando tu paquete. El número de seguimiento estará disponible pronto.',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16, color: Colors.grey),
              ),
              const SizedBox(height: 20),
              ElevatedButton.icon(
                onPressed: () => Navigator.of(context).pop(),
                icon: const Icon(Icons.arrow_back),
                label: const Text('Volver a Detalles de Orden'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Theme.of(context).colorScheme.primary,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// Simula una línea de tiempo simple para el seguimiento.
  Widget _buildTimeline(BuildContext context, String currentStatus) {
    // Definimos los pasos y si están completados
    final steps = [
      {'title': 'Orden Procesada', 'isCompleted': true},
      {'title': 'En Bodega de Origen', 'isCompleted': currentStatus.toLowerCase() != 'procesando'},
      {'title': 'En Camino', 'isCompleted': currentStatus.toLowerCase().contains('en camino') || currentStatus.toLowerCase().contains('shipped')},
      {'title': 'Entrega Finalizada', 'isCompleted': currentStatus.toLowerCase().contains('shipped')},
    ];

    return Column(
      children: steps.asMap().entries.map((entry) {
        int index = entry.key;
        Map<String, dynamic> step = entry.value;
        bool isLast = index == steps.length - 1;

        return Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Icono y línea de conexión
            Column(
              children: [
                _buildTimelineDot(context, step['isCompleted'] as bool),
                if (!isLast)
                  Container(
                    width: 2,
                    height: 50,
                    color: step['isCompleted'] as bool ? Theme.of(context).colorScheme.primary : Colors.grey.shade300,
                  ),
              ],
            ),
            const SizedBox(width: 15),
            // Título y detalle
            Padding(
              padding: EdgeInsets.only(top: 8.0, bottom: isLast ? 8.0 : 0.0),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    step['title'] as String,
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      color: step['isCompleted'] as bool ? Colors.black87 : Colors.grey.shade600,
                    ),
                  ),
                  if (step['isCompleted'] as bool)
                    Text(
                      index == 0 ? 'Hace 2 días' : index == 1 ? 'Hace 1 día' : 'Hoy',
                      style: TextStyle(fontSize: 12, color: Colors.grey.shade500),
                    ),
                ],
              ),
            ),
          ],
        );
      }).toList(),
    );
  }

  /// Construye el punto (dot) de la línea de tiempo.
  Widget _buildTimelineDot(BuildContext context, bool isCompleted) {
    return Container(
      width: 14,
      height: 14,
      decoration: BoxDecoration(
        color: isCompleted ? Theme.of(context).colorScheme.primary : Colors.white,
        shape: BoxShape.circle,
        border: Border.all(
          color: isCompleted ? Theme.of(context).colorScheme.primary : Colors.grey.shade400,
          width: 2,
        ),
      ),
      child: isCompleted
          ? Icon(
              Icons.check,
              color: Colors.white,
              size: 8,
            )
          : null,
    );
  }
}