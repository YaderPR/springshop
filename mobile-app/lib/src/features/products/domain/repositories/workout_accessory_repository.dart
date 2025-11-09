import 'package:springshop/src/features/products/domain/entities/workout_accessory.dart';

abstract class WorkoutAccessoryRepository {
  Future<WorkoutAccessory> findById(int id); 
}