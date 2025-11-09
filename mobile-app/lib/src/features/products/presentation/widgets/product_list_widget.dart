// lib/src/features/products/presentation/widgets/product_list_widget.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/products/data/services/apparel_service.dart';
import 'package:springshop/src/features/products/data/services/supplement_service.dart'; 
import 'package:springshop/src/features/products/data/services/workout_accessory_service.dart'; 
import 'package:springshop/src/features/products/data/services/product_service.dart'; 
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_list_item_widget.dart';
import 'package:springshop/src/features/products/presentation/screens/product_detail_screen.dart';

// IDs de Categoría Globales (Se asume que son enteros)
const int WORKOUT_ACCESSORY_CATEGORY_ID = 3;
const int APPAREL_CATEGORY_ID = 4;
const int SUPPLEMENT_CATEGORY_ID = 5;

class ProductListWidget extends StatefulWidget {
  final List<int> productIds;
  final int? categoryId; 

  const ProductListWidget({
    super.key,
    required this.productIds,
    this.categoryId
  });

  @override
  State<ProductListWidget> createState() => _ProductListWidgetState();
}

class _ProductListWidgetState extends State<ProductListWidget> {
  late Future<List<Product>> _productsFuture;

  @override
  void initState() {
    super.initState();
    _productsFuture = _fetchProducts();
  }
  
  Future<List<Product>> _fetchProducts() async {
    final int? filterCategoryId = widget.categoryId;
    
    // 1. Determinar y usar el servicio especializado o genérico
    if (filterCategoryId == APPAREL_CATEGORY_ID) {

      print('🛒 Usando ApparelService (ID $APPAREL_CATEGORY_ID).');
      final apparelService = context.read<ApparelService>();
      // Los IDs ya están filtrados por subcategoría o son el mix completo de Apparel.
      return await apparelService.getApparelsByIds(widget.productIds);
    
    } else if (filterCategoryId == SUPPLEMENT_CATEGORY_ID) {

      print('💊 Usando SupplementService (ID $SUPPLEMENT_CATEGORY_ID).');
      final supplementService = context.read<SupplementService>();
      return await supplementService.getSupplementsByIds(widget.productIds);
      
    } else if (filterCategoryId == WORKOUT_ACCESSORY_CATEGORY_ID) {

      print('⚙️ Usando WorkoutAccessoryService (ID $WORKOUT_ACCESSORY_CATEGORY_ID).');
      final accessoryService = context.read<WorkoutAccessoryService>();
      return await accessoryService.getWorkoutAccessoriesByIds(widget.productIds);

    } else {
      // 2. Usar el servicio genérico para cualquier otro caso
      print('📦 Usando ProductService para obtener productos genéricos (ID: $filterCategoryId).');
      final productService = context.read<ProductService>();
      return await productService.getProductsByIds(widget.productIds);
    }
    
    // NOTA: Se eliminó la lógica de filtrado por categoryId (product.categoryId == filterId)
    // porque es innecesaria y potencialmente incorrecta, ya que los IDs que recibimos
    // (widget.productIds) ya representan la lista final de productos deseada.
  }

  void _handleProductTap(Product product) {
    print('Producto con ID ${product.id} clickeado. Navegando a detalles...');
    
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ProductDetailScreen(product: product),
      ),
    );
  }

  void _openFilterBottomSheet() {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        final colorScheme = Theme.of(context).colorScheme;
        return Container(
          height: 300,
          padding: const EdgeInsets.all(20.0),
          color: colorScheme.surface,
          child: Column(
            children: [
              Text(
                'Opciones de Filtro',
                style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    color: colorScheme.onSurface,
                    fontWeight: FontWeight.bold,
                  ),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: () {
                  Navigator.pop(context);
                  print('Filtros aplicados (simulado)');
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: colorScheme.primary,
                  foregroundColor: colorScheme.onPrimary,
                ),
                child: const Text('Aplicar Filtros'),
              ),
            ],
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      appBar: AppBar(
        title: Text('Productos (${widget.productIds.length} encontrados)'),
        backgroundColor: colorScheme.surface,
        foregroundColor: colorScheme.onSurface,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.search),
            onPressed: () => print('Abrir búsqueda'),
          ),
          IconButton(
            icon: const Icon(Icons.camera_alt_outlined),
            onPressed: () => print('Abrir cámara'),
          ),
          TextButton.icon(
            onPressed: _openFilterBottomSheet,
            icon: Icon(Icons.filter_alt, color: colorScheme.primary),
            label: Text(
              'Filtrar',
              style: TextStyle(color: colorScheme.primary, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
      body: Container(
        color: colorScheme.surface,
        child: FutureBuilder<List<Product>>(
          future: _productsFuture,
          builder: (context, snapshot) {

            if (snapshot.hasError) {
              return Center(
                child: Padding(
                  padding: const EdgeInsets.all(24.0),
                  child: Text(
                    'Error al cargar los productos: ${snapshot.error}.',
                    textAlign: TextAlign.center,
                    style: TextStyle(color: colorScheme.error),
                  ),
                ),
              );
            }

            if (snapshot.connectionState == ConnectionState.waiting) {
              return const Center(child: CircularProgressIndicator());
            }

            if (snapshot.hasData && snapshot.data!.isNotEmpty) {
              final List<Product> products = snapshot.data!;
              return ListView.separated(
                padding: const EdgeInsets.symmetric(vertical: 0.0),
                itemCount: products.length,
                separatorBuilder: (context, index) => Divider(
                  height: 1,
                  color: colorScheme.outline.withOpacity(0.3),
                  indent: 16,
                  endIndent: 16,
                ),
                itemBuilder: (context, index) {
                  final product = products[index];
                  return ProductListItemWidget(
                    product: product,
                    onProductTap: _handleProductTap, 
                  );
                },
              );
            }

            return Center(
              child: Text(
                'No se encontraron productos para los IDs proporcionados.',
                style: TextStyle(color: colorScheme.onSurface.withOpacity(0.6)),
              ),
            );
          },
        ),
      ),
    );
  }
}