
import 'package:springshop/src/features/products/domain/entities/apparel.dart';

abstract class ApparelRepository {
  Future<Apparel> findById(int id); 
}