// lib/src/features/products/presentation/screens/product_detail_screen.dart

import 'package:flutter/material.dart';
// 💡 Importar el BottomNavBarWidget principal para la persistencia
import 'package:springshop/src/features/home/presentation/widgets/bottom_nav_bar_widget.dart'; 
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/entities/apparel.dart';
import 'package:springshop/src/features/products/domain/entities/supplement.dart';
import 'package:springshop/src/features/products/domain/entities/workout_accessory.dart';

import 'package:springshop/src/features/products/presentation/widgets/apparels/apparel_details_section.dart'; 
import 'package:springshop/src/features/products/presentation/widgets/workout_accessories/workout_accessory_details_section.dart'; 
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
    
    // 💡 Detección de tipos y casteos
    final isApparel = product is Apparel;
    final isSupplement = product is Supplement;
    final isWorkoutAccessory = product is WorkoutAccessory;
    
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
      
      // 🔑 Usamos Stack para superponer la barra de acción del producto sobre el contenido scrollable
      body: Stack(
        children: [
          // 1. Contenido principal (Scrollable)
          SingleChildScrollView(
            // 🔑 Añadir padding al final para evitar que el contenido sea cubierto 
            // por la barra de acción del producto Y la barra de navegación principal.
            // Se estima un espacio de seguridad de ~130-150 píxeles.
            padding: const EdgeInsets.only(bottom: 140), 
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // 2. Imagen grande
                ProductImageView(imageUrl: product.imageUrl),

                // 3. Sección de información básica (Precio, Nombre, Stock)
                ProductInfoSection(product: product),

                // 4. Separador visual
                const Divider(height: 10, thickness: 10, color: Color(0xFFF0F0F0)),
                
                // 🔑 INSERCIÓN CONDICIONAL: Detalles especializados
                if (isApparel && apparel != null)
                  ApparelDetailsSection(apparel: apparel),
                
                if (isWorkoutAccessory && workoutAccessory != null)
                  WorkoutAccessoryDetailsSection(accessory: workoutAccessory),
                
                if (isSupplement && supplement != null)
                  SupplementDetailsSection(supplement: supplement),
                
                if (!hasSpecializedSection)
                    const Divider(height: 10, thickness: 10, color: Color(0xFFF0F0F0)),

                // 5. Sección de Descripción
                ProductDescriptionSection(description: product.description),
                
                const SizedBox(height: 30),
              ],
            ),
          ),

          // 2. Barra de acción del producto (FIJA en la parte inferior del BODY)
          const Align(
            alignment: Alignment.bottomCenter,
            child: ProductActionBar(), // 💡 Se coloca aquí y no como bottomNavigationBar
          ),
        ],
      ),

      // 7. Barra de navegación principal (bottomNavigationBar del Scaffold)
      // 💡 Necesitas definir el onTap para que esta barra funcione correctamente 
      // y regrese a la raíz de la navegación.
      bottomNavigationBar: BottomNavBarWidget(
        // Asumimos que esta pantalla se navega desde la Portada (índice 0)
        currentIndex: 0, 
        onTap: (index) {
          // Lógica para volver a la pantalla principal y cambiar de pestaña
          // 1. Desapilar todas las rutas hasta la raíz (HomeScreen)
          Navigator.of(context).popUntil((route) => route.isFirst); 
          
          // 2. NOTA: Para cambiar el índice del HomeScreen desde aquí, 
          // necesitarías un GlobalKey o un Provider. Por simplicidad, 
          // la acción solo regresa a la portada, que es la primera ruta.
        },
      ),
    );
  }
}