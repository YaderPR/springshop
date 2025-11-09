import 'package:dio/dio.dart';
import 'package:springshop/src/features/products/domain/entities/workout_accessory.dart';
import 'package:springshop/src/features/products/domain/repositories/workout_accessory_repository.dart';

class WorkoutAccessoryApiRepository implements WorkoutAccessoryRepository {
  final Dio _dioClient;
  WorkoutAccessoryApiRepository(this._dioClient);
  static final productPath = "/products/workout-accessories";
  @override
  Future<WorkoutAccessory> findById(int id) async {
    try {
      final response = await _dioClient.get("$productPath/$id");
      if (response.statusCode == 200 && response.data != null) {
        final jsonData = response.data;
        return WorkoutAccessory(
          id: jsonData["id"].toString(),
          name: jsonData["name"],
          imageUrl: jsonData["imageUrl"],
          description: jsonData["description"],
          stock: jsonData["stock"] as int,
          categoryId: jsonData["categoryId"].toString(),
          categoryName: jsonData["categoryName"],
          price: jsonData["price"] as double,
          material: jsonData["material"].toString(),
          dimensions: jsonData["dimensions"].toString(),
          weight: jsonData["weight"].toString(),
          color: jsonData["color"].toString(),
          workoutAccessoryCategoryId: jsonData["workoutAccessoryCategoryId"].toString(),
          workoutAccessoryCategoryName: jsonData["workoutAccessoryCategoryName"].toString()
        );
      } else {
        throw DioException(
          requestOptions: response.requestOptions,
          response: response,
          error: 'Error al obtener workout-accessories: Status ${response.statusCode}',
        );
      }
    } on DioException catch (e) {
      // Manejo de errores de red, timeouts, o errores 401/403 gestionados por AuthInterceptor
      throw Exception('Fallo en la solicitud GET del workout-accessories: ${e.message}');
    } catch (e) {
      // Otros errores (parsing, etc.)
      throw Exception('Error desconocido al procesar workout-accessories: $e');
    }
  }
}