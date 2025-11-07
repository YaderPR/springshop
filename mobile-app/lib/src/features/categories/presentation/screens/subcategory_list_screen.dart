// lib/src/features/categories/presentation/screens/subcategory_list_screen.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/categories/domain/entities/subcategory.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_list_widget.dart';

// --------------------

class SubcategoryListScreen extends StatelessWidget {
  // Recibe el nombre de la categoría principal (ej. "Ropa")
  final String categoryName;

  const SubcategoryListScreen({
    super.key,
    required this.categoryName,
  });

  // Datos mock para las subcategorías dinámicas
  final List<Subcategory> _mockSubcategories = const [
    Subcategory(
      id: 'sub1', 
      name: 'Pantalones', 
      imageUrl: '👖', // Usamos un emoji como mock de imagen
      productIdsMock: [1, 5, 8, 12, 15],
    ),
    Subcategory(
      id: 'sub2', 
      name: 'Camisetas', 
      imageUrl: '👕',
      productIdsMock: [2, 6, 9, 13, 16],
    ),
    Subcategory(
      id: 'sub3', 
      name: 'Vestidos', 
      imageUrl: '👗',
      productIdsMock: [3, 7, 10, 14, 17],
    ),
    Subcategory(
      id: 'sub4', 
      name: 'Calzado', 
      imageUrl: '👟',
      productIdsMock: [4, 11, 18],
    ),
  ];

  // 💡 Lógica de redirección a ProductListWidget
  void _handleSubcategoryClick(BuildContext context, String title, List<int> productIds) {
    print('Evento Click: Navegando a productos de $title con ${productIds.length} IDs.');
    
    // Redirección al ProductListWidget, pasándole los IDs correspondientes
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ProductListWidget(productIds: productIds),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    // IDs mock para la opción genérica (trae todos los productos posibles)
    const List<int> genericProductIds = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20];

    return Scaffold(
      appBar: AppBar(
        title: Text(categoryName), // Título de la categoría principal (ej. "Ropa")
        actions: const [
          // Iconos de búsqueda y carrito como en tu boceto
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 8.0),
            child: Icon(Icons.search),
          ),
          Padding(
            padding: EdgeInsets.symmetric(horizontal: 8.0),
            child: Icon(Icons.shopping_cart_outlined),
          ),
        ],
      ),
      
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // --- 3. Card Genérica/Mix (FIJA) ---
              SubcategoryCard(
                title: 'Genérica / Mix',
                icon: '🛒',
                color: colorScheme.primaryContainer, // Color que destaca
                onTap: () => _handleSubcategoryClick(
                  context, 
                  'Genérica / Mix', 
                  genericProductIds // Pasa todos los IDs
                ),
              ),
              
              const SizedBox(height: 16),
              
              // --- 4. Cards Dinámicas (Subcategorías) ---
              ..._mockSubcategories.map((subcategory) {
                return Padding(
                  padding: const EdgeInsets.only(bottom: 8.0),
                  child: SubcategoryCard(
                    title: subcategory.name,
                    icon: subcategory.imageUrl,
                    color: colorScheme.surfaceContainer, // Color más sutil
                    onTap: () => _handleSubcategoryClick(
                      context, 
                      subcategory.name, 
                      subcategory.productIdsMock // Pasa los IDs de la subcategoría
                    ),
                  ),
                );
              }).toList(),
            ],
          ),
        ),
      ),
      // Barra de navegación inferior (simulada)
      bottomNavigationBar: const BottomAppBar(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            Icon(Icons.home_outlined),
            Icon(Icons.search),
            Icon(Icons.person_outline),
          ],
        ),
      ),
    );
  }
}


class SubcategoryCard extends StatelessWidget {
  final String title;
  final String icon;
  final Color color;
  final VoidCallback onTap;

  const SubcategoryCard({
    super.key,
    required this.title,
    required this.icon,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      color: color,
      elevation: 0, // Usamos elevación cero para un diseño más plano
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: InkWell( // InkWell para el efecto visual de click
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 20.0, horizontal: 16.0),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Nombre de la subcategoría
              Text(
                title,
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.w600,
                  color: Theme.of(context).colorScheme.onSurface,
                ),
              ),
              // Icono/Emoji
              Text(
                icon,
                style: const TextStyle(fontSize: 30),
              ),
            ],
          ),
        ),
      ),
    );
  }
}