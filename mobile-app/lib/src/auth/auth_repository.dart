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
}