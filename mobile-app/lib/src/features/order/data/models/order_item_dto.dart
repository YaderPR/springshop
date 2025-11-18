class OrderItemResponseDto {
  final int id;
  final int productId;
  final int quantity;
  final double price;
  OrderItemResponseDto({
    required this.id,
    required this.productId,
    required this.quantity,
    required this.price,
  });
  OrderItemResponseDto copyWith({int? id, int? productId, int? quantity, double? price}) {
    return OrderItemResponseDto(
      id: id ?? this.id,
      productId: productId ?? this.productId,
      quantity: quantity ?? this.quantity,
      price: price ?? this.price,
    );
  }
  factory OrderItemResponseDto.fromJson(Map<String, dynamic> json) {
    return OrderItemResponseDto(id: json["id"], productId: json["productId"], price: json["price"], quantity: json["quantity"]);
  }

}
