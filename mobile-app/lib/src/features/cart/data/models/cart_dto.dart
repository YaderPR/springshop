// lib/src/features/cart/data/models/cart_dto.dart

class CartRequestDto {
  final int userId;
  CartRequestDto({required this.userId});

  Map<String, dynamic> toJson() => {'userId': userId};
}

class CartItemCreateRequestDto {
  final int productId;
  final int quantity;

  CartItemCreateRequestDto({required this.productId, required this.quantity});

  Map<String, dynamic> toJson() => {
    'productId': productId,
    'quantity': quantity,
  };
}

// Modelos de respuesta (simplificados)
class CartItemResponseDto {
  final int id;
  final int productId;
  final int cartId;
  final int quantity;
  final double price;

  CartItemResponseDto({
    required this.id,
    required this.productId,
    required this.cartId,
    required this.quantity,
    required this.price,
  });

  factory CartItemResponseDto.fromJson(Map<String, dynamic> json) =>
      CartItemResponseDto(
        id: json['id'] as int,
        productId: json['productId'] as int,
        cartId: json['cartId'] as int,
        quantity: json['quantity'] as int,
        price: json['price'] as double,
      );
}

class CartResponseDto {
  final int id;
  final int userId;
  final List<CartItemResponseDto> items;

  CartResponseDto({
    required this.id,
    required this.userId,
    required this.items,
  });

  factory CartResponseDto.fromJson(Map<String, dynamic> json) =>
      CartResponseDto(
        id: json['id'] as int,
        userId: json['userId'] as int,
        items:
            (json['items'] as List<dynamic>?)
                ?.map(
                  (e) =>
                      CartItemResponseDto.fromJson(e as Map<String, dynamic>),
                )
                .toList() ??
            [],
      );
}

class CartItemUpdateRequestDto {
  final int productId;
  final int quantity;

  CartItemUpdateRequestDto({required this.productId, required this.quantity});

  Map<String, dynamic> toJson() => {
    'productId': productId,
    'quantity': quantity,
  };
}
