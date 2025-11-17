// lib/domain/repositories/address_repository.dart

import 'package:springshop/src/features/order/domain/entities/address_entity.dart';

/// Define el contrato de acceso a datos para el recurso Address.
/// Es una interfaz abstracta que la Capa de Datos (Data Layer) debe implementar.
abstract class AddressRepository {
  
  /// (POST /api/v2/addresses)
  /// Crea una nueva dirección.
  /// Retorna la entidad creada (incluyendo el ID asignado por el servidor).
  Future<AddressEntity> createAddress(AddressEntity address);

  /// (GET /api/v2/addresses/{id})
  /// Obtiene una dirección por su identificador.
  /// El uso de 'Future<AddressEntity>' implica que debe lanzar una excepción si no se encuentra.
  Future<AddressEntity> getAddressById(int id);

  // (GET /api/v2/addresses/user/{userId})
  // Obtiene una direccion por identificador de usuario.
  // Retorna la entidad si existe o null si status code 404.
  Future<AddressEntity?> getLastAddressByUser(int userId);
}