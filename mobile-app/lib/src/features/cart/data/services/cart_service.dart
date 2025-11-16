import 'package:flutter/foundation.dart';
import 'package:springshop/src/features/cart/data/models/cart_dto.dart';
import 'package:springshop/src/features/cart/domain/entities/cart_item.dart';
import 'package:springshop/src/features/products/data/services/product_service.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/cart/domain/repositories/cart_repository.dart'; // 💡 Importar la interfaz
import 'package:springshop/src/features/products/domain/repositories/product_repository.dart'; // 💡 Necesario para obtener ProductDetails

/// Gestor de estado del carrito, accesible globalmente.
class CartService with ChangeNotifier {
  // 🔑 DEPENDENCIAS INYECTADAS
  final CartRepository _cartRepository;
  final ProductService _productService; // Usaremos la interfaz para obtener detalles

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
    notifyListeners();

    try {
      // 1. Llama al repositorio para obtener o crear el carrito
      final cartResponse = await _cartRepository.getOrCreateCart(userId);
      
      // 💥 CORRECCIÓN: Usamos 'id' en lugar de 'cartId'
      _currentCartId = cartResponse.id; 
      
      print('🛒 [CartService] Carrito inicializado. CartID: $_currentCartId');

      // 2. Carga los ítems actuales del carrito
      await _fetchCartItems(cartResponse);

    } catch (e) {
      print('❌ [CartService.initializeCart] Error al inicializar el carrito: $e');
      _currentCartId = null; 
      _items = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }


  /// Carga los ítems del carrito y sus detalles de los microservicios.
  /// Acepta el CartResponseDto directamente, ya sea de getOrCreateCart o de una llamada
  /// de sincronización futura.
  Future<void> _fetchCartItems(dynamic cartResponse) async {
    if (_currentCartId == null) return;

    // 1. Obtener los IDs de productos y cantidades desde la respuesta del carrito
    final List<dynamic> rawItems = cartResponse.items;
    
    // Convertimos el ID del producto a String para ser compatible con ProductRepository
    final productIds = rawItems.map((item) => item.productId.toString()).toList();
    
    if (productIds.isEmpty) {
        _items = [];
        return;
    }

    // 2. Llamada concurrente a ProductService para obtener detalles
    final products = await _productService.getProductsByIds(productIds.map((el) => el as int).toList());
    final Map<String, Product> productDetailsMap = 
        {for (var p in products) p.id: p};

    // 3. Unir datos
    _items = rawItems.map((rawItem) {
      final productIdString = rawItem.productId.toString();
      
      // Asumo que CartItemResponseDto tiene la propiedad 'id' para el itemId
      final int? itemId = rawItem.id as int?; 

      return CartItem(
        itemId: itemId, // Usamos el ID del ítem
        productId: productIdString,
        quantity: rawItem.quantity,
        productDetails: productDetailsMap[productIdString],
      );
    }).where((item) => item.productDetails != null).toList(); 
  }


  // ====================================================================
  // 2. OPERACIONES DEL CARRITO
  // ====================================================================

  /// Agrega un producto al carrito o incrementa su cantidad.
  Future<void> addItem(String productId, {int quantity = 1}) async {
    if (_currentCartId == null) {
      throw Exception("El carrito no ha sido inicializado.");
    }

    _isLoading = true;
    notifyListeners();
    try {
        final existingItem = _items.firstWhere(
            (item) => item.productId == productId,
            orElse: () => CartItem(productId: productId, quantity: 0),
        );
        
        // El DTO de creación/actualización requiere el productId como int
        final int prodId = int.parse(productId);

        if (existingItem.quantity > 0 && existingItem.itemId != null) {
            // Caso de actualización: incrementamos la cantidad existente
            final newQuantity = existingItem.quantity + quantity;
            await _cartRepository.updateItemQuantity(
                _currentCartId!, existingItem.itemId!, newQuantity);
        } else {
            // Caso de creación: añadimos un nuevo ítem
            final createDto = CartItemCreateRequestDto(
                productId: prodId,
                quantity: quantity,
            );
            await _cartRepository.addItem(_currentCartId!, createDto);

            // Nota: Se elimina la lógica de actualización local para depender del sync
        }

        // 🎯 SINCRONIZACIÓN: Llamamos al repositorio para obtener la versión actualizada del carrito
        final updatedCartResponse = await _cartRepository.getCartById(_currentCartId!);
        await _fetchCartItems(updatedCartResponse);

    } catch (e) {
        print('❌ [CartService.addItem] Error al añadir ítem: $e');
    } finally {
        _isLoading = false;
        notifyListeners();
    }
  }

  /// Elimina completamente un ítem del carrito.
  Future<void> removeItem(String productId) async {
    if (_currentCartId == null) return;
    
    final existingItem = _items.firstWhere((item) => item.productId == productId);
    if (existingItem.itemId == null) return; // No hay ID de ítem para eliminar
    
    _isLoading = true;
    notifyListeners();
    try {
        await _cartRepository.removeItem(_currentCartId!, existingItem.itemId!);
        _items.removeWhere((item) => item.productId == productId);
    } catch (e) {
        print('❌ [CartService.removeItem] Error al eliminar ítem: $e');
    } finally {
        _isLoading = false;
        notifyListeners();
    }
  }

  /// Actualiza la cantidad de un ítem.
  Future<void> updateQuantity(String productId, int newQuantity) async {
    if (_currentCartId == null) return;

    final itemIndex = _items.indexWhere((item) => item.productId == productId);
    if (itemIndex == -1 || _items[itemIndex].itemId == null) return;

    _isLoading = true;
    notifyListeners();

    try {
        if (newQuantity <= 0) {
            await removeItem(productId);
        } else {
            await _cartRepository.updateItemQuantity(
                _currentCartId!, _items[itemIndex].itemId!, newQuantity);
            
            // Si la API tuvo éxito, actualizamos localmente y notificamos
            _items[itemIndex].quantity = newQuantity;
        }
    } catch (e) {
        print('❌ [CartService.updateQuantity] Error al actualizar cantidad: $e');
    } finally {
        _isLoading = false;
        notifyListeners();
    }
  }

  // ====================================================================
  // 3. AUXILIARES
  // ====================================================================

  /// Calcula el total de la compra.
  double get totalAmount {
    return _items.fold(0.0, (sum, item) => sum + item.subtotal);
  }
}