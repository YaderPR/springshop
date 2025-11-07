// lib/src/features/products/presentation/widgets/product_action_bar.dart
import 'package:flutter/material.dart';

class ProductActionBar extends StatelessWidget {
  const ProductActionBar({super.key});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 10.0),
      decoration: BoxDecoration(
        color: colorScheme.surface,
        boxShadow: const [
          BoxShadow(
            color: Colors.black12,
            blurRadius: 5.0,
            offset: Offset(0, -2),
          ),
        ],
      ),
      child: Row(
        children: [
          // Botón 1: Agregar a la cesta
          Expanded(
            child: OutlinedButton.icon(
              onPressed: () {
                // TODO: Implementar lógica de agregar al carrito
                print('Agregado a la Cesta (Simulado)');
              },
              icon: const Icon(Icons.shopping_cart_outlined),
              label: const Text('Añadir a la cesta'),
              style: OutlinedButton.styleFrom(
                minimumSize: const Size(double.infinity, 50),
                side: BorderSide(color: colorScheme.primary, width: 2),
                foregroundColor: colorScheme.primary,
                backgroundColor: colorScheme.surface,
              ),
            ),
          ),
          const SizedBox(width: 10),

          // Botón 2: Comprar ahora (Botón principal y relleno)
          Expanded(
            child: ElevatedButton(
              onPressed: () {
                // TODO: Implementar lógica de compra inmediata
                print('Comprar Ahora (Simulado)');
              },
              child: const Text('Cómpralo ya'),
              style: ElevatedButton.styleFrom(
                minimumSize: const Size(double.infinity, 50),
                backgroundColor: colorScheme.primary,
                foregroundColor: colorScheme.onPrimary,
                textStyle: const TextStyle(fontWeight: FontWeight.bold),
              ),
            ),
          ),
        ],
      ),
    );
  }
}