// lib/src/features/categories/presentation/widgets/categories_list_widget.dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/categories/domain/entities/category.dart';
import 'package:springshop/src/features/categories/domain/repositories/category_repository.dart';
import 'package:springshop/src/features/categories/presentation/widgets/category_item_widget.dart';
import 'package:springshop/src/features/categories/presentation/screens/subcategory_list_screen.dart';


class CategoriesListWidget extends StatefulWidget {
  const CategoriesListWidget({super.key});

  @override
  State<CategoriesListWidget> createState() => _CategoriesListWidgetState();
}

class _CategoriesListWidgetState extends State<CategoriesListWidget> {
  late Future<List<Category>> _categoriesFuture;
  List<Category> _categories = []; 
  String? _selectedCategoryId;

  @override
  void initState() {
    super.initState();
    _categoriesFuture = context.read<CategoryRepository>().getCategories();
  }

  // 🔑 LÓGICA DE CLICK ACTUALIZADA
  void _handleCategoryClick(String categoryId) {
    setState(() {
      _selectedCategoryId = categoryId;
    });

    // 1. Encontrar el objeto Category real usando el ID.
    final selectedCategory = _categories.firstWhere(
      (cat) => cat.id == categoryId,
      orElse: () => throw Exception('Categoría con ID $categoryId no encontrada.'),
    );
    final int categoryIdAsInt = int.parse(selectedCategory.id);
    final List<int> productIdsAsIntList = selectedCategory.productIds as List<int>;
    // 2. Navegar a la pantalla de Subcategorías, pasando el NOMBRE de la categoría
    // (Ej. "Ropa", "Electrónica").
    Navigator.push(
      context,
      MaterialPageRoute(
          builder: (context) => SubcategoryListScreen(
              categoryId: categoryIdAsInt,
              categoryName: selectedCategory.name,
              categoryProductIds: productIdsAsIntList,
          )),
    );
    print('✅ CATEGORY CLICK EVENT: Navegando a Subcategorías de: ${selectedCategory.name}');
  }
  
  // 💡 Función de ayuda para construir el contenido de la lista (Sin cambios)
  Widget _buildCategoryContent(List<Category> categories, ColorScheme colorScheme) {
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
                if (snapshot.hasError) {
                  return Center(
                    child: Text(
                      'Error al cargar categorías: ${snapshot.error}',
                      textAlign: TextAlign.center,
                      style: TextStyle(color: colorScheme.error),
                    ),
                  );
                }

                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                }

                if (snapshot.hasData && snapshot.data!.isNotEmpty) {
                  if (_categories.isEmpty) {
                    _categories = snapshot.data!;
                  }
                  return _buildCategoryContent(snapshot.data!, colorScheme);
                }

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