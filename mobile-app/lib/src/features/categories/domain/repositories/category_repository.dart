// lib/src/features/categories/domain/repositories/category_repository.dart
import '../entities/category.dart';

abstract class CategoryRepository {
  Future<List<Category>> getCategories();
}