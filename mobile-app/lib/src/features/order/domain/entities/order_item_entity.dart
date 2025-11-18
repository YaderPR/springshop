class OrderItemEntity {
  final int id;
  final int productId;
  final int quantity;
  final double price;
  OrderItemEntity({
    required this.id,
    required this.productId,
    required this.quantity,
    required this.price,
  });
  OrderItemEntity copyWith({int? id, int? productId, int? quantity, double? price}) {
    return OrderItemEntity(
      id: id ?? this.id,
      productId: productId ?? this.productId,
      quantity: quantity ?? this.quantity,
      price: price ?? this.price,
    );
  }

}
