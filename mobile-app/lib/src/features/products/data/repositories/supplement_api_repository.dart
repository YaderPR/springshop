import 'package:dio/dio.dart';
import 'package:springshop/src/features/products/domain/entities/supplement.dart';
import 'package:springshop/src/features/products/domain/repositories/supplement_repository.dart';

class SupplementApiRepository implements SupplementRepository {
  final Dio _dioClient;
  SupplementApiRepository(this._dioClient);
  static final productPath = "/products/supplements";
  @override
  Future<Supplement> findById(int id) async {
    try {
      final response = await _dioClient.get("$productPath/$id");
      if (response.statusCode == 200 && response.data != null) {
        final jsonData = response.data;
        return Supplement(
          id: jsonData["id"].toString(),
          name: jsonData["name"],
          imageUrl: jsonData["imageUrl"],
          description: jsonData["description"],
          stock: jsonData["stock"] as int,
          categoryId: jsonData["categoryId"].toString(),
          categoryName: jsonData["categoryName"],
          price: jsonData["price"] as double,
          brand: jsonData["brand"].toString(),
          size: jsonData["size"].toString(),
          warnings: jsonData["warnings"].toString(),
          usageInstructions: jsonData["usageInstructions"].toString(),
          flavor: jsonData["flavor"].toString(),
          ingredients: jsonData["ingredients"].toString()
          
        );
      } else {
        throw DioException(
          requestOptions: response.requestOptions,
          response: response,
          error: 'Error al obtener el supplements: Status ${response.statusCode}',
        );
      }
    } on DioException catch (e) {
      // Manejo de errores de red, timeouts, o errores 401/403 gestionados por AuthInterceptor
      throw Exception('Fallo en la solicitud GET del supplements: ${e.message}');
    } catch (e) {
      // Otros errores (parsing, etc.)
      throw Exception('Error desconocido al procesar supplements: $e');
    }
  }
}