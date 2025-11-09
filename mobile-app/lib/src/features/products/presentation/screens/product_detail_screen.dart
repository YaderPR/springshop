// lib/src/features/products/presentation/screens/product_detail_screen.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/entities/apparel.dart';
import 'package:springshop/src/features/products/domain/entities/supplement.dart';
import 'package:springshop/src/features/products/domain/entities/workout_accessory.dart';

import 'package:springshop/src/features/products/presentation/widgets/apparels/apparel_details_section.dart'; 
import 'package:springshop/src/features/products/presentation/widgets/workout_accessories/workout_accessory_details_section.dart'; 
// 🔑 Importar el widget de detalles de suplementos
import 'package:springshop/src/features/products/presentation/widgets/supplements/supplement_details_section.dart'; 

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
    
    // 💡 Detección de tipos
    final isApparel = product is Apparel;
    final isSupplement = product is Supplement;
    final isWorkoutAccessory = product is WorkoutAccessory;
    
    // Casteo seguro de tipos
    final Apparel? apparel = isApparel ? product as Apparel : null;
    final Supplement? supplement = isSupplement ? product as Supplement : null;
    final WorkoutAccessory? workoutAccessory = isWorkoutAccessory ? product as WorkoutAccessory : null;

    final bool hasSpecializedSection = isApparel || isSupplement || isWorkoutAccessory;

    return Scaffold(
      appBar: AppBar(
        title: Text(product.name, maxLines: 1, overflow: TextOverflow.ellipsis),
        actions: [
          IconButton(onPressed: () {}, icon: const Icon(Icons.share_outlined)),
          IconButton(onPressed: () {}, icon: const Icon(Icons.favorite_border)),
          IconButton(onPressed: () {}, icon: const Icon(Icons.shopping_cart_outlined)),
        ],
      ),
      
      body: SingleChildScrollView(
        padding: const EdgeInsets.only(bottom: 80),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 2. Imagen grande
            ProductImageView(imageUrl: product.imageUrl),

            // 3. Sección de información básica (Precio, Nombre, Stock)
            ProductInfoSection(product: product),

            // 4. Separador visual (Siempre presente antes de la primera sección especializada)
            const Divider(height: 10, thickness: 10, color: Color(0xFFF0F0F0)),
            
            // 🔑 INSERCIÓN CONDICIONAL: Detalles de Apparel
            if (isApparel && apparel != null)
              ApparelDetailsSection(apparel: apparel),
            
            // 🔑 INSERCIÓN CONDICIONAL: Detalles de Workout Accessory
            if (isWorkoutAccessory && workoutAccessory != null)
              WorkoutAccessoryDetailsSection(accessory: workoutAccessory),
            
            // 🔑 INSERCIÓN CONDICIONAL: Detalles de Supplement (AHORA CON EL WIDGET REAL)
            if (isSupplement && supplement != null)
              SupplementDetailsSection(supplement: supplement),
            
            // Opcional: Si ninguna especialización usó el divisor, podríamos querer uno antes de la descripción.
            if (!hasSpecializedSection)
                const Divider(height: 10, thickness: 10, color: Color(0xFFF0F0F0)),


            // 5. Sección de Descripción
            ProductDescriptionSection(description: product.description),
            
            // 6. Espacio adicional 
            const SizedBox(height: 30),
          ],
        ),
      ),

      // 7. Barra de acción fija en la parte inferior
      bottomNavigationBar: ProductActionBar(),
    );
  }
}