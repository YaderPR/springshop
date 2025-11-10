// lib/src/features/search/presentation/widgets/global_product_search_widget.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/products/data/services/product_service.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/presentation/screens/product_detail_screen.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_list_item_widget.dart';

class GlobalProductSearchWidget extends StatefulWidget {
  // 🔑 Término de búsqueda que se pasa desde el AppBar/SearchScreen
  final String searchTerm;

  const GlobalProductSearchWidget({
    super.key,
    required this.searchTerm,
  });

  @override
  State<GlobalProductSearchWidget> createState() => _GlobalProductSearchWidgetState();
}

class _GlobalProductSearchWidgetState extends State<GlobalProductSearchWidget> {
  // Lista maestra de todos los productos (se carga una sola vez)
  late Future<List<Product>> _allProductsFuture;
  
  // Lista filtrada basada en el searchTerm
  List<Product> _filteredProducts = [];
  
  // Guardamos la lista completa tras la carga inicial
  List<Product> _masterProductList = [];

  @override
  void initState() {
    super.initState();
    // 1. Cargar la lista completa de productos al inicio
    _allProductsFuture = _fetchProducts();
  }

  // 2. Ejecutar la búsqueda inicial y al actualizar el término
  @override
  void didUpdateWidget(GlobalProductSearchWidget oldWidget) {
    super.didUpdateWidget(oldWidget);
    // Filtrar si el término de búsqueda ha cambiado
    if (oldWidget.searchTerm != widget.searchTerm) {
      _filterProducts(widget.searchTerm);
    }
  }

  // 3. Obtención de todos los productos
  Future<List<Product>> _fetchProducts() async {
    print('📦 Cargando todos los productos genéricos con ProductService.findAll()');
    try {
      final productService = context.read<ProductService>();
      final products = await productService.findAll();
      
      // Guardar la lista maestra y realizar el filtro inicial
      _masterProductList = products;
      _filterProducts(widget.searchTerm, initialLoad: true);
      
      return products;
    } catch (e) {
      print('Error al cargar todos los productos: $e');
      rethrow;
    }
  }
  
  // 4. Lógica de Filtrado Local (Por coincidencia de nombre)
  void _filterProducts(String query, {bool initialLoad = false}) {
    final normalizedQuery = query.trim().toLowerCase();
    
    // Si la lista maestra no está cargada o la búsqueda está vacía, no hacemos nada
    if (_masterProductList.isEmpty && !initialLoad) return;
    
    final List<Product> newFilteredList;

    if (normalizedQuery.isEmpty) {
      // Si la búsqueda está vacía, mostramos todos los productos cargados (o ninguno)
      newFilteredList = _masterProductList;
    } else {
      // Filtrar por coincidencia de nombre (insensible a mayúsculas/minúsculas)
      newFilteredList = _masterProductList.where((product) {
        return product.name.toLowerCase().contains(normalizedQuery);
      }).toList();
    }

    setState(() {
      _filteredProducts = newFilteredList;
    });
  }
  
  // 5. Manejo del tap para navegar a detalles
  void _handleProductTap(Product product) {
    print('Producto con ID ${product.id} clickeado. Navegando a detalles...');
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ProductDetailScreen(product: product),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    // Usamos FutureBuilder solo para la carga inicial de _allProductsFuture
    return FutureBuilder<List<Product>>(
      future: _allProductsFuture,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }

        if (snapshot.hasError) {
          return Center(child: Text('Error: ${snapshot.error}'));
        }
        
        // Si no hay errores, mostramos la lista filtrada actual
        if (_filteredProducts.isEmpty) {
          return Center(
            child: Text(
              widget.searchTerm.isEmpty 
                ? 'Empieza a buscar productos.'
                : 'No se encontraron resultados para "${widget.searchTerm}".',
              style: TextStyle(color: colorScheme.onSurface.withOpacity(0.6)),
            ),
          );
        }

        return ListView.separated(
          padding: const EdgeInsets.symmetric(vertical: 0.0),
          itemCount: _filteredProducts.length,
          separatorBuilder: (context, index) => Divider(
            height: 1,
            color: colorScheme.outline.withOpacity(0.3),
            indent: 16,
            endIndent: 16,
          ),
          itemBuilder: (context, index) {
            final product = _filteredProducts[index];
            return ProductListItemWidget(
              product: product,
              onProductTap: _handleProductTap,
            );
          },
        );
      },
    );
  }
}