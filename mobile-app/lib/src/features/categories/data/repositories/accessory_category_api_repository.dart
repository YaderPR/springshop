import 'package:dio/dio.dart';
import 'package:springshop/src/features/categories/domain/entities/subcategory.dart';
import 'package:springshop/src/features/categories/domain/repositories/accessory_category_repository.dart';

class AccessoryCategoryApiRepository implements AccessoryCategoryRepository {
  final Dio _dioClient;
  static const String _categoriesPath = '/products/workoutaccessories/categories';
  AccessoryCategoryApiRepository(this._dioClient);
  @override
  Future<List<Subcategory>> getCategories() async {
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
          final List<int> productIdsList = (json['accessoryIds'] as List<dynamic>?)
            // Si la lista de IDs es nula o falta, usamos una lista vacía
            ?.map((e) => e as int)
            .toList() ?? []; 
            
          // Asumo que el JSON tiene 'id', 'name' e 'imageUrl'
          return Subcategory(
            // El ID a veces llega como int o puede ser nulo, .toString() es robusto
            id: json['id']?.toString() ?? '', 
            name: json['name'] as String,
            imageUrl: json['imageUrl'] as String,
            ids: productIdsList, // Asignación segura de List<int>
          );
        }).toList();
        
      } else {
        // Manejo de códigos de estado no exitosos
        // 💡 MEJORA: Incluir el response.data en el error para mejor diagnóstico
        throw DioException(
          requestOptions: response.requestOptions,
          response: response,
          error: 'Error al obtener subcategorías: Status ${response.statusCode}. Data: ${response.data}',
        );
      }
    } on DioException catch (e) {
      // Manejo de errores de red, timeouts, etc.
      throw Exception('Fallo en la solicitud GET de subcategorías: ${e.message}');
    } catch (e) {
      // Otros errores (parsing, etc.)
      throw Exception('Error desconocido al procesar subcategorías: $e');
    }
  }
}