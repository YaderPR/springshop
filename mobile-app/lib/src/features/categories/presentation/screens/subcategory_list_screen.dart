// lib/src/features/categories/presentation/screens/subcategory_list_screen.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/categories/domain/entities/subcategory.dart';
import 'package:springshop/src/features/categories/presentation/widgets/subcategory_card.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_list_widget.dart';

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
  // Estado para manejar la lista de subcategorías cargadas
  late Future<List<Subcategory>> _subcategoriesFuture;

  @override
  void initState() {
    super.initState();
    // Inicia la carga de datos al inicializar el estado
    _subcategoriesFuture = _fetchSubcategories();
  }
  
  // 🚀 Lógica Central para Obtener Subcategorías
  Future<List<Subcategory>> _fetchSubcategories() async {
    // 1. Determinar qué servicio usar (si aplica)
    final int id = widget.categoryId;
    
    try {
      if (id == APPAREL_CATEGORY_ID) {
        print('Cargando subcategorías de Apparel (ID $APPAREL_CATEGORY_ID) desde API.');
        // Usar context.read<T>() para acceder a la instancia del repositorio
        final repository = context.read<ApparelCategoryRepository>();
        
        return repository.getCategories();
        
      } else if (id == WORKOUT_ACCESSORY_CATEGORY_ID) {
        print('Cargando subcategorías de Workout Accessory (ID $WORKOUT_ACCESSORY_CATEGORY_ID) desde API.');
        final repository = context.read<AccessoryCategoryRepository>();
        return repository.getCategories();

      // ⚠️ CASO ESPECIAL: SUPPLEMENTS y otras categorías Genéricas
      } else if (id == SUPPLEMENT_CATEGORY_ID) { 
         print('SUPPLEMENTS (ID $SUPPLEMENT_CATEGORY_ID) no tiene subcategorías dinámicas. Usando lista vacía.');
         return [];
         
      } else {
        // Para cualquier otra categoría no mapeada o genérica
        print('Categoría ID $id no mapeada para subcategorías especializadas. Usando lista vacía.');
        return [];
      }
    } catch (e) {
      print('ERROR al cargar subcategorías para ID $id: $e');
      // Puedes lanzar el error para mostrarlo en el FutureBuilder o retornar una lista vacía
      rethrow; 
    }
  }

  // 💡 Lógica de redirección a ProductListWidget
  void _handleSubcategoryClick(BuildContext context, String title, List<int> productIds) {
    print('Evento Click: Navegando a productos de $title con ${productIds.length} IDs.');
    
    // Redirección al ProductListWidget, pasándole los IDs correspondientes
    // NOTA: No pasamos categoryId aquí, ya que el ProductListWidget debe usar
    // los IDs de producto que le proporcione la subcategoría/categoría genérica.
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ProductListWidget(productIds: productIds, categoryId: widget.categoryId),
      ),
    );
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
                  // Usar los IDs recibidos de la Categoría Principal para el Mix
                  widget.categoryProductIds 
                ),
              ),
              
              const SizedBox(height: 16),
              
              // --- 4. Cards Dinámicas (Subcategorías) ---
              // Usar FutureBuilder para mostrar la lista de subcategorías
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
                      // Si no hay subcategorías (y no es Supplements)
                      return Padding(
                        padding: const EdgeInsets.all(16.0),
                        child: Text(
                          'No hay subcategorías especializadas disponibles.',
                          style: Theme.of(context).textTheme.bodyMedium,
                        ),
                      );
                  }
                  
                  // Mostrar las subcategorías obtenidas de la API
                  return Column(
                    children: subcategories.map((subcategory) {
                      return Padding(
                        padding: const EdgeInsets.only(bottom: 8.0),
                        child: SubcategoryCard(
                          title: subcategory.name,
                          // Usar un placeholder si imageUrl es null o vacío
                          icon: subcategory.imageUrl.isNotEmpty ? subcategory.imageUrl : '📦', 
                          color: colorScheme.surfaceContainer, 
                          onTap: () => _handleSubcategoryClick(
                            context, 
                            subcategory.name, 
                            subcategory.ids // Pasa los IDs de producto de la subcategoría
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