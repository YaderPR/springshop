// lib/domain/entities/address_entity.dart

class AddressEntity {
  // El ID es opcional en la creación, pero existe en la respuesta.
  final int? id; 
  final String street;
  final String city;
  final String state;
  final String country;
  final String zipCode;
  final String? phoneNumber; // Opcional según la API
  final int userId;

  AddressEntity({
    this.id,
    required this.street,
    required this.city,
    required this.state,
    required this.country,
    required this.zipCode,
    this.phoneNumber,
    required this.userId,
  });

  // Método helper para crear una nueva instancia con cambios
  AddressEntity copyWith({
    int? id,
    String? street,
    String? city,
    String? state,
    String? country,
    String? zipCode,
    String? phoneNumber,
    int? userId,
  }) {
    return AddressEntity(
      id: id ?? this.id,
      street: street ?? this.street,
      city: city ?? this.city,
      state: state ?? this.state,
      country: country ?? this.country,
      zipCode: zipCode ?? this.zipCode,
      phoneNumber: phoneNumber ?? this.phoneNumber,
      userId: userId ?? this.userId,
    );
  }
}