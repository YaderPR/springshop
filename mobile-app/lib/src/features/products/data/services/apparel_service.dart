import 'package:springshop/src/features/products/domain/entities/apparel.dart';
import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/repositories/apparel_repository.dart';

class ApparelService {
  final ApparelRepository _apparelRepository;
  
  ApparelService(this._apparelRepository);
  Future<List<Product>> getApparelsByIds(List<int> ids) async {
    List<Future<Apparel>> apparelFutures = ids
        .map((id) => _apparelRepository.findById(id))
        .toList();

    try {
      List<Apparel> apparels = await Future.wait(apparelFutures);
      return apparels;

    } on Exception catch (e) {
      print('Error al obtener apparels por IDs: $e'); 
      return []; 
    }
  }
}