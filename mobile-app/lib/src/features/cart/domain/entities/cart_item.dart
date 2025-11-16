import 'package:springshop/src/features/products/domain/entities/product.dart';

class CartItem {
  // 🔑 ID del ítem dentro del carrito (Necesario para PUT/DELETE en el backend de Carrito)
  final int? itemId; 

  // ID del producto, como String (si lo usa el Product Service para detalles)
  final String productId; 
  
  // Cantidad actual
  int quantity;
  
  // Detalles del producto obtenidos del Product Service (para UI)
  Product? productDetails; 

  CartItem({
    this.itemId, // Ahora puede ser nulo si el ítem es nuevo o no se ha cargado completamente
    required this.productId, 
    this.quantity = 1, 
    this.productDetails,
  });

  /// Calcula el subtotal multiplicando el precio del producto por la cantidad.
  double get subtotal => (productDetails?.price ?? 0.0) * quantity;
}