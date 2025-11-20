import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/core/auth/auth_state_notifier.dart';

// Importaciones de servicios y entidades
import 'package:springshop/src/features/cart/data/services/cart_service.dart';
import 'package:springshop/src/features/cart/domain/entities/cart_item.dart';

// Importaciones de dirección real
import 'package:springshop/src/features/order/domain/entities/address_entity.dart';
import 'package:springshop/src/features/order/domain/services/address_service.dart';
// Importamos todos los widgets modulares
import '../widgets/order_summary_widgets.dart';

// ====================================================================
// CLASE DE CÁLCULO DE RESUMEN
// ====================================================================

/// Contiene los cálculos financieros del pedido basados en los ítems del carrito.
class OrderSummaryCalculated {
 final List<CartItem> items;
 final double shippingCost = 10.00; // Temporal
 final double taxRate = 0.15; // Temporal

 OrderSummaryCalculated({required this.items});

 // Calcula el subtotal sumando el subtotal de cada ítem del carrito
 double get subtotal => items.fold(0.0, (sum, item) => sum + item.subtotal);
 double get taxes => subtotal * taxRate;
 double get total => subtotal + shippingCost + taxes;
}

// ====================================================================
// PANTALLA PRINCIPAL - STATEFUL WIDGET
// ====================================================================

class OrderSummaryScreen extends StatefulWidget {
 final int userId;
 final int addressId; // ID de la dirección a cargar

 const OrderSummaryScreen({
  super.key,
  required this.userId,
  required this.addressId,
 });

 @override
 State<OrderSummaryScreen> createState() => _OrderSummaryScreenState();
}

class _OrderSummaryScreenState extends State<OrderSummaryScreen> {
 // Estado para almacenar la dirección y el estado de carga
 AddressEntity? _address;
 bool _isLoadingAddress = true; // Indica si estamos cargando la dirección

 @override
 void initState() {
  super.initState();
  // Iniciar la carga de la dirección
  _fetchAddress();
 }

 // Función para obtener la dirección por ID usando el AddressService real
 Future<void> _fetchAddress() async {
  try {
   final addressService = Provider.of<AddressService>(
    context,
    listen: false,
   );

   print(
    '📞 [OrderSummaryScreen] Solicitando dirección ID: ${widget.addressId}',
   );

   // 🚀 LLAMADA REAL AL SERVICIO: Usamos el método findAddressById
   final AddressEntity? fetchedAddress = await addressService
     .findAddressById(widget.addressId);

   if (mounted) {
    setState(() {
     _address = fetchedAddress;
     _isLoadingAddress = false;
    });
    if (_address != null) {
     print(
      '✅ [OrderSummaryScreen] Dirección cargada: ${_address!.street}',
     );
    } else {
     print(
      '❌ [OrderSummaryScreen] Dirección no encontrada para ID: ${widget.addressId}',
     );
    }
   }
  } catch (e) {
   print('❌ Error al cargar la dirección: $e');
   if (mounted) {
    ScaffoldMessenger.of(context).showSnackBar(
     const SnackBar(
      content: Text('Error al cargar la dirección de envío.'),
     ),
    );
    setState(() {
     _isLoadingAddress = false;
     _address =
       null; // Asegurarse de que el estado sea nulo en caso de error
    });
   }
  }
 }

 @override
 Widget build(BuildContext context) {
  final colorScheme = Theme.of(context).colorScheme;
  // Leemos el AuthStateNotifier con listen: true para acceder a 'user'
  final authNotifier = Provider.of<AuthStateNotifier>(context); 
    // Usamos listen: false en este caso para solo obtener el cartService y evitar reconstrucciones innecesarias.
  final cartService = Provider.of<CartService>(context); 

    // Obtenemos el cartId
    final int cartId = cartService.currentCartId ?? 0;
    
  return Consumer<CartService>(
   builder: (context, _, child) {
    final cartItems = cartService.items;
    final summary = OrderSummaryCalculated(items: cartItems);

    // Mostrar indicador de carga si el servicio de carrito o la dirección se están cargando
    if (cartService.isLoading || _isLoadingAddress) {
     return Scaffold(
      appBar: AppBar(title: const Text('Cargando Pedido...')),
      body: const Center(child: CircularProgressIndicator()),
     );
    }

    // Manejar caso de dirección no encontrada o error de carga
    if (_address == null) {
     return Scaffold(
      appBar: AppBar(title: const Text('Error de Pedido')),
      body: Center(
       child: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Text(
         'La dirección de envío seleccionada no se pudo encontrar. Por favor, regresa al carrito e inténtalo de nuevo.',
         style: Theme.of(context).textTheme.titleMedium,
         textAlign: TextAlign.center,
        ),
       ),
      ),
     );
    }
        
        // Manejar el caso de carrito vacío o ID de carrito inválido
        if (cartItems.isEmpty || cartId == 0) {
     return Scaffold(
      appBar: AppBar(title: const Text('Resumen del Pedido')),
      body: Center(
       child: Text(
        'Tu carrito está vacío o no es válido.',
        style: Theme.of(context).textTheme.headlineSmall,
       ),
      ),
     );
    }

    // Si todo está cargado y válido, mostramos la pantalla
    return Scaffold(
     appBar: AppBar(
      title: const Text('Resumen del Pedido'),
      backgroundColor: colorScheme.surface,
      foregroundColor: colorScheme.onSurface,
      elevation: 1,
     ),
     body: SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Column(
       crossAxisAlignment: CrossAxisAlignment.start,
       children: <Widget>[
        // 1. Información de IDs (Debug)
        OrderIdsDebugInfo(
         cartId: cartId, // Usamos el cartId extraído
         userId: widget.userId,
         addressId: widget.addressId,
        ),
        const SizedBox(height: 16),

        // 2. Dirección de Envío (Datos REALES)
        const SectionTitle(title: 'Dirección de Envío'),
        ShippingAddressCard(
         username: authNotifier.user?.username, // 🔑 Usamos el username del AuthNotifier
         addressLine1: '${_address!.street}, ${_address!.state}',
         city: _address!.city,
         zipCode: _address!.zipCode,
         phoneNumber: _address!.phoneNumber,
        ),
        const SizedBox(height: 24),

        // 3. Ítems del Carrito
        SectionTitle(title: 'Artículos (${cartItems.length})'),
        OrderItemsList(items: cartItems),
        const SizedBox(height: 24),

        // 4. Detalle del Pago
        const SectionTitle(title: 'Detalle del Pago'),
        PriceSummaryCard(summary: summary),
        const SizedBox(height: 80),
       ],
      ),
     ),
     // Botón de Pagar fijo en la parte inferior
     bottomNavigationBar: PayNowButton(
      cartId: cartId, 
      userId: widget.userId, 
      addressId: widget.addressId,
     ),
    );
   },
  );
 }
}