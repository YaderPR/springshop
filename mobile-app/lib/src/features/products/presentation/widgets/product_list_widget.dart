// lib/src/features/products/presentation/widgets/product_list_widget.dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/products/data/services/product_service.dart'; 
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_list_item_widget.dart';
// 🔑 Importar la pantalla de detalles que creamos antes
import 'package:springshop/src/features/products/presentation/screens/product_detail_screen.dart';


class ProductListWidget extends StatefulWidget {
  final List<int> productIds;

  const ProductListWidget({
    super.key,
    required this.productIds, 
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
    // Nota: La ruta de importación del servicio ha sido corregida 
    // a 'application/services/product_service.dart' para seguir la arquitectura Domain/Application/Data/Presentation
    // Si tu servicio está en otra ruta, ajústalo.
    final productService = context.read<ProductService>();
    return productService.getProductsByIds(widget.productIds);
  }

  // 🔑 CAMBIO CLAVE: Ahora recibe el objeto Product completo.
  void _handleProductTap(Product product) {
    print('Producto con ID ${product.id} clickeado. Navegando a detalles...');
    
    // 💡 Implementación de la navegación a la ProductDetailScreen
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
            // ... manejo de estados (Error, Cargando) ...
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

            // Estado: Datos listos
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
                    // 💡 onProductTap ahora envía el objeto product completo
                    onProductTap: _handleProductTap, 
                  );
                },
              );
            }
            
            // Estado: Lista vacía
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