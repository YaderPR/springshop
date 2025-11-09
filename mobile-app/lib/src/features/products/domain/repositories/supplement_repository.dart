import 'package:springshop/src/features/products/domain/entities/supplement.dart';

abstract class SupplementRepository {
  Future<Supplement> findById(int id); 
}