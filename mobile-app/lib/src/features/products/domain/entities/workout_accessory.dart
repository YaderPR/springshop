import 'package:flutter/foundation.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';

@immutable
class WorkoutAccessory extends Product {
  final String material;
  final String dimensions;
  final String weight;
  final String color;
  final String workoutAccessoryCategoryId;
  final String workoutAccessoryCategoryName;
  const WorkoutAccessory({required super.id, required super.name,
                 required super.description, required super.price,
                 required super.stock, required super.imageUrl,
                 required super.categoryId, required super.categoryName, 
                 required this.material, required this.dimensions, required this.weight, 
                 required this.color, required this.workoutAccessoryCategoryId, 
                 required this.workoutAccessoryCategoryName});

}