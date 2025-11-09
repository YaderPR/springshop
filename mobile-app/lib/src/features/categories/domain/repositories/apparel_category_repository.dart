import 'package:springshop/src/features/categories/domain/entities/subcategory.dart';

abstract class ApparelCategoryRepository {
  Future<List<Subcategory>> getCategories();
}