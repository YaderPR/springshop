// lib/src/features/products/data/repositories/product_api_repository.dart

import 'package:dio/dio.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/repositories/product_repository.dart';

class ProductApiRepository implements ProductRepository {
  final Dio _dioClient;
  ProductApiRepository(this._dioClient);
  static final productPath = "/products";

  @override
  Future<Product> findById(int id) async {
    final fullUrl = "$productPath/$id";
    try {
      final response = await _dioClient.get(fullUrl);
      
      if (response.statusCode == 200 && response.data != null) {
        final jsonData = response.data;
        
        // 🔑 Mapeo de Producto: Usando conversiones seguras
        return Product(
          id: jsonData["id"].toString(),
          name: jsonData["name"] as String,
          imageUrl: jsonData["imageUrl"] as String,
          description: jsonData["description"] as String,
          
          // Conversiones robustas para números (JSON puede devolver int o double)
          stock: (jsonData["stock"] is num) ? (jsonData["stock"] as num).toInt() : 0,
          price: (jsonData["price"] is num) ? (jsonData["price"] as num).toDouble() : 0.0,
          
          categoryId: jsonData["categoryId"].toString(),
          categoryName: jsonData["categoryName"] as String,
        );
      } else {
        // Lanzar DioException si la respuesta no es 200 y no hay un error manejado
        throw DioException(
          requestOptions: response.requestOptions,
          response: response,
          error: 'Error al obtener producto: Status ${response.statusCode}',
        );
      }
    } on DioException catch (e) {
      // Captura y relanza errores de Dio (404, 500, timeouts)
      throw Exception('Fallo en la solicitud GET de producto ID $id: ${e.message}');
    } catch (e) {
      // Otros errores (parsing, cast, etc.)
      throw Exception('Error desconocido al procesar producto ID $id: $e');
    }
  }
  @override
  Future<List<Product>> findAll() async {
    try {
      final response = await _dioClient.get(productPath);
      
      if (response.statusCode == 200 && response.data is List) {
        final List<dynamic> jsonList = response.data;
        List<Product> products = [];

        for (var productJson in jsonList) {
          try {
             products.add(
                Product(
                  id: productJson["id"].toString(),
                  name: productJson["name"] as String,
                  description: productJson["description"] as String,
                  price: (productJson["price"] is num) ? (productJson["price"] as num).toDouble() : 0.0,
                  stock: (productJson["stock"] is num) ? (productJson["stock"] as num).toInt() : 0,
                  imageUrl: productJson["imageUrl"] as String,
                  categoryId: productJson["categoryId"].toString(),
                  categoryName: productJson["categoryName"] as String,
                ),
              );
          } catch (e) {
              print('⚠️ Advertencia: Error de mapeo en un producto durante findAll(): $e');
              continue;
          }
        }
        
        return products;
        
      } else {
         throw DioException(
          requestOptions: response.requestOptions,
          response: response,
          error: 'Error al obtener la lista de productos: Status ${response.statusCode}',
        );
      }
    } on DioException catch (e) {
      throw Exception('Fallo en la solicitud GET de findAll: ${e.message}');
    } catch (e) {
      throw Exception('Error desconocido al procesar findAll: $e');
    }
  }
}