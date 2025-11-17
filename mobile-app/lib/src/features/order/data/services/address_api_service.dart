// lib/data/services/address_service_impl.dart

import 'package:springshop/src/features/order/domain/entities/address_entity.dart';
import 'package:springshop/src/features/order/domain/repositories/address_repository.dart';
import 'package:springshop/src/features/order/domain/services/address_service.dart';

/// Implementación concreta de AddressService.
/// Esta clase contiene la lógica de negocio y utiliza el repositorio.
class AddressApiService implements AddressService {
  // 🔑 DEPENDENCIAS INYECTADAS
  final AddressRepository _addressRepository;

  // 🔑 CONSTRUCTOR CON INYECCIÓN DE DEPENDENCIAS
  AddressApiService(this._addressRepository);

  // ====================================================================
  // IMPLEMENTACIÓN DEL CONTRATO
  // ====================================================================

  /// Caso de uso: Registrar una nueva dirección validando los campos.
  @override
  Future<AddressEntity> registerNewAddress(AddressEntity address) async {
    // 💡 Aquí podrías agregar lógica de negocio antes de la persistencia:
    // 1. Validación de formato de campos (p.ej., zipCode regex).
    // 2. Verificar que el usuario no exceda el número máximo de direcciones.
    
    print('📞 [AddressService] Iniciando registro de nueva dirección...');

    if (address.street.isEmpty || address.city.isEmpty) {
      throw Exception("Los campos de calle y ciudad son obligatorios.");
    }
    
    // Delega la persistencia al repositorio
    final newAddress = await _addressRepository.createAddress(address);
    
    print('✅ [AddressService] Dirección registrada con ID: ${newAddress.id}');
    return newAddress;
  }

  /// Caso de uso: Buscar la última o principal dirección por ID de usuario.
  @override
  Future<AddressEntity?> findLastAddressByUserId(int userId) async {
    print('📞 [AddressService] Buscando última dirección para UserID: $userId');

    // Delega la búsqueda al repositorio
    final address = await _addressRepository.getLastAddressByUser(userId);

    if (address == null) {
      print('⚠️ [AddressService] Usuario $userId no tiene direcciones registradas.');
      // 💡 Aquí podrías tener lógica para ofrecer una dirección por defecto o un mensaje específico
    }
    
    return address;
  }
  Future<AddressEntity?> findAddressById(int id) async {
    final address = await _addressRepository.getAddressById(id);
    return address;
  }
}