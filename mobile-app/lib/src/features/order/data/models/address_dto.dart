// lib/data/models/address_dto.dart

// Importamos la entidad de dominio para el mapeo
import 'package:springshop/src/features/order/domain/entities/address_entity.dart';

/// DTO usado para recibir la respuesta de la API (GET/POST/PUT).
class AddressResponseDto {
  final int id;
  final String street;
  final String city;
  final String state;
  final String country;
  final String zipCode;
  final String? phoneNumber;
  final int userId;

  AddressResponseDto({
    required this.id,
    required this.street,
    required this.city,
    required this.state,
    required this.country,
    required this.zipCode,
    this.phoneNumber,
    required this.userId,
  });

  factory AddressResponseDto.fromJson(Map<String, dynamic> json) {
    return AddressResponseDto(
      id: json['id'] as int,
      street: json['street'] as String,
      city: json['city'] as String,
      state: json['state'] as String,
      country: json['country'] as String,
      zipCode: json['zipCode'] as String,
      phoneNumber: json['phoneNumber'] as String?,
      userId: json['userId'] as int,
    );
  }

  // Mapeo a la Entidad de Dominio (Hacia arriba)
  AddressEntity toEntity() {
    return AddressEntity(
      id: id,
      street: street,
      city: city,
      state: state,
      country: country,
      zipCode: zipCode,
      phoneNumber: phoneNumber,
      userId: userId,
    );
  }
}

/// DTO usado para enviar datos a la API (POST/PUT).
class AddressRequestDto {
  final String street;
  final String city;
  final String state;
  final String country;
  final String zipCode;
  final String? phoneNumber;
  final int userId;

  AddressRequestDto({
    required this.street,
    required this.city,
    required this.state,
    required this.country,
    required this.zipCode,
    this.phoneNumber,
    required this.userId,
  });

  // Mapeo desde la Entidad de Dominio (Hacia abajo)
  factory AddressRequestDto.fromEntity(AddressEntity entity) {
    return AddressRequestDto(
      street: entity.street,
      city: entity.city,
      state: entity.state,
      country: entity.country,
      zipCode: entity.zipCode,
      phoneNumber: entity.phoneNumber,
      userId: entity.userId,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'street': street,
      'city': city,
      'state': state,
      'country': country,
      'zipCode': zipCode,
      'phoneNumber': phoneNumber,
      'userId': userId,
    };
  }
}