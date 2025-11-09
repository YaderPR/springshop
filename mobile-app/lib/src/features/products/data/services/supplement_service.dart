// lib/src/features/products/data/services/supplement_service.dart

import 'package:springshop/src/features/products/data/services/product_service.dart'; // 💡 Necesario para el fallback genérico
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/repositories/supplement_repository.dart';

class SupplementService {
  final SupplementRepository _supplementRepository;
  final ProductService _productService; // 🔑 Nuevo: Dependencia del servicio genérico
  
  // 🔑 Constructor actualizado
  SupplementService(this._supplementRepository, this._productService);

  Future<List<Product>> getSupplementsByIds(List<int> ids) async {
    
    // Convertimos cada ID en una Future<Product?> protegida por try-catch
    List<Future<Product?>> productFutures = ids.map((id) async {
      
      try {
        // 1. Intentar cargar el producto especializado (Supplement)
        final supplement = await _supplementRepository.findById(id);
        print('✅ Suplemento ID $id cargado con éxito.');
        return supplement; // Devuelve Supplement (que es subtipo de Product)
        
      } catch (error) {
        // 2. Fallo en la carga especializada (e.g., 404, parsing, etc.)
        final isNotFound = error.toString().contains('404');
        
        if (isNotFound) {
          print('⚠️ Fallo 404 para Suplemento ID $id. Intentando fallback genérico...');
          
          try {
            // 3. Intento de Fallback con ProductService genérico
            List<Product> genericProducts = await _productService.getProductsByIds([id]);
            
            if (genericProducts.isNotEmpty) {
              print('💡 Producto genérico ID $id cargado como fallback.');
              return genericProducts.first; // Retorna Product
            }
          } catch (e) {
            // 4. Captura errores internos del fallback
            print('❌ El fallback genérico para ID $id FALLÓ (Error: $e).');
          }
        }
        
        // 5. Si la carga falló, retornamos null
        print('❌ Suplemento ID $id descartado (Error inicial: $error).');
        return null;
      }
      
    }).toList();

    // Esperamos que todas las futures terminen (algunas serán null)
    List<Product?> productsWithNulls = await Future.wait(productFutures);
    
    // 6. Filtramos y devolvemos solo productos válidos
    return productsWithNulls.whereType<Product>().toList();
  }
}