import 'package:dio/dio.dart';
import 'package:springshop/src/features/categories/domain/entities/category.dart';

import '../../domain/repositories/category_repository.dart';
class CategoryApiRepository implements  CategoryRepository {
  final Dio _dioClient;

  CategoryApiRepository(this._dioClient);

  // Endpoint específico
  static const String _categoriesPath = '/products/categories';
  @override
  Future<List<Category>> getCategories() async {
    try {
      final response = await _dioClient.get(_categoriesPath);

      // 3. Manejar la respuesta
      if (response.statusCode == 200 && response.data != null) {
        // Asegurarse de que la respuesta sea una lista y mapearla
        final List<dynamic> jsonList = response.data;

        // 4. Mapear de JSON a Entidades Category
        return jsonList.map((json) {
          // Asumo que el JSON tiene 'id', 'name' e 'imageUrl'
          return Category(
            id: json['id'].toString(),
            name: json['name'] as String,
            imageUrl: json['imageUrl'] as String,
            productIds: json['productIds'] as List<dynamic>
          );
        }).toList();
      } else {
        // Manejo de códigos de estado no exitosos
        throw DioException(
          requestOptions: response.requestOptions,
          response: response,
          error: 'Error al obtener categorías: Status ${response.statusCode}',
        );
      }
    } on DioException catch (e) {
      // Manejo de errores de red, timeouts, o errores 401/403 gestionados por AuthInterceptor
      throw Exception('Fallo en la solicitud GET de categorías: ${e.message}');
    } catch (e) {
      // Otros errores (parsing, etc.)
      throw Exception('Error desconocido al procesar categorías: $e');
    }
  }
}