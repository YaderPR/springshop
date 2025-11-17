import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/cart/data/services/cart_service.dart';

class ProductActionBar extends StatelessWidget {
  // 💡 Necesitamos el ID del producto que se está visualizando
  final String productId;
  
  const ProductActionBar({super.key, required this.productId});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    
    // Accedemos al CartService
    final cartService = context.read<CartService>();
    
    // Función para mostrar un SnackBar de confirmación
    void showConfirmation(String message) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(message)),
      );
    }

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
              onPressed: () async {
                try {
                  // Lógica: Agregar 1 unidad del producto al carrito
                  await cartService.addItem(productId, quantity: 1);
                  showConfirmation('Producto añadido a la cesta!');
                } catch (e) {
                  showConfirmation('Error al añadir: ${e.toString()}');
                }
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
                // TODO: Implementar lógica de compra inmediata (generalmente es añadir al carrito y navegar a checkout)
                print('Comprar Ahora (Simulado) - Producto ID: $productId');
                showConfirmation('Funcionalidad de Compra Rápida Pendiente.');
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