import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/repositories/product_repository.dart';

class ProductService {
  final ProductRepository _productRepository;
  
  ProductService(this._productRepository);
  Future<List<Product>> getProductsByIds(List<int> ids) async {
    List<Future<Product>> productFutures = ids
        .map((id) => _productRepository.findById(id))
        .toList();

    try {
      List<Product> products = await Future.wait(productFutures);
      return products;

    } on Exception catch (e) {
      print('Error al obtener productos por IDs: $e'); 
      return []; 
    }
  }
}