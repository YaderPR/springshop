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
  // 1. Declarar la variable para el Future
  late Future<List<Category>> _categoriesFuture;

  // 2. Variable para almacenar la lista real de categorías
  List<Category> _categories = [];

  // 3. Estado de la categoría seleccionada
  String? _selectedCategoryId;

  @override
  void initState() {
    super.initState();
    // 💡 IMPORTANTE: La inicialización del Future debe hacerse aquí,
    // pero la llamada a 'context.read' debe hacerse en didChangeDependencies.
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // 4. Inicializar el Future llamando al repositorio (usando context.read)
    // Usamos context.read para evitar escuchar cambios si no es necesario.
    _categoriesFuture = context.read<CategoryRepository>().getCategories();
  }

  void _handleCategoryClick(String categoryId) {
    setState(() {
      _selectedCategoryId = categoryId;
    });
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const ProductListWidget()),
    );
    // 🚀 PROTOTIPO DE LÓGICA DE EVENTO
    print('✅ CATEGORY CLICK EVENT: El ID seleccionado es: $categoryId');

    // 💡 FUTURO: Aquí es donde se invocaría un UseCase o Notifier
    // para notificar al sistema sobre el cambio de categoría,
    // por ejemplo, para cargar productos relacionados.
    // context.read<ProductNotifier>().loadProductsByCategory(categoryId);
  }

  @override
  Widget build(BuildContext context) {
    final screenHeight = MediaQuery.of(context).size.height;
    final colorScheme = Theme.of(context).colorScheme;

    return Container(
      height: screenHeight / 2,
      // El color de fondo debería usar el colorScheme
      color: colorScheme.surface,
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

          // 5. Usar FutureBuilder para manejar los estados de la solicitud
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
                if (snapshot.connectionState != ConnectionState.done) {
                  return const Center(child: CircularProgressIndicator());
                }

                // Estado 3: Datos listos
                if (snapshot.hasData && snapshot.data!.isNotEmpty) {
                  // Guardar los datos y establecer la primera categoría como seleccionada
                  if (_categories.isEmpty) {
                    _categories = snapshot.data!;
                    _selectedCategoryId = _categories.first.id;
                  }

                  return SingleChildScrollView(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: Wrap(
                      spacing: 20.0,
                      runSpacing: 16.0,
                      alignment: WrapAlignment.start,
                      children: _categories.map((category) {
                        return CategoryItemWidget(
                          category: category,
                          isSelected: _selectedCategoryId == category.id,
                          onCategoryTap: _handleCategoryClick,
                        );
                      }).toList(),
                    ),
                  );
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
