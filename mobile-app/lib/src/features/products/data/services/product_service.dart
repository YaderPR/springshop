// lib/src/features/products/data/services/product_service.dart (CORREGIDO)

import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/repositories/product_repository.dart';

class ProductService {
  final ProductRepository _productRepository;
  
  ProductService(this._productRepository);

  Future<List<Product>> getProductsByIds(List<int> ids) async {
    // 💡 1. Las futures ahora devuelven Product O NULL (Product?)
    List<Future<Product?>> productFutures = ids.map((id) {
      // 2. Intentamos cargar, si falla, capturamos y devolvemos null
      return _productRepository.findById(id).then((product) {
        return product;
      }).catchError((e) {
        print('❌ Advertencia (Producto Genérico): Fallo al cargar Producto ID $id. Error: $e');
        return null; // Devolvemos null para que el Future.wait no falle
      });
    }).toList();

    try {
      // 3. Esperamos y recibimos una lista con nulos si hubo fallos
      List<Product?> productsWithNulls = await Future.wait(productFutures);
      
      // 4. Filtramos los nulos
      List<Product> products = productsWithNulls
          .whereType<Product>()
          .toList();
      
      if (products.isEmpty && ids.isNotEmpty) {
        print('⚠️ Advertencia: Ningún producto genérico pudo ser cargado.');
      }
      
      return products;

    } on Exception catch (e) {
      // Este catch es el catch de último recurso
      print('Error grave en Future.wait de ProductService: $e'); 
      return []; 
    }
  }
  Future<List<Product>> findAll() async {
    return await _productRepository.findAll();
  }
}