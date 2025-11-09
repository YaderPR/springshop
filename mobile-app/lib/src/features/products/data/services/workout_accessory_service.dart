// lib/src/features/products/data/services/workout_accessory_service.dart

import 'package:springshop/src/features/products/data/services/product_service.dart'; // 💡 Necesario para el fallback genérico
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/repositories/workout_accessory_repository.dart';

class WorkoutAccessoryService {
  final WorkoutAccessoryRepository _workoutAccessoryRepository;
  final ProductService _productService; // 🔑 Nuevo: Dependencia del servicio genérico
  
  // 🔑 Constructor actualizado
  WorkoutAccessoryService(this._workoutAccessoryRepository, this._productService);
  
  Future<List<Product>> getWorkoutAccessoriesByIds(List<int> ids) async {
    
    // Convertimos cada ID en una Future<Product?> protegida por try-catch
    List<Future<Product?>> productFutures = ids.map((id) async {
      
      try {
        // 1. Intentar cargar el producto especializado (WorkoutAccessory)
        final accessory = await _workoutAccessoryRepository.findById(id);
        print('✅ Accesorio ID $id cargado con éxito.');
        return accessory; // Devuelve WorkoutAccessory (que es subtipo de Product)
        
      } catch (error) {
        // 2. Fallo en la carga especializada (e.g., 404, parsing, etc.)
        final isNotFound = error.toString().contains('404');
        
        if (isNotFound) {
          print('⚠️ Fallo 404 para Accesorio ID $id. Intentando fallback genérico...');
          
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
        print('❌ Accesorio ID $id descartado (Error inicial: $error).');
        return null;
      }
      
    }).toList();

    // Esperamos que todas las futures terminen (algunas serán null)
    List<Product?> productsWithNulls = await Future.wait(productFutures);
    
    // 6. Filtramos y devolvemos solo productos válidos
    return productsWithNulls.whereType<Product>().toList();
  }
}