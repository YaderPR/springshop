import '../entities/product.dart';
abstract class ProductRepository {
  Future<Product> findById(int id); 
}