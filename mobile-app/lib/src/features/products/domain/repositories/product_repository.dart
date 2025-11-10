import '../entities/product.dart';
abstract class ProductRepository {
  Future<Product> findById(int id);
  Future<List<Product>> findAll();
}