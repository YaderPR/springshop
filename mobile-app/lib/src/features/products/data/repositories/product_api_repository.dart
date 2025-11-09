import 'package:dio/dio.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/repositories/product_repository.dart';

class ProductApiRepository implements ProductRepository {
  final Dio _dioClient;
  ProductApiRepository(this._dioClient);
  static final productPath = "/products";
  @override
  Future<Product> findById(int id) async {
    try {
      final response = await _dioClient.get("$productPath/$id");
      if (response.statusCode == 200 && response.data != null) {
        final jsonData = response.data;
        return Product(
          id: jsonData["id"].toString(),
          name: jsonData["name"],
          imageUrl: jsonData["imageUrl"],
          description: jsonData["description"],
          stock: jsonData["stock"] as int,
          categoryId: jsonData["categoryId"].toString(),
          categoryName: jsonData["categoryName"],
          price: jsonData["price"] as double,
        );
      } else {
        throw DioException(
          requestOptions: response.requestOptions,
          response: response,
          error: 'Error al obtener producto: Status ${response.statusCode}',
        );
      }
    } on DioException catch (e) {
      // Manejo de errores de red, timeouts, o errores 401/403 gestionados por AuthInterceptor
      throw Exception('Fallo en la solicitud GET de apparel: ${e.message}');
    } catch (e) {
      // Otros errores (parsing, etc.)
      throw Exception('Error desconocido al procesar apparel: $e');
    }
  }
}
