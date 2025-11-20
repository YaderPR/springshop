import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'package:springshop/src/features/order/domain/services/order_service.dart';
import 'package:springshop/src/features/order/data/models/order_dto.dart';
import 'package:springshop/src/features/shipment/presentation/screens/shipment_tracker_screen.dart'; // Tu DTO de respuesta

class OrderDetailsScreen extends StatefulWidget {
  final int orderId;

  // Recibe el orderId pasado desde el DeepLinkService
  const OrderDetailsScreen({super.key, required this.orderId});

  @override
  State<OrderDetailsScreen> createState() => _OrderDetailsScreenState();
}

class _OrderDetailsScreenState extends State<OrderDetailsScreen> {
  // Estado para la orden
  Future<OrderResponseDto>? _orderFuture;

  @override
  void initState() {
    super.initState();
    // Inicializa la carga de datos
    _fetchOrderDetails();
  }

  // Lógica para llamar al servicio y obtener la orden
  void _fetchOrderDetails() {
    // Asegúrate de usar context.read() fuera del build para acceder a Providers.
    final orderService = context.read<OrderService>();
    setState(() {
      // Llamamos al OrderService que configuraste previamente
      _orderFuture = orderService.fetchOrderById(widget.orderId);
    });
  }

  @override
  Widget build(BuildContext context) {
    // Obtener el esquema de colores del tema
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Detalles de la Orden'),
        // Limpiamos la pila de navegación después de un pago exitoso
        // para que el usuario no regrese a la pantalla de resumen del pedido.
        automaticallyImplyLeading: false,
      ),
      body: FutureBuilder<OrderResponseDto>(
        future: _orderFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }

          if (snapshot.hasError) {
            // Manejo de errores
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    // 💡 Usar color de error del tema
                    Icon(
                      Icons.error_outline,
                      color: colorScheme.error,
                      size: 40,
                    ),
                    const SizedBox(height: 10),
                    Text(
                      'Error al cargar la orden: ${snapshot.error}',
                      textAlign: TextAlign.center,
                      style: TextStyle(color: colorScheme.onSurface),
                    ),
                    const SizedBox(height: 20),
                    ElevatedButton(
                      onPressed: _fetchOrderDetails,
                      child: const Text('Reintentar'),
                    ),
                  ],
                ),
              ),
            );
          }

          if (snapshot.hasData) {
            final order = snapshot.data!;
            return _buildOrderDetails(context, order);
          }

          // Caso por defecto (ej: sin datos, aunque con error ya cubierto)
          return const Center(
            child: Text('No se encontraron detalles de la orden.'),
          );
        },
      ),
    );
  }

  // Widget para mostrar el contenido de la orden exitosa
  Widget _buildOrderDetails(BuildContext context, OrderResponseDto order) {
    final colorScheme = Theme.of(context).colorScheme;

    // Función auxiliar para obtener el color del estado de la orden
    Color _getStatusColor(String status) {
      switch (status.toLowerCase()) {
        case 'completada':
        case 'pagada':
          return Colors.green.shade600; // Usar una sombra de verde reconocible
        case 'pendiente':
          return Colors.orange; // Usar una sombra de naranja
        case 'cancelada':
          return colorScheme.error; // Usar el color de error del tema
        default:
          return colorScheme.tertiary; // Usar un color secundario o terciario
      }
    }

    final successColor =
        Colors.green.shade600; // Color específico para el mensaje de éxito

    // 💡 Función de navegación de ejemplo (deberás crear ShippingTrackerScreen)
    void navigateToShippingTracker() {
      // Comentado temporalmente hasta que se cree la pantalla

      Navigator.of(context).push(
        MaterialPageRoute(
          builder: (_) => ShipmentTrackerScreen(orderId: order.id),
        ),
      );
      debugPrint('Navegando a Seguimiento de Envío para la Orden ${order.id}');
    }

    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 1. Mensaje de Éxito
          Card(
            // 💡 Usar color específico para éxito
            color: successColor,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            child: Padding(
              padding: const EdgeInsets.all(16.0),
              child: Row(
                children: [
                  // 💡 Usar color de texto contrastante
                  const Icon(Icons.check_circle, color: Colors.white),
                  const SizedBox(width: 10),
                  Text(
                    '¡Pago Exitoso!\n Tu orden ha sido confirmada.',
                    // 💡 Usar color de texto contrastante
                    style: TextStyle(
                      color: Colors.white,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 20),

          // 2. Botón/Widget de Seguimiento de Envío (NUEVO)
          Card(
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
            elevation: 4,
            child: ListTile(
              leading: Icon(
                Icons.delivery_dining,
                color: colorScheme.primary,
                size: 30,
              ),
              title: const Text(
                'Seguimiento del Envío',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              subtitle: const Text('Ver el estado actual de tu pedido.'),
              trailing: const Icon(Icons.arrow_forward_ios, size: 16),
              onTap: navigateToShippingTracker, // 💡 Acción para navegar
            ),
          ),
          const SizedBox(height: 20),

          // 3. Información General
          Text(
            'Orden #${order.id}',
            style: Theme.of(
              context,
            ).textTheme.headlineMedium?.copyWith(fontWeight: FontWeight.bold),
          ),
          Text(
            'Estado: ${order.status}',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(
              color: _getStatusColor(
                order.status,
              ), // 💡 Usar la función de estado
              fontWeight: FontWeight.w500,
            ),
          ),
          const Divider(height: 30),

          // 4. Resumen de Precios
          Text(
            'Total Pagado: \$${order.totalAmount.toStringAsFixed(2)}',
            style: Theme.of(
              context,
            ).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 15),

          // 5. Detalle de Ítems
          const Text(
            'Artículos del Pedido',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          // 💡 Implementación simple de la lista de ítems (siguiendo el patrón de ListTile)
          ...order.items.map(
            (item) => Padding(
              padding: const EdgeInsets.only(top: 8.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Expanded(
                    child: Text(
                      '${item.quantity}x ${item.productId}',
                      style: TextStyle(color: colorScheme.onSurfaceVariant),
                    ),
                  ),
                  Text(
                    '\$${(item.quantity * item.price).toStringAsFixed(2)}',
                    style: Theme.of(context).textTheme.bodyLarge,
                  ),
                ],
              ),
            ),
          ),

          const SizedBox(height: 30),

          // 6. Botón para continuar (ej: volver a la página de inicio)
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () {
                // Vuelve a la pantalla principal o al historial de órdenes
                Navigator.of(
                  context,
                ).popUntil((route) => route.isFirst); // Vuelve al Home
              },
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 15),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
                elevation: 3,
              ),
              child: const Text(
                'Volver al Home',
                style: TextStyle(fontSize: 16),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
