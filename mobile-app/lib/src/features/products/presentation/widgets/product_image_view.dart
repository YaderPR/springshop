// lib/src/features/products/presentation/widgets/product_image_view.dart
import 'package:flutter/material.dart';

class ProductImageView extends StatelessWidget {
  final String imageUrl;

  const ProductImageView({super.key, required this.imageUrl});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Center(
        child: Image.network(
          imageUrl,
          height: 300, // Altura fija para la imagen principal
          fit: BoxFit.contain, // Asegura que la imagen se vea completa
          loadingBuilder: (context, child, loadingProgress) {
            if (loadingProgress == null) return child;
            return const SizedBox(
              height: 300,
              child: Center(child: CircularProgressIndicator()),
            );
          },
          errorBuilder: (context, error, stackTrace) {
            return const SizedBox(
              height: 300,
              child: Center(child: Icon(Icons.image_not_supported, size: 50)),
            );
          },
        ),
      ),
    );
  }
}