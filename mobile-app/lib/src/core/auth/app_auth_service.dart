import 'package:dio/dio.dart';
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:springshop/src/core/auth/auth_repository.dart';
import 'package:springshop/src/features/auth/domain/entities/user.dart';
import 'package:springshop/src/features/cart/data/services/cart_service.dart'; // 💡 AGREGADO: Importar CartService
import 'dart:convert'; // AGREGADO: Necesario si se necesita decodificar JWT (lo mantengo por si acaso)

final FlutterAppAuth appAuth = FlutterAppAuth();
final FlutterSecureStorage secureStorage = FlutterSecureStorage();

// --- Configuración de Keycloak ---
const String clientId = 'springshop-app-client';
const String realm = 'master';
const String keycloakBaseUrl = 'http://172.24.84.191:9090/realms/$realm';
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
  // 🔑 PROPIEDADES AGREGADAS PARA EL CARRO
  CartService? _cartService;

  // 🔑 INYECCIÓN: Propiedad privada para la instancia de Dio inyectada (para Keycloak)
  final Dio _keycloakDio;
  final Dio _apiGatewayDio;

  // 🔑 CONSTRUCTOR: Usamos un constructor con nombre claro o solo posicional
  AppAuthService({required Dio keycloakDio, required Dio apiGatewayDio})
    : _keycloakDio = keycloakDio,
      _apiGatewayDio = apiGatewayDio;

  // =======================================================
  // LÓGICA DE CARRO ACOPLADA
  // =======================================================

  /// 🔑 SETTER: Para inyectar el CartService desde el Provider.
  void setCartService(CartService service) {
    _cartService = service;
  }

  /// 🎯 FUNCIÓN DE INICIALIZACIÓN DEL CARRO
  Future<void> setCartIdAfterLogin(int userId) async {
    if (_cartService != null && userId != 0) {
      print('🚀 Usuario $userId autenticado. Inicializando carrito...');
      await _cartService!.initializeCart(userId);
    }
  }

  // =======================================================
  // CONTRATO: 1. GESTIÓN DEL INICIO Y LOGOUT
  // =======================================================

  @override
  Future<void> signIn() async {
    try {
      final AuthorizationTokenResponse? result = await appAuth
          .authorizeAndExchangeCode(
            AuthorizationTokenRequest(
              clientId,
              redirectUri,
              serviceConfiguration: _serviceConfiguration,
              scopes: scopes,
              promptValues: ['login'],
              allowInsecureConnections: true,
            ),
          );

      if (result == null || result.accessToken == null) {
        throw Exception(
          "El flujo de autenticación terminó sin recibir tokens válidos.",
        );
      }

      await secureStorage.write(key: 'access_token', value: result.accessToken);
      await secureStorage.write(
        key: 'refresh_token',
        value: result.refreshToken,
      );
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
  Future<User> getAndSyncUser() async {
    // 1. OBTENER DATOS DETALLADOS DE KEYCLOAK (USER INFO)
    // Usamos el método existente getUserInfo()
    User keycloakUser;
    try {
      keycloakUser = await getUserInfo();
      print('✅ Datos de Keycloak obtenidos. SUB: ${keycloakUser.sub}');
    } catch (e) {
      print('❌ Fallo al obtener UserInfo de Keycloak: $e');
      rethrow;
    }

    // 2. SINCRONIZAR CON EL API GATEWAY PARA OBTENER EL ID INTERNO
    final token = await getAccessToken();
    const syncPath = '/users/me/sync';

    try {
      final response = await _apiGatewayDio.post(
        syncPath,
        options: Options(
          headers: {
            'Authorization': 'Bearer $token',
            'Content-Type': 'application/json',
          },
        ),
      );

      // ====================================================================
      // CLAVE DE DEPURACIÓN SYNC
      // ====================================================================
      print(
        '🔎 RESPUESTA SYNC: Status: ${response.statusCode}, Data: ${response.data}',
      );

      // La respuesta del backend solo tiene ID, SUB, USERNAME
      if (response.data != null && response.statusCode == 200) {
        final Map<String, dynamic> jsonResponse = response.data;
        final String internalId =
            jsonResponse['id']?.toString() ??
            keycloakUser.id; // Usar el ID interno

        // 3. FUSIONAR DATOS: Tomar todos los detalles de Keycloak y sobreescribir el ID
        final mergedUser = keycloakUser.copyWith(
          id: internalId, // Usamos el ID del User Service
        );

        print('✅ Sincronización finalizada. ID interno: ${mergedUser.id}');

        // 🎯 LÓGICA DE CARRITO: Llama a la función auxiliar
        final int? cartUserId = int.tryParse(internalId);
        if (cartUserId != null) {
          await _setCartIdAfterLogin(cartUserId); // USAMOS LA FUNCIÓN AUXILIAR
        }
        // --------------------------------------------------

        return mergedUser;
      } else {
        // Fallo en la sincronización, pero devolver el user de Keycloak con ID temporal
        print(
          '❌ Fallo en la sincronización. Devolviendo datos crudos de Keycloak.',
        );
        throw Exception(
          'Fallo al obtener ID interno del usuario. Status: ${response.statusCode}',
        );
      }
    } on DioException catch (e) {
      print('❌ Error Dio en getAndSyncUser: $e');
      rethrow;
    } catch (e) {
      print('❌ Excepción genérica atrapada en getAndSyncUser: $e');
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
  // CONTRATO: 2. GESTIÓN DE TOKENS (SIN CAMBIOS)
  // =======================================================

  @override
  Future<void> refreshTokens() async {
    final refreshToken = await secureStorage.read(key: 'refresh_token');

    if (refreshToken == null) {
      throw Exception(
        'No refresh token disponible, forzando cierre de sesión.',
      );
    }

    try {
      final TokenResponse? resp = await appAuth.token(
        TokenRequest(
          clientId,
          redirectUri,
          refreshToken: refreshToken,
          serviceConfiguration: _serviceConfiguration,
          scopes: scopes,
          allowInsecureConnections: true,
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
    final expirationString = await secureStorage.read(
      key: 'access_token_expiration',
    );
    final accessToken = await secureStorage.read(key: 'access_token');

    if (accessToken == null) return null;

    if (expirationString != null) {
      final expirationDate = DateTime.parse(expirationString);
      // Refrescar si falta menos de 5 minutos para expirar
      if (expirationDate
          .subtract(const Duration(minutes: 5))
          .isBefore(DateTime.now())) {
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
  // CONTRATO: 3. OBTENCIÓN DE DATOS DEL USUARIO (SIN CAMBIOS)
  // =======================================================

  @override
  Future<User> getUserInfo() async {
    final token = await getAccessToken();

    if (token == null) {
      throw Exception(
        'Acceso no autorizado: No se pudo obtener el Access Token.',
      );
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
        throw Exception(
          'Fallo al obtener información del usuario. La respuesta está vacía.',
        );
      }
    } on DioException catch (e) {
      if (e.response?.statusCode == 401) {
        throw Exception(
          'Token inválido o expirado. Se requiere re-autenticación.',
        );
      }
      print('❌ Error Dio en getUserInfo: $e');
      rethrow;
    } catch (e) {
      print('❌ Error general en getUserInfo: $e');
      rethrow;
    }
  }

  Future<void> _setCartIdAfterLogin(int userId) async {
    if (_cartService != null && userId != 0) {
      print('🚀 Usuario $userId autenticado. Inicializando carrito...');
      await _cartService!.initializeCart(userId);
    }
  }

  // =======================================================
  // IMPLEMENTACIÓN NUEVA: Sincronización al inicio de la App
  // =======================================================
  @override
  Future<User?> syncUserAndInitializeCart() async {
    try {
      // 1. Obtener datos de Keycloak (Necesario para tener un token válido)
      final keycloakUser = await getUserInfo();
      print('✅ [AppAuthService] UserInfo de Keycloak obtenido en el inicio: SUB ${keycloakUser.sub}');

      // 2. Sincronizar con el API Gateway para obtener el ID interno (Lógica de getAndSyncUser)
      final token = await getAccessToken();
      const syncPath = '/users/me/sync';

      final response = await _apiGatewayDio.post(
          syncPath,
          options: Options(headers: {'Authorization': 'Bearer $token'}),
      );

      if (response.data != null && response.statusCode == 200) {
        final Map<String, dynamic> jsonResponse = response.data;
        final String internalId = jsonResponse['id']?.toString() ?? keycloakUser.id;

        // 3. Fusionar y crear el modelo de usuario completo
        final mergedUser = keycloakUser.copyWith(id: internalId);
        
        print('✅ [AppAuthService] Sincronización exitosa. ID interno: $internalId');

        // 4. Inicializar el carrito
        final int? cartUserId = int.tryParse(internalId);
        if (cartUserId != null) {
          await _setCartIdAfterLogin(cartUserId);
        }

        return mergedUser;
      } else {
        throw Exception('Fallo al obtener ID interno durante el chequeo inicial. Status: ${response.statusCode}');
      }
    } catch (e) {
      print('❌ [AppAuthService.syncUserAndInitializeCart] Fallo en la sincronización: $e');
      // No rethrow aquí; el AuthStateNotifier lo maneja limpiando la sesión si es necesario.
      return null;
    }
  }
}
