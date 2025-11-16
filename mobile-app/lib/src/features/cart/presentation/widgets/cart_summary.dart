// lib/src/features/cart/presentation/widgets/cart_summary.dart

import 'package:flutter/material.dart';
import 'package:springshop/src/features/cart/data/services/cart_service.dart'; // Asumo el path

class CartSummary extends StatelessWidget {
  final CartService service;

  const CartSummary({super.key, required this.service});

  @override
  Widget build(BuildContext context) {
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
                    color: colorScheme.primary, // Color primario para el énfasis
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Botón de Checkout
            ElevatedButton(
              onPressed: total > 0 ? () {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('¡Procediendo al pago! (Simulado)')),
                );
              } : null,
              style: ElevatedButton.styleFrom(
                padding: const EdgeInsets.symmetric(vertical: 16),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                // ElevatedButton usa los colores del tema global por defecto, pero se confirma aquí
              ),
              child: const Text(
                'Proceder al Pago',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
            ),
          ],
        ),
      ),
    );
  }
}