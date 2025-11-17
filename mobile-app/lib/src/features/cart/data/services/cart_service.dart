import 'package:flutter/foundation.dart';
import 'package:springshop/src/features/cart/data/models/cart_dto.dart';
import 'package:springshop/src/features/cart/domain/entities/cart_item.dart';
import 'package:springshop/src/features/products/data/services/product_service.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/cart/domain/repositories/cart_repository.dart'; // 💡 Importar la interfaz

/// Gestor de estado del carrito, accesible globalmente.
class CartService with ChangeNotifier {
  // 🔑 DEPENDENCIAS INYECTADAS
  final CartRepository _cartRepository;
  final ProductService
      _productService; // Usaremos la interfaz para obtener detalles

  // --- Estado ---
  List<CartItem> _items = [];
  bool _isLoading = false;
  int? _currentCartId; // ID persistente del carrito (obtenido del backend)

  List<CartItem> get items => _items;
  bool get isLoading => _isLoading;
  int? get currentCartId => _currentCartId;

  // 🔑 CONSTRUCTOR CON INYECCIÓN DE DEPENDENCIAS
  CartService(this._cartRepository, this._productService);

  // ====================================================================
  // 1. LÓGICA DE INICIALIZACIÓN (Llamada desde AppAuthService)
  // ====================================================================

  /// 🎯 Se llama después del login para obtener o crear el ID del carrito.
  Future<void> initializeCart(int userId) async {
    _isLoading = true;
    print(
        '🔄 [CartService.initializeCart] Iniciando chequeo de carrito para UserID: $userId');
    notifyListeners(); // 🔑 Notificar inicio de carga

    try {
      // 1. INTENTAR OBTENER UN CARRITO EXISTENTE
      print('🔎 [CartService] Buscando carrito activo existente...');
      // Usamos el nuevo método que asumimos en el repositorio
      CartResponseDto? cartResponse =
          await _cartRepository.getExistingCart(userId);

      bool isNewCart = false;

      if (cartResponse == null) {
        // 2. SI NO EXISTE, CREAR UNO NUEVO
        print(
            '🚫 [CartService] No se encontró carrito activo. Procediendo a crear uno nuevo...');
        // Usamos el nuevo método que asumimos en el repositorio
        cartResponse = await _cartRepository.createCart(userId);
        isNewCart = true;
      }

      // 3. Establecer el ID y notificar el resultado
      _currentCartId = cartResponse.id;

      if (isNewCart) {
        print(
            '🎉 [CartService] Nuevo carrito creado y establecido. CartID: $_currentCartId');
      } else {
        print(
            '✅ [CartService] Carrito existente recuperado y establecido. CartID: $_currentCartId');
      }

      // 4. Carga los ítems actuales del carrito
      await _fetchCartItems(cartResponse);
    } catch (e) {
      print(
        '❌ [CartService.initializeCart] Error al inicializar el carrito (Limpiando estado): $e',
      );
      _currentCartId = null;
      _items = [];
    } finally {
      _isLoading = false;
      notifyListeners(); // Notificar fin de carga/cambio de estado
      print('🏁 [CartService.initializeCart] Finalizado. CartID final: $_currentCartId');
    }
  }

  /// Carga los ítems del carrito y sus detalles de los microservicios.
  /// Acepta el CartResponseDto directamente, ya sea de getOrCreateCart o de una llamada
  /// de sincronización futura.
  Future<void> _fetchCartItems(CartResponseDto cartResponse) async {
    if (_currentCartId == null) return;

    // 1. Obtener los IDs de productos y cantidades desde la respuesta del carrito
    final List<CartItemResponseDto> rawItems = cartResponse.items;

    // Convertimos el ID del producto a String para ser compatible con ProductRepository
    final productIds =
        rawItems.map((item) => item.productId.toString()).toList();

    if (productIds.isEmpty) {
      _items = [];
      return;
    }

    // 2. Llamada concurrente a ProductService para obtener detalles
    final products = await _productService.getProductsByIds(
      productIds.map((el) => int.parse(el)).toList(),
    );
    final Map<String, Product> productDetailsMap = {
      for (var p in products) p.id: p,
    };

    // 3. Unir datos
    _items = rawItems
        .map((rawItem) {
          final productIdString = rawItem.productId.toString();

          // Asumo que CartItemResponseDto tiene la propiedad 'id' para el itemId
          final int? itemId = rawItem.id;

          return CartItem(
            itemId: itemId, // Usamos el ID del ítem
            productId: productIdString,
            quantity: rawItem.quantity,
            productDetails: productDetailsMap[productIdString],
          );
        })
        .where((item) => item.productDetails != null)
        .toList();
  }

  // ====================================================================
  // 2. OPERACIONES DEL CARRITO
  // ====================================================================

