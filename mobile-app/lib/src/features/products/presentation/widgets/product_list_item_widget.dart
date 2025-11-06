// lib/src/features/products/presentation/widgets/product_list_item_widget.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';

class ProductListItemWidget extends StatelessWidget {
  final Product product;
  final ValueChanged<String> onProductTap; // Callback para el clic, recibe el ID

  const ProductListItemWidget({
    super.key,
    required this.product,
    required this.onProductTap,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final textTheme = Theme.of(context).textTheme;

    return GestureDetector(
      onTap: () => onProductTap(product.id), // Llama al callback con el ID
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 8.0, horizontal: 16.0),
        // Color de fondo adaptable al tema, un poco más oscuro si es necesario para contrastar con el fondo general
        color: colorScheme.surface, 
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Imagen del producto
            Container(
              width: 120,
              height: 120,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8.0),
                border: Border.all(color: colorScheme.outline.withOpacity(0.2)),
                color: colorScheme.surfaceContainerHighest, // Color de fondo para la imagen
              ),
              clipBehavior: Clip.antiAlias, // Recortar la imagen si es más grande
              child: Image.network(
                product.imageUrl,
                fit: BoxFit.cover,
                loadingBuilder: (context, child, loadingProgress) {
                  if (loadingProgress == null) return child;
                  return Center(
                    child: CircularProgressIndicator(
                      value: loadingProgress.expectedTotalBytes != null
                          ? loadingProgress.cumulativeBytesLoaded /
                              loadingProgress.expectedTotalBytes!
                          : null,
                      color: colorScheme.primary,
                    ),
                  );
                },
                errorBuilder: (context, error, stackTrace) {
                  return Icon(
                    Icons.broken_image,
                    color: colorScheme.error,
                    size: 40,
                  );
                },
              ),
            ),
            const SizedBox(width: 16),
            // Detalles del producto
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    product.name,
                    style: textTheme.titleMedium?.copyWith(
                      color: colorScheme.onSurface,
                      fontWeight: FontWeight.w600,
                    ),
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '${product.currency} ${product.price.toStringAsFixed(2)}', // Formato de precio
                    style: textTheme.titleLarge?.copyWith(
                      color: colorScheme.primary, // Resaltar el precio con el color primario
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 8),
                  // Aquí se podrían añadir más detalles como "Envío gratis", "Vendidos", etc.
                  // Por ahora, lo dejamos simple como pediste.
                  // Text(
                  //   'Envío internacional gratis',
                  //   style: textTheme.bodySmall?.copyWith(color: colorScheme.secondary),
                  // ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}