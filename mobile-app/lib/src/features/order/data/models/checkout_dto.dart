// data/models/checkout_dto.dart
class CheckoutRequestDto {
 final int cartId;
 final int userId;
 final int addressId;
 final String redirectUrl; // 🔑 NUEVO: URL de redirección (Deep Link para la app)

 CheckoutRequestDto({
  required this.cartId,
  required this.userId,
  required this.addressId,
  required this.redirectUrl, // 🔑 Añadido al constructor
 });

 Map<String, dynamic> toJson() => {
    'cartId': cartId,
    'userId': userId,
    'addressId': addressId,
    'redirectUrl': redirectUrl, // 🔑 Añadido al JSON que se envía a la API
   };
}