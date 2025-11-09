import 'package:dio/dio.dart';
import 'package:springshop/src/features/categories/domain/entities/category.dart';
import '../../domain/repositories/category_repository.dart';

class CategoryApiRepository implements CategoryRepository {
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
        // Usamos as List<dynamic>? para manejar un posible nulo
        final List<dynamic> jsonList = response.data as List<dynamic>? ?? [];

        // 4. Mapear de JSON a Entidades Category
        return jsonList.map((json) {
          
          // 🔑 CONVERSIÓN CRÍTICA: Convertir List<dynamic> a List<int> de forma segura
          final List<int> productIdsList = (json['productIds'] as List<dynamic>?)
            // Si la lista de IDs es nula o falta, usamos una lista vacía
            ?.map((e) => e as int)
            .toList() ?? []; 
            
          // Asumo que el JSON tiene 'id', 'name' e 'imageUrl'
          return Category(
            // El ID a veces llega como int o puede ser nulo, .toString() es robusto
            id: json['id']?.toString() ?? '', 
            name: json['name'] as String,
            imageUrl: json['imageUrl'] as String,
            productIds: productIdsList, // Asignación segura de List<int>
          );
        }).toList();
        
      } else {
        // Manejo de códigos de estado no exitosos
        // 💡 MEJORA: Incluir el response.data en el error para mejor diagnóstico
        throw DioException(
          requestOptions: response.requestOptions,
          response: response,
          error: 'Error al obtener categorías: Status ${response.statusCode}. Data: ${response.data}',
        );
      }
    } on DioException catch (e) {
      // Manejo de errores de red, timeouts, etc.
      throw Exception('Fallo en la solicitud GET de categorías: ${e.message}');
    } catch (e) {
      // Otros errores (parsing, etc.)
      throw Exception('Error desconocido al procesar categorías: $e');
    }
  }
}