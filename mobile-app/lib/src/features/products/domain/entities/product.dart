// lib/src/features/products/domain/entities/product.dart
import 'package:flutter/foundation.dart';

@immutable
class Product {
  final String id;
  final String name;
  final String description;
  final double price;
  final int stock;
  final String imageUrl;
  final String categoryId;
  final String categoryName;

  const Product({
    required this.id,
    required this.name,
    required this.description,
    required this.price,
    required this.stock,
    required this.imageUrl,
    required this.categoryId,
    required this.categoryName
  });

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is Product &&
        other.id == id &&
        other.name == name &&
        other.imageUrl == imageUrl &&
        other.price == price && 
        other.stock == stock &&
        other.categoryId == categoryId &&
        other.categoryName == categoryName &&
        other.description == description;
  }

  @override
  int get hashCode =>
      id.hashCode ^
      name.hashCode ^
      imageUrl.hashCode ^
      price.hashCode ^ description.hashCode ^
      stock.hashCode ^
      categoryId.hashCode ^
      description.hashCode ^
      categoryName.hashCode;
}