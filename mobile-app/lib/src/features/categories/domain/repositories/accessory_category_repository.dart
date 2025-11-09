import 'package:springshop/src/features/categories/domain/entities/subcategory.dart';

abstract class AccessoryCategoryRepository {
  Future<List<Subcategory>> getCategories();
}