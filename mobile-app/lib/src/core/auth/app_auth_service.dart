import 'package:dio/dio.dart';
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:springshop/src/features/auth/domain/user.dart'; // Corregido: asumimos que User está en domain/entities
import 'auth_repository.dart'; 
// Eliminado: import 'package:http/http.dart' as http;
// Eliminado: import 'package:http/http.dart' as _dio; // Esto era incorrecto

final FlutterAppAuth appAuth = FlutterAppAuth();
final FlutterSecureStorage secureStorage = FlutterSecureStorage();

// --- Configuración de Keycloak ---
const String clientId = 'springshop-app-client';
const String realm = 'master';
const String keycloakBaseUrl = 'http://10.203.95.191:9090/realms/$realm';
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
  // 🔑 INYECCIÓN: Propiedad privada para la instancia de Dio inyectada (para Keycloak)
  final Dio _keycloakDio; 
  
  // 🔑 CONSTRUCTOR: Usamos un constructor con nombre claro o solo posicional
  AppAuthService({required Dio keycloakDio}) : _keycloakDio = keycloakDio;

  // =======================================================
  // CONTRATO: 1. GESTIÓN DEL INICIO Y LOGOUT
  // =======================================================

  @override
  Future<void> signIn() async {
    try {
      final AuthorizationTokenResponse? result =
          await appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          clientId,
          redirectUri,
          serviceConfiguration: _serviceConfiguration,
          scopes: scopes,
          promptValues: ['login'], 
          allowInsecureConnections: true
        ),
      );

      if (result == null || result.accessToken == null) {
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
      rethrow; 
    }
  }

  @override
  Future<void> logout() async {
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
    }
  }

  @override
  Future<bool> isAuthenticated() async {
    final refreshToken = await secureStorage.read(key: 'refresh_token');
    return refreshToken != null;
  }
  
  // =======================================================
  // CONTRATO: 2. GESTIÓN DE TOKENS
  // =======================================================

  @override
  Future<void> refreshTokens() async {
    final refreshToken = await secureStorage.read(key: 'refresh_token');

    if (refreshToken == null) {
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
          allowInsecureConnections: true
        ),
      );

      if (resp == null || resp.accessToken == null) {
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
      rethrow; 
    }
  }

  @override
  Future<String?> getAccessToken() async {
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
  
  // =======================================================
  // CONTRATO: 3. OBTENCIÓN DE DATOS DEL USUARIO
  // =======================================================

  @override
  Future<User> getUserInfo() async {
    final token = await getAccessToken();
    
    if (token == null) {
      throw Exception('Acceso no autorizado: No se pudo obtener el Access Token.');
    }
    
    // 1. Construir el URL del endpoint userinfo
    final userinfoUri = '$keycloakBaseUrl/protocol/openid-connect/userinfo';
    
    try {
      // 2. Realizar la solicitud HTTP usando el Dio inyectado (_keycloakDio)
      final response = await _keycloakDio.get(
        userinfoUri, 
        options: Options(
          headers: {
            'Authorization': 'Bearer $token',
            'Accept': 'application/json',
          },
        ),
      );

      // 3. Mapear
      if (response.data != null) {
        final Map<String, dynamic> jsonResponse = response.data;
        return User.fromJson(jsonResponse);
      } else {
        throw Exception('Fallo al obtener información del usuario. La respuesta está vacía.');
      }
      
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) {
        throw Exception('Token inválido o expirado. Se requiere re-autenticación.');
      }
      print('❌ Error Dio en getUserInfo: $e');
      rethrow; 
    } catch (e) {
      print('❌ Error general en getUserInfo: $e');
      rethrow;
    }
  }

  // =======================================================
  // AUXILIAR: callApi (Corregido para usar Dio)
  // =======================================================

  // Este método asume que será llamado por un Interceptor o un cliente que
  // también tiene un Dio inyectado (el cliente de la API).
  // Si este método *va a ser* llamado por el Interceptor, debe estar en la interfaz.
  // Pero si solo es un método auxiliar, la implementación cambia para usar el Dio correcto.
  // 🔑 NOTA: Ya que no tenemos el Dio del API inyectado aquí, lo ideal es
  // mover este método al repositorio de la API. Si es estrictamente necesario,
  // deberías inyectar el *otro* Dio también.
  // Asumiremos que este método debe ser removido o implementado en otro lugar
  // ya que la responsabilidad del AppAuthService es la autenticación, no la API.
  
  /*
  @override
  Future<Response> callApi(String path) async { // Deberías cambiar el tipo de retorno si es un método de la interfaz
    final token = await getAccessToken();
    if (token == null) {
      throw Exception('No access token disponible.');
    }
    
    // Aquí necesitarías el DIO configurado para la API, no _keycloakDio.
    // **Si lo dejas aquí, debes inyectar el Dio de la API también.**
    throw UnimplementedError('callApi debe ser implementado en el Repositorio de la API o este servicio debe inyectar el Dio del API.');
  }
  */
}