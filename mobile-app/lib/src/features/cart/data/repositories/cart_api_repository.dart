import 'package:dio/dio.dart';
import 'package:springshop/src/features/cart/data/models/cart_dto.dart'; 
import 'package:springshop/src/features/cart/domain/repositories/cart_repository.dart';

class CartApiRepository implements CartRepository {
  final Dio _dio;
  static const String _basePath = '/carts';

  CartApiRepository(this._dio);

  /// 🔑 Función clave: Intenta obtener el carrito, si falla (404/vacío), lo crea.
  @override
  Future<CartResponseDto> getOrCreateCart(int userId) async {
    try {
      // 1. Asumo que tienes un endpoint para buscar por UserID
      final response = await _dio.get('$_basePath/user/$userId');
      return CartResponseDto.fromJson(response.data);
    } on DioException catch (e) {
      if (e.response?.statusCode == 404) {
        // 2. Si el carrito no existe (404), lo creamos
        print('Carrito no encontrado para userId $userId. Creando uno nuevo...');
        final request = CartRequestDto(userId: userId);
        final response = await _dio.post(_basePath, data: request.toJson());
        return CartResponseDto.fromJson(response.data);
      }
      rethrow;
    }
  }

  @override
  Future<CartResponseDto> getCartById(int cartId) async {
    // Endpoint: GET /api/v2/carts/{cartId}
    final response = await _dio.get('$_basePath/$cartId');
    return CartResponseDto.fromJson(response.data);
  }

  @override
  Future<CartItemResponseDto> addItem(
      int cartId, CartItemCreateRequestDto item) async {
    // Si el producto ya existe en el carrito, la API debe actualizar la cantidad
    final response = await _dio.post(
      '$_basePath/$cartId/items',
      data: item.toJson(),
    );
    return CartItemResponseDto.fromJson(response.data);
  }

  // Los demás métodos son llamados por el CartService.
  @override
  Future<void> updateItemQuantity(int cartId, int itemId, int quantity) async {
    // Solo necesitamos la cantidad en el DTO de actualización
    final request = CartItemUpdateRequestDto(productId: 0, quantity: quantity); 
    // Endpoint: PUT /api/v2/carts/{cartId}/items/{itemId}
    await _dio.put('$_basePath/$cartId/items/$itemId', data: request.toJson());
  }

  @override
  Future<void> removeItem(int cartId, int itemId) async {
    await _dio.delete('$_basePath/$cartId/items/$itemId');
  }

  @override
  Future<void> clearCart(int cartId) async {
    // Endpoint DELETE /api/v2/carts/{cartId}/items
    await _dio.delete('$_basePath/$cartId/items');
  }
}