  /// Agrega un producto al carrito o incrementa su cantidad.
  Future<void> addItem(String productId, {int quantity = 1}) async {
    if (_currentCartId == null) {
      throw Exception("El carrito no ha sido inicializado.");
    }

    // 🔑 CLAVE: Notificar que estamos cargando/operando antes de la llamada a la API
    _isLoading = true;
    notifyListeners();

    try {
      // Encontrar ítem existente o crear un placeholder para la lógica
      final existingItem = _items.firstWhere(
        (item) => item.productId == productId,
        // Usamos un item temporal para manejar el caso orElse
        orElse: () =>
            CartItem(productId: productId, quantity: 0, productDetails: null),
      );

      // El DTO de creación/actualización requiere el productId como int
      final int prodId = int.parse(productId);
      print("ItemId desde AddItem: ${existingItem.itemId}");

      if (existingItem.quantity > 0 && existingItem.itemId != null) {
        // Caso de actualización: incrementamos la cantidad existente
        print('🛒 [CartService.addItem] Actualizando cantidad de $productId. Nueva Cantidad: ${existingItem.quantity + quantity}');
        final newQuantity = existingItem.quantity + quantity;
        await _cartRepository.updateItemQuantity(
          _currentCartId!,
          existingItem.itemId!,
          newQuantity,
          int.tryParse(productId) ?? 0
        );
      } else {
        // Caso de creación: añadimos un nuevo ítem
        print('🛒 [CartService.addItem] Añadiendo nuevo ítem $productId con Cantidad: $quantity');
        final createDto = CartItemCreateRequestDto(
          productId: prodId,
          quantity: quantity,
        );
        await _cartRepository.addItem(_currentCartId!, createDto);
      }

      // 🎯 SINCRONIZACIÓN: Llamamos al repositorio para obtener la versión actualizada del carrito
      final updatedCartResponse = await _cartRepository.getCartById(
        _currentCartId!,
      );
      await _fetchCartItems(updatedCartResponse);
    } catch (e) {
      print('❌ [CartService.addItem] Error al añadir ítem: $e');
    } finally {
      _isLoading = false;
      notifyListeners(); // 🔑 Notificar fin de operación y actualización de la lista _items
    }
  }

  /// Elimina completamente un ítem del carrito.
  Future<void> removeItem(String productId) async {
    if (_currentCartId == null) return;

    final existingItem = _items.firstWhere(
      (item) => item.productId == productId,
      orElse: () => CartItem(productId: productId, quantity: 0, productDetails: null),
    );
    if (existingItem.itemId == null) {
        print('⚠️ [CartService.removeItem] Ítem $productId no encontrado o sin itemId para eliminar.');
        return; // No hay ID de ítem para eliminar
    }

    _isLoading = true;
    notifyListeners(); // Notificar inicio de carga/operación

    try {
      print('🛒 [CartService.removeItem] Eliminando ítem $productId con ItemID: ${existingItem.itemId}');
      await _cartRepository.removeItem(_currentCartId!, existingItem.itemId!);

      // 🎯 SINCRONIZACIÓN: Llamamos al repositorio para obtener la versión actualizada del carrito
      final updatedCartResponse = await _cartRepository.getCartById(
        _currentCartId!,
      );
      await _fetchCartItems(updatedCartResponse);
    } catch (e) {
      print('❌ [CartService.removeItem] Error al eliminar ítem: $e');
    } finally {
      _isLoading = false;
      notifyListeners(); // Notificar fin de operación
    }
  }

  /// Actualiza la cantidad de un ítem.
  Future<void> updateQuantity(String productId, int newQuantity) async {
    print("currentCartId y productId desde updateQuantity: $_currentCartId - $productId");
    if (_currentCartId == null) return;

    final itemIndex = _items.indexWhere((item) => item.productId == productId);
    print("itemIndex y items[itemIndex].itemId desde updateQuantity: $itemIndex - ${_items[itemIndex].itemId}");
    if (itemIndex == -1 || _items[itemIndex].itemId == null) return;

    _isLoading = true;
    notifyListeners(); // Notificar inicio de carga/operación

    try {
      print("newQuantity desde updateQuantity: $newQuantity");
      if (newQuantity <= 0) {
        // Si la cantidad es 0 o menos, usamos la lógica de eliminación
        print('🛒 [CartService.updateQuantity] Cantidad es 0 o menos. Eliminando ítem $productId.');
        await removeItem(productId);
      } else {
        print('🛒 [CartService.updateQuantity] Actualizando cantidad de $productId a $newQuantity.');
        await _cartRepository.updateItemQuantity(
          _currentCartId!,
          _items[itemIndex].itemId!,
          newQuantity,
          int.tryParse(productId) ?? 0
        );

        // 🎯 SINCRONIZACIÓN: Llamamos al repositorio para obtener la versión actualizada del carrito
        final updatedCartResponse = await _cartRepository.getCartById(
          _currentCartId!,
        );
        await _fetchCartItems(updatedCartResponse);

        // Nota: El fetch de ítems ya actualiza _items, así que no es necesario
        // la actualización local manual de la cantidad.
      }
    } catch (e) {
      print('❌ [CartService.updateQuantity] Error al actualizar cantidad: $e');
    } finally {
      _isLoading = false;
      notifyListeners(); // Notificar fin de operación
    }
  }

  // ====================================================================
  // 3. AUXILIARES
  // ====================================================================

  /// Calcula el total de la compra.
  double get totalAmount {
    // Usamos el subtotal de cada CartItem, que asume que productDetails está disponible.
    return _items.fold(0.0, (sum, item) => sum + item.subtotal);
  }

  // ====================================================================
  // 4. GESTIÓN DEL ESTADO GLOBAL (NUEVO)
  // ====================================================================

  /// Limpia el estado del carrito (llamado al cerrar sesión).
  void clear() {
    print('🛒 [CartService] Limpiando estado del carrito.');
    _items = [];
    _isLoading = false;
    _currentCartId = null;
    notifyListeners();
  }
}