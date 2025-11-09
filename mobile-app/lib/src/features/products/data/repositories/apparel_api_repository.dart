// lib/src/features/products/data/repositories/apparel_api_repository.dart (Código Corregido)

import 'package:dio/dio.dart';
import 'package:springshop/src/features/products/domain/entities/apparel.dart';
import 'package:springshop/src/features/products/domain/repositories/apparel_repository.dart';

class ApparelApiRepository implements ApparelRepository {
  final Dio _dioClient;
  ApparelApiRepository(this._dioClient);
  static final productPath = "/products/apparels";
  

  @override
  Future<Apparel> findById(int id) async {
    // 🔑 LOG CLAVE: Muestra la URL completa que se está llamando.
    final fullUrl = "$productPath/$id";
    print("🚀 API CALL: Intentando GET a $fullUrl para ID $id"); 
    
    try {
      final response = await _dioClient.get(fullUrl);
      
      // La ejecución salta a catch(DioException) si el status code es 404

      if (response.statusCode == 200 && response.data != null) {
        final jsonData = response.data;
        
        // El log de JSONDATA!!! solo se imprimirá aquí si la llamada tiene éxito (Status 200).
        print("ID!!!: $id"); 
        print("JSONDATA!!!: $jsonData"); // Usar solo $jsonData, sin concatenación '+'
        
        // Nota: Mantenemos el mapeo con conversiones seguras por si el 404 se arregla
        // y aparece de nuevo el error de tipos.
        return Apparel(
          // Asumo que tu entidad Apparel tiene estos tipos:
          id: jsonData["id"].toString(), // Si la API devuelve un int, usa as int, no toString()
          name: jsonData["name"] as String,
          imageUrl: jsonData["imageUrl"] as String,
          description: jsonData["description"] as String,
          stock: (jsonData["stock"] is num) ? (jsonData["stock"] as num).toInt() : 0, // Conversión segura
          categoryId: (jsonData["categoryId"]?.toString()) ?? '', // Conversión segura a String
          categoryName: jsonData["categoryName"] as String,
          price: (jsonData["price"] is num) ? (jsonData["price"] as num).toDouble() : 0.0, // Conversión segura
          brand: jsonData["brand"].toString(),
          color: jsonData["color"].toString(),
          apparelCategoryId: (jsonData["apparelCategoryId"]?.toString()) ?? '',
          apparelCategoryName: jsonData["apparelCategoryName"].toString(),
          size: jsonData["size"].toString()
        );

      } else {
        throw DioException(
          requestOptions: response.requestOptions,
          response: response,
          error: 'Error al obtener el apparel: Status ${response.statusCode}',
        );
      }
    } on DioException catch (e) {
      // Este catch es el que se activa con el 404
      throw Exception('Fallo en la solicitud GET del apparel: ${e.message}');
    } catch (e) {
      // Otros errores (parsing, etc.)
      throw Exception('Error desconocido al procesar apparel: $e');
    }
  }
}