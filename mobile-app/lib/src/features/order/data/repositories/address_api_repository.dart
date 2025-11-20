// lib/data/repositories/address_api_repository.dart

import 'package:dio/dio.dart';
import 'package:springshop/src/features/order/domain/entities/address_entity.dart';
import 'package:springshop/src/features/order/domain/repositories/address_repository.dart';
import 'package:springshop/src/features/order/data/models/address_dto.dart';

/// Implementación concreta de AddressRepository que utiliza Dio para las llamadas API.
class AddressApiRepository implements AddressRepository {
  final Dio _dio;
  static const String _basePath = '/addresses'; // Asumimos /api/v2/addresses

  AddressApiRepository(this._dio);

  // ====================================================================
  // IMPLEMENTACIÓN DEL CONTRATO
  // ====================================================================

  /// 🎯 Crea una nueva dirección.
  @override
  Future<AddressEntity> createAddress(AddressEntity address) async {
    print('✨ [AddressApiRepository] Creando nueva dirección para UserID: ${address.userId}');
    try {
      // 1. Mapear Entidad de Dominio a DTO de Solicitud
      final request = AddressRequestDto.fromEntity(address);
      
      // 2. Llamada a la API: POST /api/v2/addresses
      final response = await _dio.post(_basePath, data: request.toJson());
      
      // 3. Mapear DTO de Respuesta a Entidad de Dominio y retornar
      final responseDto = AddressResponseDto.fromJson(response.data);
      return responseDto.toEntity();

    } on DioException catch (e) {
      print('❌ [AddressApiRepository] Error al crear dirección: ${e.message}');
      rethrow;
    }
  }

  /// 🎯 Obtiene una dirección por su ID.
  @override
  Future<AddressEntity> getAddressById(int id) async {
    print('🔎 [AddressApiRepository] Buscando dirección por ID: $id');
    try {
      // Llamada a la API: GET /api/v2/addresses/{id}
      final response = await _dio.get('$_basePath/$id');
      
      final responseDto = AddressResponseDto.fromJson(response.data);
      return responseDto.toEntity();
    } on DioException {
      // Podrías relanzar una excepción de dominio específica (p.ej., AddressNotFoundException)
      rethrow;
    }
  }

  /// 🎯 Obtiene la última (o principal) dirección de un usuario.
  @override
  Future<AddressEntity?> getLastAddressByUser(int userId) async {
    print('🔎 [AddressApiRepository] Buscando última dirección para UserID: $userId');
    try {
      // Endpoint asumido: GET /api/v2/addresses/users/{userId}/latest
      final response = await _dio.get('$_basePath/users/$userId/latest');
      
      final responseDto = AddressResponseDto.fromJson(response.data);
      return responseDto.toEntity();
    } on DioException catch (e) {
      // Si la API regresa un 404 (Not Found), significa que no hay dirección.
      if (e.response?.statusCode == 404) {
        print('✅ [AddressApiRepository] No se encontró dirección (404). Retornando null.');
        return null;
      }
      // Para cualquier otro error (500, timeout, etc.), relanzamos la excepción.
      rethrow;
    }
  }
}