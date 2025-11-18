import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
// Importa tus DTOs y Servicios
import 'package:springshop/src/features/order/domain/services/order_service.dart';
import 'package:springshop/src/features/order/data/models/order_dto.dart'; // Tu DTO de respuesta

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
    final orderService = context.read<OrderService>();
    setState(() {
      // Llamamos al OrderService que configuraste previamente
      _orderFuture = orderService.fetchOrderById(widget.orderId);
    });
  }

  @override
  Widget build(BuildContext context) {
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
                    const Icon(Icons.error_outline, color: Colors.red, size: 40),
                    const SizedBox(height: 10),
                    Text('Error al cargar la orden: ${snapshot.error}', textAlign: TextAlign.center),
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
          return const Center(child: Text('No se encontraron detalles de la orden.'));
        },
      ),
    );
  }

  // Widget para mostrar el contenido de la orden exitosa
  Widget _buildOrderDetails(BuildContext context, OrderResponseDto order) {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 1. Mensaje de Éxito
          const Card(
            color: Colors.green,
            child: Padding(
              padding: EdgeInsets.all(16.0),
              child: Row(
                children: [
                  Icon(Icons.check_circle, color: Colors.white),
                  SizedBox(width: 10),
                  Text(
                    '¡Pago Exitoso! Tu orden ha sido confirmada.',
                    style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
            ),
          ),
          const SizedBox(height: 20),

          // 2. Información General
          Text(
            'Orden #${order.id}',
            style: Theme.of(context).textTheme.headlineMedium?.copyWith(fontWeight: FontWeight.bold),
          ),
          Text(
            'Estado: ${order.status}',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(color: Colors.green),
          ),
          const Divider(height: 30),

          // 3. Resumen de Precios
          Text(
            'Total Pagado: \$${order.totalAmount.toStringAsFixed(2)}',
            style: Theme.of(context).textTheme.titleLarge?.copyWith(fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 15),

          // 4. Detalle de Ítems (Mapeo de order.items a OrderItemsList si existe)
          const Text('Artículos del Pedido', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          // Aquí integrarías un widget para mostrar order.items
          // Por ejemplo: OrderItemsList(items: order.items) 
          // Si no tienes un widget reutilizable, itera sobre order.items aquí.
          // ... (Lógica de visualización de ítems) ...
          
          const SizedBox(height: 30),
          
          // 5. Botón para continuar (ej: volver a la página de inicio)
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () {
                // Vuelve a la pantalla principal o al historial de órdenes
                Navigator.of(context).popUntil((route) => route.isFirst); // Vuelve al Home
              },
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 15),
                backgroundColor: Theme.of(context).colorScheme.primary,
                foregroundColor: Theme.of(context).colorScheme.onPrimary,
              ),
              child: const Text('Volver al Home', style: TextStyle(fontSize: 16)),
            ),
          ),
        ],
      ),
    );
  }
}