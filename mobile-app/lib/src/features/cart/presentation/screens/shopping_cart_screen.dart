import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/cart/data/services/cart_service.dart';
import 'package:springshop/src/features/cart/presentation/widgets/cart_item_card.dart';
import 'package:springshop/src/features/cart/presentation/widgets/cart_summary.dart';


class ShoppingCartScreen extends StatelessWidget {
  const ShoppingCartScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Consumer<CartService>(
      builder: (context, cartService, child) {
        return Scaffold(
          appBar: AppBar(
            title: Text('Mi Carrito (${cartService.items.length})'),
            elevation: 1, // Leve elevación para mejor separación
            backgroundColor:
                colorScheme.surface, // Fondo del AppBar basado en el tema
            foregroundColor: colorScheme.onSurface, // Color del título/iconos
          ),
          body: cartService.isLoading
              ? const Center(child: CircularProgressIndicator())
              : cartService.items.isEmpty
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(
                        Icons.shopping_cart_outlined,
                        size: 80,
                        color: colorScheme.outlineVariant,
                      ), // Color temático
                      const SizedBox(height: 16),
                      Text(
                        'Tu carrito está vacío.',
                        style: TextStyle(
                          fontSize: 20,
                          color: colorScheme.onSurfaceVariant,
                        ), // Color temático
                      ),
                      const SizedBox(height: 8),
                      Text(
                        '¡Añade algunos productos increíbles!',
                        style: TextStyle(
                          fontSize: 16,
                          color: colorScheme.onSurfaceVariant,
                        ), // Color temático
                      ),
                    ],
                  ),
                )
              : Column(
                  children: [
                    Expanded(
                      child: ListView.builder(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 12.0,
                          vertical: 8.0,
                        ),
                        itemCount: cartService.items.length,
                        itemBuilder: (context, index) {
                          final item = cartService.items[index];
                          return CartItemCard(item: item, service: cartService);
                        },
                      ),
                    ),
                    CartSummary(service: cartService),
                  ],
                ),
        );
      },
    );
  }
}
