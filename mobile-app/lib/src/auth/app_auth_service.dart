import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'auth_repository.dart'; // 💡 Importar la nueva interfaz

final FlutterAppAuth appAuth = FlutterAppAuth();
final FlutterSecureStorage secureStorage = FlutterSecureStorage();

// --- Configuración de Keycloak (ajústalo a tu entorno) ---
const String clientId = 'springshop-app-client';
const String realm = 'master';
const String keycloakBaseUrl = 'http://10.185.74.191:9090/realms/$realm';
const String redirectUri = 'org.springshop://callback';
const List<String> scopes = ['openid', 'profile', 'email', 'offline_access'];

// Endpoints configurados manualmente
final AuthorizationServiceConfiguration _serviceConfiguration =
    AuthorizationServiceConfiguration(
  authorizationEndpoint: '$keycloakBaseUrl/protocol/openid-connect/auth',
  tokenEndpoint: '$keycloakBaseUrl/protocol/openid-connect/token',
  endSessionEndpoint: '$keycloakBaseUrl/protocol/openid-connect/logout',
);

// 🚀 Implementamos el contrato AuthRepository
class AppAuthService implements AuthRepository { 
  
  // =======================================================
  // CONTRATO: 1. GESTIÓN DEL INICIO Y LOGOUT
  // =======================================================

  @override
  Future<void> signIn() async {
    // La implementación es correcta, solo rethrow en caso de result == null
    // para notificar al AuthStateNotifier que el login falló.
    try {
      final AuthorizationTokenResponse result =
          await appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          clientId,
          redirectUri,
          serviceConfiguration: _serviceConfiguration,
          scopes: scopes,
          promptValues: ['login'], // o 'select_account'
          allowInsecureConnections: true
        ),
      );

      if (result.accessToken == null) {
        throw Exception("El flujo de autenticación terminó sin recibir tokens válidos.");
      }

      await secureStorage.write(key: 'access_token', value: result.accessToken);
      await secureStorage.write(key: 'refresh_token', value: result.refreshToken);
      await secureStorage.write(key: 'id_token', value: result.idToken);
      await secureStorage.write(
        key: 'access_token_expiration',
        value: result.accessTokenExpirationDateTime?.toIso8601String(),
      );

    } catch (e) {
      print('❌ [AppAuthService.signIn] Error durante autenticación: $e');
      // Re-lanza la excepción para que el AuthStateNotifier sepa que el estado es false.
      rethrow; 
    }
  }

  @override
  Future<void> logout() async {
    // Implementación original es correcta.
    try {
      final idToken = await secureStorage.read(key: 'id_token');
      if (idToken == null) {
        await secureStorage.deleteAll();
        return;
      }
      
      await appAuth.endSession(
        EndSessionRequest(
          idTokenHint: idToken,
          postLogoutRedirectUrl: redirectUri,
          serviceConfiguration: _serviceConfiguration,
        ),
      );

      await secureStorage.deleteAll();
    } catch (e) {
      print('❌ Error cerrando sesión: $e');
      // No rethrow, el logout es crítico para la UX.
    }
  }

  @override
  Future<bool> isAuthenticated() async {
    // 💡 Implementación necesaria para el AuthStateNotifier
    final refreshToken = await secureStorage.read(key: 'refresh_token');
    return refreshToken != null;
  }
  
  // =======================================================
  // CONTRATO: 2. GESTIÓN DE TOKENS Y API
  // =======================================================

  @override
  Future<void> refreshTokens() async {
    // Renombrado de 'refresh' a 'refreshTokens' para el contrato.
    final refreshToken = await secureStorage.read(key: 'refresh_token');

    if (refreshToken == null) {
       // Lanza una excepción para que el AuthStateNotifier fuerce el logout
       throw Exception('No refresh token disponible, forzando cierre de sesión.');
    }

    try {
      final TokenResponse? resp = await appAuth.token(
        TokenRequest(
          clientId,
          redirectUri,
          refreshToken: refreshToken,
          serviceConfiguration: _serviceConfiguration,
          scopes: scopes,
        ),
      );

      if (resp == null || resp.accessToken == null) {
         // Si la respuesta es nula, Keycloak probablemente invalidó el token.
         throw Exception('El servidor no renovó el token. Sesión caducada.');
      }

      await secureStorage.write(key: 'access_token', value: resp.accessToken);
      await secureStorage.write(
        key: 'refresh_token',
        value: resp.refreshToken ?? refreshToken,
      );
       await secureStorage.write(
        key: 'access_token_expiration',
        value: resp.accessTokenExpirationDateTime?.toIso8601String(),
      );

    } catch (e) {
       print('❌ Error al refrescar token: $e');
       // Relanza la excepción para que los interceptores o el AuthStateNotifier
       // puedan atraparla y forzar el logout/login.
       rethrow; 
    }
  }

  @override
  Future<String?> getAccessToken() async {
    // Implementación mejorada para refrescar si el token está a punto de expirar (opcional, pero buena práctica).
    final expirationString = await secureStorage.read(key: 'access_token_expiration');
    final accessToken = await secureStorage.read(key: 'access_token');

    if (accessToken == null) return null;

    if (expirationString != null) {
        final expirationDate = DateTime.parse(expirationString);
        // Refrescar si falta menos de 5 minutos para expirar
        if (expirationDate.subtract(const Duration(minutes: 5)).isBefore(DateTime.now())) {
            try {
                print('🟡 Token a punto de expirar. Intentando refrescar...');
                await refreshTokens();
                // Devolver el nuevo token
                return await secureStorage.read(key: 'access_token');
            } catch (_) {
                // Si falla el refresh, devolver null para forzar re-login
                return null;
            }
        }
    }
    
    return accessToken;
  }
  
  // Mantener callApi para usar en interceptor de Dio
  Future<http.Response> callApi(String path) async {
    final token = await getAccessToken();
    if (token == null) {
      throw Exception('No access token disponible.');
    }
    
    final uri = Uri.parse('http://10.185.74.191:8080/api/$path');
    return await http.get(uri, headers: {
      'Authorization': 'Bearer $token',
      'Accept': 'application/json',
    });
  }
}