// lib/src/features/cart/presentation/widgets/cart_item_card.dart

import 'package:flutter/material.dart';
import 'package:springshop/src/features/cart/data/services/cart_service.dart';
import 'package:springshop/src/features/cart/domain/entities/cart_item.dart'; // Asumo el path

class CartItemCard extends StatelessWidget {
  final CartItem item;
  final CartService service;

  const CartItemCard({super.key, required this.item, required this.service});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final product = item.productDetails;

    if (product == null) {
      return Center(
          child: Text("Producto no encontrado.",
              style: TextStyle(color: colorScheme.error)));
    }

    return Card(
      elevation: 2,
      margin: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 4.0),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(12.0),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Imagen del Producto
            ClipRRect(
              borderRadius: BorderRadius.circular(8.0),
              child: Image.network(
                product.imageUrl,
                width: 70,
                height: 70,
                fit: BoxFit.cover,
                errorBuilder: (context, error, stackTrace) => Container(
                  width: 70,
                  height: 70,
                  color: colorScheme.surfaceVariant, // Usar un color temático
                  child: Icon(Icons.shopping_bag_outlined, color: colorScheme.onSurfaceVariant),
                ),
              ),
            ),
            const SizedBox(width: 12),

            // Detalles y Precio
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    product.name,
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                      color: colorScheme.onSurface,
                    ),
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    'Subtotal: \$${item.subtotal.toStringAsFixed(2)}',
                    style: TextStyle(
                        color: colorScheme.primary,
                        fontWeight: FontWeight.w600), // Usar color primario
                  ),
                ],
              ),
            ),

            // Control de Cantidad
            Row(
              children: [
                // Botón de Restar (usando color de error para acciones destructivas)
                IconButton(
                  icon: const Icon(Icons.remove_circle_outline, size: 24),
                  onPressed: () =>
                      service.updateQuantity(item.productId, item.quantity - 1),
                  color: colorScheme.error,
                ),
                Text(
                  '${item.quantity}',
                  style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onSurface),
                ),
                // Botón de Sumar (usando color secundario o primario para acciones positivas)
                IconButton(
                  icon: const Icon(Icons.add_circle_outline, size: 24),
                  onPressed: () =>
                      service.updateQuantity(item.productId, item.quantity + 1),
                  color: colorScheme.secondary,
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}