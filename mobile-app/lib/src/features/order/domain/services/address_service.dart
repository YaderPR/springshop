// lib/domain/services/address_service.dart

import 'package:springshop/src/features/order/domain/entities/address_entity.dart';

/// Define el contrato para las operaciones de negocio relacionadas con Direcciones.
/// Es la interfaz que debe ser inyectada y utilizada por la Capa de Presentación.
abstract class AddressService {

  /// Caso de uso: Registrar una nueva dirección validando los campos.
  /// (Usa AddressRepository.createAddress internamente)
  Future<AddressEntity> registerNewAddress(AddressEntity address);

  /// Caso de uso: Buscar la última o principal dirección por ID de usuario.
  /// Retorna la dirección o null si el usuario no tiene ninguna registrada.
  /// (Usa AddressRepository.getLastAddressByUser internamente)
  Future<AddressEntity?> findLastAddressByUserId(int userId);

  Future<AddressEntity?> findAddressById(int id);
}