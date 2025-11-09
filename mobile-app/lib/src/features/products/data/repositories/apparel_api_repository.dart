import 'package:dio/dio.dart';
import 'package:springshop/src/features/products/domain/entities/apparel.dart';
import 'package:springshop/src/features/products/domain/repositories/apparel_repository.dart';

class ApparelApiRepository implements ApparelRepository {
  final Dio _dioClient;
  ApparelApiRepository(this._dioClient);
  static final productPath = "/products/apparels";
  @override
  Future<Apparel> findById(int id) async {
    try {
      final response = await _dioClient.get("$productPath/$id");
      if (response.statusCode == 200 && response.data != null) {
        final jsonData = response.data;
        return Apparel(
          id: jsonData["id"].toString(),
          name: jsonData["name"],
          imageUrl: jsonData["imageUrl"],
          description: jsonData["description"],
          stock: jsonData["stock"] as int,
          categoryId: jsonData["categoryId"].toString(),
          categoryName: jsonData["categoryName"],
          price: jsonData["price"] as double,
          brand: jsonData["brand"].toString(),
          color: jsonData["color"].toString(),
          apparelCategoryId: jsonData["apparelCategoryId"],
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
      // Manejo de errores de red, timeouts, o errores 401/403 gestionados por AuthInterceptor
      throw Exception('Fallo en la solicitud GET del apparel: ${e.message}');
    } catch (e) {
      // Otros errores (parsing, etc.)
      throw Exception('Error desconocido al procesar apparel: $e');
    }
  }
}