// lib/src/features/products/domain/entities/product.dart
import 'package:flutter/foundation.dart';

@immutable
class Product {
  final String id;
  final String name;
  final String imageUrl;
  final double price;
  final String currency; // Añadimos para más flexibilidad

  const Product({
    required this.id,
    required this.name,
    required this.imageUrl,
    required this.price,
    this.currency = 'C\$', // Por defecto, como en tu imagen
  });

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is Product &&
        other.id == id &&
        other.name == name &&
        other.imageUrl == imageUrl &&
        other.price == price &&
        other.currency == currency;
  }

  @override
  int get hashCode =>
      id.hashCode ^
      name.hashCode ^
      imageUrl.hashCode ^
      price.hashCode ^
      currency.hashCode;
}