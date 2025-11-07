// lib/src/features/categories/presentation/widgets/categories_list_widget.dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/categories/domain/entities/category.dart';
import 'package:springshop/src/features/categories/domain/repositories/category_repository.dart';
import 'package:springshop/src/features/categories/presentation/widgets/category_item_widget.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_list_widget.dart';

class CategoriesListWidget extends StatefulWidget {
  const CategoriesListWidget({super.key});

  @override
  State<CategoriesListWidget> createState() => _CategoriesListWidgetState();
}

class _CategoriesListWidgetState extends State<CategoriesListWidget> {
  late Future<List<Category>> _categoriesFuture;

  // ⚠️ La lista de categorías (List<Category>) se sigue guardando aquí, 
  // ya que se usa *fuera* del FutureBuilder (en _handleCategoryClick)
  List<Category> _categories = []; 

  String? _selectedCategoryId;

  @override
  void initState() {
    super.initState();
    // 💡 BUENA PRÁCTICA: Inicializar el Future en initState.
    _categoriesFuture = context.read<CategoryRepository>().getCategories();
    // ⚠️ ELIMINADO: didChangeDependencies ya no es necesario.
  }

  void _handleCategoryClick(String categoryId) {
    setState(() {
      _selectedCategoryId = categoryId;
    });

    // 🔑 CORRECCIÓN CRÍTICA Y LÓGICA: 
    // 1. Encontrar el objeto Category real usando el ID de tipo String.
    final selectedCategory = _categories.firstWhere(
      (cat) => cat.id == categoryId,
      orElse: () => throw Exception('Categoría con ID $categoryId no encontrada.'),
    );

    // 2. Navegar, pasando la lista de IDs de producto
    Navigator.push(
      context,
      MaterialPageRoute(
          builder: (context) => ProductListWidget(
                // 💡 Se accede de forma segura a la lista de IDs de producto
                productIds: selectedCategory.productIds as List<int>, 
              )),
    );
    print('✅ CATEGORY CLICK EVENT: El ID seleccionado es: $categoryId con ${selectedCategory.productIds?.length} productos.');
  }
  
  // 💡 Función de ayuda para construir el contenido de la lista (Mejor legibilidad)
  Widget _buildCategoryContent(List<Category> categories, ColorScheme colorScheme) {
    // 💡 Inicializar la selección: Solo si no hay selección previa y la lista no está vacía.
    if (_selectedCategoryId == null && categories.isNotEmpty) {
        _selectedCategoryId = categories.first.id;
    }
    
    return SingleChildScrollView(
        padding: const EdgeInsets.symmetric(horizontal: 16.0),
        child: Wrap(
          spacing: 20.0,
          runSpacing: 16.0,
          alignment: WrapAlignment.start,
          children: categories.map((category) {
            return CategoryItemWidget(
              category: category,
              isSelected: _selectedCategoryId == category.id,
              onCategoryTap: _handleCategoryClick,
            );
          }).toList(),
        ),
      );
  }

  @override
  Widget build(BuildContext context) {
    final screenHeight = MediaQuery.of(context).size.height;
    final colorScheme = Theme.of(context).colorScheme;

    return Container(
      height: screenHeight / 2,
      color: Colors.black,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.only(
              left: 16.0,
              top: 16.0,
              right: 16.0,
              bottom: 8.0,
            ),
            child: Text(
              'Compra por categorías',
              style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                color: colorScheme.onSurface,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          Expanded(
            child: FutureBuilder<List<Category>>(
              future: _categoriesFuture,
              builder: (context, snapshot) {
                // Estado 1: Error
                if (snapshot.hasError) {
                  return Center(
                    child: Text(
                      'Error al cargar categorías: ${snapshot.error}',
                      textAlign: TextAlign.center,
                      style: TextStyle(color: colorScheme.error),
                    ),
                  );
                }

                // Estado 2: Cargando
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }

                // Estado 3: Datos listos
                if (snapshot.hasData && snapshot.data!.isNotEmpty) {
                  // ⚠️ CORRECCIÓN: Guardar los datos SÓLO si es la primera vez que llegan.
                  // Esto previene sobrescribir si hay una reconstrucción de setState.
                  if (_categories.isEmpty) {
                    _categories = snapshot.data!;
                  }

                  return _buildCategoryContent(snapshot.data!, colorScheme);
                }

                // Estado 4: Sin datos
                return Center(
                  child: Text(
                    'No hay categorías disponibles.',
                    style: TextStyle(
                      color: colorScheme.onSurface.withOpacity(0.6),
                    ),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}