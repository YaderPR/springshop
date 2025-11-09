import 'package:flutter/foundation.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';

@immutable
class Supplement extends Product {
  final String size;
  final String brand;
  final String flavor;
  final String ingredients;
  final String usageInstructions;
  final String warnings;
  const Supplement({required super.id, required super.name,
                 required super.description, required super.price,
                 required super.stock, required super.imageUrl,
                 required super.categoryId, required super.categoryName,
                 required this.size, required this.flavor,
                 required this.brand, required this.ingredients,
                 required this.usageInstructions, required this.warnings});

}