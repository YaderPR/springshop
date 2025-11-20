import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/core/auth/auth_state_notifier.dart'; // Para obtener el userId
import 'package:springshop/src/features/order/domain/services/order_service.dart';
import 'package:springshop/src/features/order/data/models/order_dto.dart';
import 'package:springshop/src/features/order/presentation/screens/order_details_screen.dart'; // OrderResponseDto

/// Pantalla que muestra la lista de órdenes históricas del usuario.
class OrderHistoryScreen extends StatelessWidget {
  const OrderHistoryScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // 1. Obtener el userId para la llamada al servicio
    final authNotifier = context.read<AuthStateNotifier>();
    final userId = authNotifier.user?.id;
    final orderService = context.read<OrderService>();
    final colorScheme = Theme.of(
      context,
    ).colorScheme; // Obtenemos el esquema de color

    // Control de seguridad: aunque la navegación lo filtra, es bueno verificar
    if (userId == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Historial de Compras')),
        body: const Center(
          child: Text('Error de Sesión: Usuario no identificado.'),
        ),
      );
    }

    // 2. Ejecutar el Future para obtener las órdenes
    final ordersFuture = orderService.fetchOrdersByUserId(
      int.tryParse(userId) ?? 0,
    );

    // 💡 SOLUCIÓN SIMPLE: Usamos el AppBar de Flutter para controlar la barra de estado automáticamente.
    // Al usar systemOverlayStyle, le indicamos a Flutter qué colores de fondo e iconos queremos.
    return Scaffold(
      appBar: AppBar(
        title: const Text('Historial de Compras'),
      ),
      body: FutureBuilder<List<OrderResponseDto>>(
        future: ordersFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (snapshot.hasError) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Icon(Icons.warning, color: Colors.red, size: 40),
                    const SizedBox(height: 10),
                    Text(
                      'Error al cargar historial: ${snapshot.error}',
                      textAlign: TextAlign.center,
                    ),
                    // Nota: Para reintentar se necesitaría envolver en un StatefulWidget.
                  ],
                ),
              ),
            );
          }

          // 3. Manejo de lista vacía
          if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.receipt_long, size: 50, color: Colors.grey),
                  SizedBox(height: 10),
                  Text(
                    'Aún no tienes órdenes registradas.',
                    style: TextStyle(color: Colors.grey),
                  ),
                ],
              ),
            );
          }

          // 4. Mostrar la lista de órdenes
          final orders = snapshot.data!;
          return ListView.builder(
            padding: const EdgeInsets.all(8.0),
            itemCount: orders.length,
            itemBuilder: (context, index) {
              final order = orders[index];
              return OrderHistoryItem(order: order);
            },
          );
        },
      ),
    );
  }
}

/// Widget para visualizar un ítem de la orden en el historial (card clickable).
class OrderHistoryItem extends StatelessWidget {
  final OrderResponseDto order;

  const OrderHistoryItem({super.key, required this.order});

  @override
  Widget build(BuildContext context) {
    // Determinar color de estado (simple)
    Color statusColor;
    switch (order.status.toLowerCase()) {
      case 'completada':
      case 'pagada':
        statusColor = Colors.green;
        break;
      case 'pendiente':
        statusColor = Colors.orange;
        break;
      case 'cancelada':
        statusColor = Colors.red;
        break;
      default:
        statusColor = Colors.blueGrey;
    }

    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      margin: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 4.0),
      child: ListTile(
        leading: Icon(Icons.shopping_bag, color: statusColor),
        title: Text(
          'Orden #${order.id}',
          style: Theme.of(
            context,
          ).textTheme.titleMedium?.copyWith(fontWeight: FontWeight.bold),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Total: \$${order.totalAmount.toStringAsFixed(2)}'),
            Text(
              'Estado: ${order.status}',
              style: TextStyle(color: statusColor, fontWeight: FontWeight.w500),
            ),
          ],
        ),
        trailing: const Icon(Icons.arrow_forward_ios, size: 16),
        onTap: () {
          // TODO: Implementar la navegación a OrderDetailsScreen para ver el detalle de la orden.
          Navigator.of(context).push(
            MaterialPageRoute(
              builder: (_) => OrderDetailsScreen(orderId: order.id),
            ),
          );
          debugPrint('Navegando a detalles de la Orden ${order.id}');
        },
      ),
    );
  }
}
