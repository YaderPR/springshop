// lib/src/features/products/presentation/screens/product_detail_screen.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_action_bar.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_description_section.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_image_view.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_info_section.dart';


class ProductDetailScreen extends StatelessWidget {
  final Product product;

  const ProductDetailScreen({
    super.key,
    required this.product,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(product.name, maxLines: 1, overflow: TextOverflow.ellipsis),
        actions: [
          IconButton(onPressed: () {}, icon: const Icon(Icons.share_outlined)),
          IconButton(onPressed: () {}, icon: const Icon(Icons.favorite_border)),
          IconButton(onPressed: () {}, icon: const Icon(Icons.shopping_cart_outlined)),
        ],
      ),
      
      // 1. Cuerpo principal con scroll
      body: SingleChildScrollView(
        padding: const EdgeInsets.only(bottom: 80), // Espacio para la barra de acciones
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 2. Imagen grande
            ProductImageView(imageUrl: product.imageUrl),

            // 3. Sección de información básica (Precio, Nombre, Stock)
            ProductInfoSection(product: product),

            // 4. Separador visual (como en eBay)
            const Divider(height: 10, thickness: 10, color: Color(0xFFF0F0F0)),
            
            // 5. Sección de Descripción
            ProductDescriptionSection(description: product.description),
            
            // 6. Espacio adicional (simulando otras secciones de eBay)
            const SizedBox(height: 30),
          ],
        ),
      ),

      // 7. Barra de acción fija en la parte inferior
      bottomNavigationBar: ProductActionBar(),
    );
  }
}