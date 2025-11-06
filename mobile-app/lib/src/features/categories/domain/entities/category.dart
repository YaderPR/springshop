// lib/src/features/categories/domain/entities/category.dart
import 'package:flutter/foundation.dart';

@immutable
class Category {
  final String id;
  final String name;
  final String? imageUrl;
  final List<dynamic>? productIds;

  const Category({
    required this.id,
    required this.name,
    this.imageUrl,
    this.productIds
  });

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is Category &&
        other.id == id &&
        other.name == name &&
        other.imageUrl == imageUrl &&
        other.productIds == productIds;
  }

  @override
  int get hashCode => id.hashCode ^ name.hashCode ^ imageUrl.hashCode;
}