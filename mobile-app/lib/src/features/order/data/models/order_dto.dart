// data/models/order_dto.dart
import 'package:springshop/src/features/order/data/models/order_item_dto.dart'; // Asumimos este DTO

class OrderResponseDto {
  final int id;
  final String status;
  final double totalAmount;
  final int userId;
  final int addressId;
  final List<OrderItemResponseDto> items;

  OrderResponseDto({
    required this.id,
    required this.status,
    required this.totalAmount,
    required this.userId,
    required this.addressId,
    required this.items,
  });

  factory OrderResponseDto.fromJson(Map<String, dynamic> json) {
    return OrderResponseDto(
      id: json['id'],
      status: json['status'],
      totalAmount: json['totalAmount'],
      userId: json['userId'],
      addressId: json['addressId'],
      items: (json['items'] as List)
          .map((i) => OrderItemResponseDto.fromJson(i))
          .toList(),
    );
  }
}