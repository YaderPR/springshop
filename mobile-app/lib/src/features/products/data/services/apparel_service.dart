// lib/src/features/products/data/services/apparel_service.dart

import 'package:dio/dio.dart'; // Para referencia al 404
import 'package:springshop/src/features/products/data/services/product_service.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/repositories/apparel_repository.dart';

class ApparelService {
  final ApparelRepository _apparelRepository;
  final ProductService _productService; 
  
  ApparelService(this._apparelRepository, this._productService);

  Future<List<Product>> getApparelsByIds(List<int> ids) async {
    
    List<Future<Product?>> productFutures = ids.map((id) async {
      
      try {
        // 1. Intentar cargar el producto especializado (Apparel)
        final apparel = await _apparelRepository.findById(id);
        print('✅ Producto especializado ID $id cargado con éxito.');
        return apparel; // Devuelve el Apparel (que es subtipo de Product)
        
      } catch (error) {
        // 2. Fallo en la carga especializada (error puede ser 404, parsing, etc.)
        final isNotFound = error.toString().contains('404');
        
        if (isNotFound) {
          print('⚠️ Fallo 404 para Apparel ID $id. Intentando fallback genérico...');
          
          try {
            // 3. Intento de Fallback con ProductService genérico (aislado en su propio try-catch)
            List<Product> genericProducts = await _productService.getProductsByIds([id]);
            
            if (genericProducts.isNotEmpty) {
              print('💡 Producto genérico ID $id cargado como fallback.');
              return genericProducts.first; // Retorna Product
            }
          } catch (e) {
            // 4. Captura cualquier error que ocurra durante la LLAMADA DE FALLBACK
            print('❌ El fallback genérico para ID $id FALLÓ (Error: $e).');
          }
        }
        
        // 5. Si la carga especializada no era 404 O el fallback falló/no encontró nada, retornamos null.
        print('❌ Producto ID $id descartado (Error inicial: $error).');
        return null;
      }
      
    }).toList();

    // El Future.wait ahora opera sobre un Future<Product?> donde cada elemento 
    // está protegido por un try-catch.
    List<Product?> productsWithNulls = await Future.wait(productFutures);
    
    // 6. Filtramos y devolvemos solo productos válidos
    return productsWithNulls.whereType<Product>().toList();
  }
}