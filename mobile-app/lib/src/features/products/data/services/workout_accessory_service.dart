import 'package:springshop/src/features/products/domain/entities/product.dart';
import 'package:springshop/src/features/products/domain/entities/workout_accessory.dart';
import 'package:springshop/src/features/products/domain/repositories/workout_accessory_repository.dart';

class WorkoutAccessoryService {
  final WorkoutAccessoryRepository _workoutAccessoryRepository;
  
  WorkoutAccessoryService(this._workoutAccessoryRepository);
  Future<List<Product>> getWorkoutAccessoriesByIds(List<int> ids) async {
    List<Future<WorkoutAccessory>> workoutAccessoryFutures = ids
        .map((id) => _workoutAccessoryRepository.findById(id))
        .toList();

    try {
      List<WorkoutAccessory> workoutAccessories = await Future.wait(workoutAccessoryFutures);
      return workoutAccessories;

    } on Exception catch (e) {
      print('Error al obtener workout-accessory por IDs: $e'); 
      return []; 
    }
  }
}