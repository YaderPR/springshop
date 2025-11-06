// lib/src/features/products/presentation/widgets/product_list_widget.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/presentation/widgets/product_list_item_widget.dart';

class ProductListWidget extends StatefulWidget {
  const ProductListWidget({super.key});

  @override
  State<ProductListWidget> createState() => _ProductListWidgetState();
}

class _ProductListWidgetState extends State<ProductListWidget> {
  // Datos mock para los productos
  final List<Product> _mockProducts = const [
    Product(
      id: 'prod1',
      name: '2x Kit de bombillas LED Lasfit 9003 H4 haz alto-bajo 50W 6000K',
      imageUrl: 'https://i.ebayimg.com/images/g/FbsAAOSw4IVijJ41/s-l1600.webp', // Similar a la imagen
      price: 1471.66,
      currency: 'C\$',
    ),
    Product(
      id: 'prod2',
      name: 'YTX5L-BS Alto Rendimiento - Sin Mantenimiento - Batería de moto',
      imageUrl: 'https://m.media-amazon.com/images/I/71MbDeQ7ffL._AC_SL1500_.jpg', // Similar a la imagen
      price: 879.54,
      currency: 'C\$',
    ),
    Product(
      id: 'prod3',
      name: 'Almohadilla de espuma de filtro de aire universal 11,8" x 7,8" 300 mm x 200 mm',
      imageUrl: 'https://m.media-amazon.com/images/I/81iRxhRQiQL._AC_SL1500_.jpg', // Similar a la imagen
      price: 120.00,
      currency: 'C\$',
    ),
    Product(
      id: 'prod4',
      name: 'Monitor de Presión Arterial para Brazo con Medición Automática',
      imageUrl: 'https://m.media-amazon.com/images/I/71YN9zWHaeL._AC_SL1500_.jpg',
      price: 350.25,
      currency: 'C\$',
    ),
    Product(
      id: 'prod5',
      name: 'Auriculares Inalámbricos Bluetooth 5.3 con Cancelación de Ruido',
      imageUrl: 'https://m.media-amazon.com/images/I/614gb9uXQuL._AC_SL1500_.jpg',
      price: 599.99,
      currency: 'C\$',
    ),
  ];

  void _handleProductTap(String productId) {
    // 💡 Aquí se gestionará el evento de clic de un producto.
    // Por ahora, solo imprime el ID. En el futuro, esto se pasaría a un servicio
    // o un UseCase para navegar a la página de detalles del producto.
    print('Producto con ID $productId clickeado.');
    // Ejemplo: Navigator.of(context).push(MaterialPageRoute(builder: (_) => ProductDetailPage(productId: productId)));
  }

  void _openFilterBottomSheet() {
    // 💡 Implementación básica del filtro
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        final colorScheme = Theme.of(context).colorScheme;
        return Container(
          height: 300, // Altura del BottomSheet
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
              // Aquí irían los widgets de filtro (sliders, checkboxes, radios)
              // Por ahora, solo un placeholder.
              ElevatedButton(
                onPressed: () {
                  Navigator.pop(context); // Cierra el BottomSheet
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
        title: const Text('Buscar Productos'),
        backgroundColor: colorScheme.surface, // Fondo de la AppBar
        foregroundColor: colorScheme.onSurface, // Color del texto y íconos
        elevation: 0, // Elimina la sombra de la AppBar
        actions: [
          IconButton(
            icon: const Icon(Icons.search), // Ícono de búsqueda
            onPressed: () {
              print('Abrir búsqueda');
              // Lógica para abrir la búsqueda
            },
          ),
          IconButton(
            icon: const Icon(Icons.camera_alt_outlined), // Ícono de cámara
            onPressed: () {
              print('Abrir cámara');
              // Lógica para abrir la cámara
            },
          ),
          // Botón de filtro
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
        // Color de fondo principal del cuerpo, para que coincida con la imagen de eBay
        color: colorScheme.surface, 
        child: ListView.separated(
          padding: const EdgeInsets.symmetric(vertical: 0.0), // Padding arriba/abajo para la lista
          itemCount: _mockProducts.length,
          separatorBuilder: (context, index) => Divider(
            height: 1, // Altura del divisor
            color: colorScheme.outline.withOpacity(0.3),
            indent: 16, // Indentación izquierda del divisor
            endIndent: 16, // Indentación derecha del divisor
          ),
          itemBuilder: (context, index) {
            final product = _mockProducts[index];
            return ProductListItemWidget(
              product: product,
              onProductTap: _handleProductTap,
            );
          },
        ),
      ),
    );
  }
}