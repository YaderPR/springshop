// lib/src/features/cart/presentation/widgets/cart_summary.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/core/auth/auth_state_notifier.dart'; 
import 'package:springshop/src/features/cart/data/services/cart_service.dart';
import 'package:springshop/src/features/order/domain/services/address_service.dart'; // Importar el servicio de dirección
import 'package:springshop/src/features/order/presentation/screens/address_form_screen.dart'; // Importar el formulario de dirección
import 'package:springshop/src/features/order/presentation/screens/order_summary_screen.dart'; 

class CartSummary extends StatelessWidget {
  final CartService service;

  const CartSummary({super.key, required this.service});

  // Lógica de navegación y verificación de dirección
  Future<void> _handleCheckout(BuildContext context) async {
    // Escuchamos el AuthNotifier con listen: false porque solo necesitamos el valor actual aquí.
    final authNotifier = Provider.of<AuthStateNotifier>(context, listen: false);
    final addressService = Provider.of<AddressService>(context, listen: false);

    // Obtener el ID del usuario logeado. Usa '0' como fallback si es null o no es String numérico.
    final userId = int.tryParse(authNotifier.user?.id ?? '0') ?? 0;
    
    if (userId == 0) {
       // Mensaje de error si el ID del usuario no es válido.
       ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Error: Usuario no identificado.')),
      );
      return;
    }

    try {
      // 1. Verificar si el usuario tiene una dirección registrada
      final existingAddress = await addressService.findLastAddressByUserId(userId);

      if (!context.mounted) return; // Asegurar que el widget siga montado

      if (existingAddress != null) {
        // 2. Si existe, navegar directamente al resumen de orden
        print('✅ Dirección encontrada. Navegando a OrderSummary con ID: ${existingAddress.id}');
        Navigator.of(context).push(
          MaterialPageRoute(
            builder: (context) => OrderSummaryScreen(
              userId: userId,
              addressId: existingAddress.id!, // Usamos el ID de la dirección existente
            ),
          ),
        );
      } else {
        // 3. Si NO existe, navegar a la pantalla de formulario de dirección
        print('⚠️ No se encontró dirección. Navegando a AddressFormScreen.');
        Navigator.of(context).push(
          MaterialPageRoute(
            builder: (context) => const AddressFormScreen(),
          ),
        );
      }
    } catch (e) {
       print('❌ Error durante la verificación de dirección: $e');
       if (!context.mounted) return;
       ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Error al verificar la dirección. Inténtalo de nuevo.')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    // Escucha el estado de login para habilitar/deshabilitar el botón
    final authNotifier = Provider.of<AuthStateNotifier>(context); 
    
    final total = service.totalAmount;
    final colorScheme = Theme.of(context).colorScheme;

    return Card(
      elevation: 4,
      margin: const EdgeInsets.all(16.0),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            // Resumen de totales
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Subtotal:', style: TextStyle(fontSize: 16, color: colorScheme.onSurfaceVariant)),
                Text('\$${total.toStringAsFixed(2)}', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w500, color: colorScheme.onSurface)),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Costo de Envío:', style: TextStyle(fontSize: 16, color: colorScheme.onSurfaceVariant)),
                Text('\$15.00', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w500, color: colorScheme.onSurface)),
              ],
            ),
            const Divider(height: 20, thickness: 1),

            // Total Final
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text('Total a Pagar:', style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: colorScheme.onSurface)),
                Text(
                  '\$${(total + 15.00).toStringAsFixed(2)}',
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.w900,
                    color: colorScheme.primary, 
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Botón de Checkout
            ElevatedButton(
              // Si el total es > 0 Y el usuario está logeado, habilita el botón y llama a la nueva lógica.
              onPressed: total > 0 && authNotifier.isLoggedIn ? () => _handleCheckout(context) : null,
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              ),
              child: Text(
                authNotifier.isLoggedIn ? 'Proceder al Pago' : 'Inicia Sesión para Pagar',
                style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
      ),
    );
  }
}