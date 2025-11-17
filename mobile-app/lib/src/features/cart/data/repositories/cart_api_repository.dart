import 'package:dio/dio.dart';
import 'package:springshop/src/features/cart/data/models/cart_dto.dart';
import 'package:springshop/src/features/cart/domain/repositories/cart_repository.dart';

class CartApiRepository implements CartRepository {
  final Dio _dio;
  static const String _basePath = '/carts';

  CartApiRepository(this._dio);

  // ====================================================================
  // NUEVOS MÉTODOS DE BÚSQUEDA Y CREACIÓN (IMPLEMENTACIÓN EXPLÍCITA)
  // ====================================================================

  /// 🎯 Intenta obtener un carrito activo para un usuario.
  /// Devuelve [CartResponseDto] si se encuentra, o [null] si la API responde 404.
  @override
  Future<CartResponseDto?> getExistingCart(int userId) async {
    try {
      // Endpoint asumido: GET /api/v2/carts/user/{userId}
      print('🔎 [CartApiRepository] Buscando carrito existente para UserId: $userId');
      final response = await _dio.get('$_basePath/user/$userId');
      return CartResponseDto.fromJson(response.data);
    } on DioException catch (e) {
      // Si la API regresa un 404 (Not Found), significa que no hay carrito activo.
      if (e.response?.statusCode == 404) {
        print('✅ [CartApiRepository] No se encontró carrito (404). Retornando null.');
        return null;
      }
      // Para cualquier otro error (500, timeout, etc.), relanzamos la excepción.
      rethrow;
    }
  }

  /// 🎯 Crea un nuevo carrito para el usuario.
  /// Se llama solo si [getExistingCart] devolvió null.
  @override
  Future<CartResponseDto> createCart(int userId) async {
    print('✨ [CartApiRepository] Creando nuevo carrito para UserId: $userId');
    // Endpoint asumido: POST /api/v2/carts
    final request = CartRequestDto(userId: userId);
    final response = await _dio.post(_basePath, data: request.toJson());
    return CartResponseDto.fromJson(response.data);
  }
  
  // ====================================================================
  // MÉTODOS ANTERIORES (getOrCreateCart ELIMINADO)
  // ====================================================================

  @override
  Future<CartResponseDto> getCartById(int cartId) async {
    // Endpoint: GET /api/v2/carts/{cartId}
    final response = await _dio.get('$_basePath/$cartId');
    return CartResponseDto.fromJson(response.data);
  }

  @override
  Future<CartItemResponseDto> addItem(
    int cartId,
    CartItemCreateRequestDto item,
  ) async {
    // Si el producto ya existe en el carrito, la API debe actualizar la cantidad
    final response = await _dio.post(
      '$_basePath/$cartId/items',
      data: item.toJson(),
    );
    return CartItemResponseDto.fromJson(response.data);
  }

  // Los demás métodos son llamados por el CartService.
  @override
  Future<void> updateItemQuantity(int cartId, int itemId, int quantity, int productId) async {
    // Solo necesitamos la cantidad en el DTO de actualización
    final request = CartItemUpdateRequestDto(productId: productId, quantity: quantity);
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