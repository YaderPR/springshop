import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/entities/supplement.dart';
import 'package:springshop/src/features/products/domain/repositories/supplement_repository.dart';

class SupplementService {
  final SupplementRepository _supplementRepository;
  
  SupplementService(this._supplementRepository);
  Future<List<Product>> getSupplementsByIds(List<int> ids) async {
    List<Future<Supplement>> supplementFutures = ids
        .map((id) => _supplementRepository.findById(id))
        .toList();

    try {
      List<Supplement> supplements = await Future.wait(supplementFutures);
      return supplements;

    } on Exception catch (e) {
      print('Error al obtener supplements por IDs: $e'); 
      return []; 
    }
  }
}