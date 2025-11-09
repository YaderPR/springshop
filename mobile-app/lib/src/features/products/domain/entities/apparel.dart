import 'package:flutter/foundation.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';

@immutable
class Apparel extends Product {
  final String size;
  final String brand;
  final String color;
  final String apparelCategoryName;
  final String apparelCategoryId;
  const Apparel({required super.id, required super.name,
                 required super.description, required super.price,
                 required super.stock, required super.imageUrl,
                 required super.categoryId, required super.categoryName,
                 required this.size, required this.color,
                 required this.apparelCategoryId, required this.apparelCategoryName,
                 required this.brand});

}