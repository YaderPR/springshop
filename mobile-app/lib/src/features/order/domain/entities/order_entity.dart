import 'package:springshop/src/features/order/domain/entities/order_item_entity.dart';

class OrderEntity {
  final int id;
  final String status;
  final double totalAmount;
  final int userId;
  final List<OrderItemEntity> items;
  final int addressId;
  final String createdAt;
  final String updatedAt;

  OrderEntity({
    required this.id,
    required this.status,
    required this.totalAmount,
    required this.userId,
    required this.items,
    required this.addressId,
    required this.createdAt,
    required this.updatedAt,
  });
  OrderEntity copyWith({
    int? id,
    String? status,
    double? totalAmount,
    int? userId,
    List<OrderItemEntity>? items,
    int? addressId,
    String? createdAt,
    String? updatedAt,
  }) {
    return OrderEntity(
      id: id ?? this.id,
      status: status ?? this.status,
      totalAmount: totalAmount ?? this.totalAmount,
      userId: userId ?? this.userId,
      items: items ?? this.items,
      addressId: addressId ?? this.addressId,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}
