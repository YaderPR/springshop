// lib/src/features/products/presentation/widgets/product_info_section.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';

class ProductInfoSection extends StatelessWidget {
  final Product product;

  const ProductInfoSection({super.key, required this.product});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    
    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Precio (Grande y en color primario)
          Text(
            '\$${product.price.toStringAsFixed(2)}',
            style: Theme.of(context).textTheme.headlineLarge?.copyWith(
                  color: colorScheme.error, // Precio destacado en color de advertencia/error
                  fontWeight: FontWeight.w800,
                ),
          ),
          const SizedBox(height: 8),

          // Nombre del producto
          Text(
            product.name,
            style: Theme.of(context).textTheme.titleLarge,
          ),
          const SizedBox(height: 12),

          // Estado del stock
          Text(
            product.stock > 0 
                ? '¡${product.stock} disponibles! Compra ahora.'
                : 'Sin stock.',
            style: TextStyle(
              color: product.stock > 0 ? Colors.green.shade700 : colorScheme.error,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }
}