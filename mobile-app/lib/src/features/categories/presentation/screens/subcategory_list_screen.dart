// lib/src/features/categories/presentation/screens/subcategory_list_screen.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/categories/domain/entities/subcategory.dart';
import 'package:springshop/src/features/categories/presentation/widgets/subcategory_card.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_list_widget.dart';

// --------------------

class SubcategoryListScreen extends StatelessWidget {
  // Recibe el nombre de la categoría principal (ej. "Ropa")
  final String categoryName;
  final int categoryId;
  final List<int> categoryProductIds;

  const SubcategoryListScreen({
    super.key,
    required this.categoryId,
    required this.categoryName, 
    required this.categoryProductIds, 
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
                color: colorScheme.primaryContainer,
                onTap: () => _handleSubcategoryClick(
                  context, 
                  'Genérica / Mix', 
                  // 🔑 CAMBIO 2: Usar los IDs recibidos de la Categoría Principal
                  categoryProductIds 
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
