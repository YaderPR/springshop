// lib/src/features/categories/presentation/screens/subcategory_list_screen.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/categories/domain/entities/subcategory.dart';
import 'package:springshop/src/features/categories/presentation/widgets/subcategory_card.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_list_widget.dart';
// 💡 Importamos el BottomNavBarWidget para la navegación persistente
import 'package:springshop/src/features/home/presentation/widgets/bottom_nav_bar_widget.dart'; 

import 'package:springshop/src/features/categories/domain/repositories/apparel_category_repository.dart'; 
import 'package:springshop/src/features/categories/domain/repositories/accessory_category_repository.dart'; 
const int WORKOUT_ACCESSORY_CATEGORY_ID = 3;
const int APPAREL_CATEGORY_ID = 4;
const int SUPPLEMENT_CATEGORY_ID = 5;

class SubcategoryListScreen extends StatefulWidget {
  final String categoryName;
  final int categoryId;
  final List<int> categoryProductIds;

  const SubcategoryListScreen({
    super.key,
    required this.categoryId,
    required this.categoryName, 
    required this.categoryProductIds, 
  });

  @override
  State<SubcategoryListScreen> createState() => _SubcategoryListScreenState();
}

class _SubcategoryListScreenState extends State<SubcategoryListScreen> {
  late Future<List<Subcategory>> _subcategoriesFuture;

  @override
  void initState() {
    super.initState();
    _subcategoriesFuture = _fetchSubcategories();
  }
  
  // 🚀 Lógica Central para Obtener Subcategorías (Sin Cambios)
  Future<List<Subcategory>> _fetchSubcategories() async {
    final int id = widget.categoryId;
    
    try {
      if (id == APPAREL_CATEGORY_ID) {
        print('Cargando subcategorías de Apparel (ID $APPAREL_CATEGORY_ID) desde API.');
        final repository = context.read<ApparelCategoryRepository>();
        return repository.getCategories();
        
      } else if (id == WORKOUT_ACCESSORY_CATEGORY_ID) {
        print('Cargando subcategorías de Workout Accessory (ID $WORKOUT_ACCESSORY_CATEGORY_ID) desde API.');
        final repository = context.read<AccessoryCategoryRepository>();
        return repository.getCategories();

      } else if (id == SUPPLEMENT_CATEGORY_ID) { 
        print('SUPPLEMENTS (ID $SUPPLEMENT_CATEGORY_ID) no tiene subcategorías dinámicas. Usando lista vacía.');
        return [];
        
      } else {
        print('Categoría ID $id no mapeada para subcategorías especializadas. Usando lista vacía.');
        return [];
      }
    } catch (e) {
      print('ERROR al cargar subcategorías para ID $id: $e');
      rethrow; 
    }
  }

  // 💡 Lógica de redirección a ProductListWidget (Sin Cambios)
  void _handleSubcategoryClick(BuildContext context, String title, List<int> productIds) {
    print('Evento Click: Navegando a productos de $title con ${productIds.length} IDs.');
    
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ProductListWidget(productIds: productIds, categoryId: widget.categoryId),
      ),
    );
  }
  
  // 💡 Lógica para manejar el onTap de la barra de navegación persistente
  void _handleNavBarTap(int index) {
      // 1. Desapila todas las rutas hasta la raíz (HomeScreen)
      Navigator.of(context).popUntil((route) => route.isFirst);
      
      // 2. Si el índice no es 0 (Portada), se necesitaría un mecanismo (Provider o GlobalKey) 
      // para cambiar la pestaña en HomeScreen. Como esto es complejo desde una ruta apilada,
      // por ahora, solo aseguramos que regrese a la Portada.
      if (index != 0) {
        // Aquí iría la lógica para cambiar el índice en HomeScreen
        print('Navegación global solicitada a índice $index.');
      }
  }


  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.categoryName),
        actions: const [
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
                title: widget.categoryName + ' (General)',
                icon: '🛒',
                color: colorScheme.primaryContainer,
                onTap: () => _handleSubcategoryClick(
                  context, 
                  'Genérica / Mix', 
                  widget.categoryProductIds 
                ),
              ),
              
              const SizedBox(height: 16),
              
              // --- 4. Cards Dinámicas (Subcategorías) ---
              FutureBuilder<List<Subcategory>>(
                future: _subcategoriesFuture,
                builder: (context, snapshot) {
                  if (snapshot.connectionState == ConnectionState.waiting) {
                    return const Center(child: CircularProgressIndicator());
                  }
                  
                  if (snapshot.hasError) {
                    return Center(
                      child: Text('Error al cargar subcategorías: ${snapshot.error}', 
                                   style: TextStyle(color: colorScheme.error)),
                    );
                  }
                  
                  final List<Subcategory> subcategories = snapshot.data ?? [];
                  
                  if (subcategories.isEmpty && widget.categoryId != SUPPLEMENT_CATEGORY_ID) {
                      return Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Text(
                          'No hay subcategorías especializadas disponibles.',
                          style: Theme.of(context).textTheme.bodyMedium,
                        ),
                      );
                  }
                  
                  return Column(
                    children: subcategories.map((subcategory) {
                      return Padding(
                        padding: const EdgeInsets.only(bottom: 8.0),
                        child: SubcategoryCard(
                          title: subcategory.name,
                          icon: subcategory.imageUrl.isNotEmpty ? subcategory.imageUrl : '📦', 
                          color: colorScheme.surfaceContainer, 
                          onTap: () => _handleSubcategoryClick(
                            context, 
                            subcategory.name, 
                            subcategory.ids 
                          ),
                        ),
                      );
                    }).toList(),
                  );
                },
              ),
            ],
          ),
        ),
      ),
      
      // 🔑 Barra de navegación funcional persistente
      bottomNavigationBar: BottomNavBarWidget(
        // Asumimos que la navegación es desde la portada (índice 0)
        currentIndex: 0,
        onTap: _handleNavBarTap, // Usamos la nueva función que desapila la ruta
      ),
    );
  }
}