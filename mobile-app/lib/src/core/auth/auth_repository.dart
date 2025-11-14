import 'package:springshop/src/features/auth/domain/user.dart'; // 💡 Asegúrate de importar tu clase User

abstract class AuthRepository {
  /// Devuelve true si existe un token de refresco válido.
  Future<bool> isAuthenticated();

  /// Inicia el flujo de autenticación (launch browser/webview).
  /// Lanza una excepción en caso de fallo (cancelación, error de red/servidor).
  Future<void> signIn();

  /// Intenta usar el refresh token para obtener un nuevo access token.
  /// Lanza una excepción o devuelve false si el refresh falla (token expirado).
  Future<void> refreshTokens();

  /// Cierra la sesión en Keycloak y limpia el almacenamiento local.
  Future<void> logout();

  /// Obtiene el access token actual. Intenta refrescarlo si está cerca de expirar.
  Future<String?> getAccessToken();
  
  // 🔑 MÉTODO AÑADIDO: Obtiene la información detallada del usuario desde el endpoint userinfo.
  /// Llama al endpoint userinfo de Keycloak usando el Access Token para obtener
  /// los detalles de la identidad del usuario autenticado.
  /// Lanza una excepción si el token es inválido o la petición falla.
  Future<User> getUserInfo(); 
}