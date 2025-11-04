// auth_service.dart
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuthService {
  final FlutterAppAuth _appAuth = FlutterAppAuth();
  final FlutterSecureStorage _secureStorage = FlutterSecureStorage();

  // Config - cambia a tus valores
  final String clientId = 'springshop-app-client';
  final String issuer = 'http://10.185.74.191:9090/realms/master';
  final String redirectUrl = 'org.springshop:/oauthredirect';
  final List<String> scopes = ['openid', 'profile', 'offline_access', 'email'];

  Future<bool> login() async {
    final authorizationEndpoint = '$issuer/protocol/openid-connect/auth';
    final tokenEndpoint = '$issuer/protocol/openid-connect/token';

    try {
      final AuthorizationTokenRequest req = AuthorizationTokenRequest(
        clientId,
        redirectUrl,
        serviceConfiguration: AuthorizationServiceConfiguration(
          authorizationEndpoint: authorizationEndpoint,
          tokenEndpoint: tokenEndpoint,
        ),
        scopes: scopes,
        preferEphemeralSession: true,
        allowInsecureConnections: true
        // flutter_appauth genera PKCE internamente
      );

      final result = await _appAuth.authorizeAndExchangeCode(req);

      if (result != null) {
        await _secureStorage.write(
          key: 'access_token',
          value: result.accessToken,
        );
        await _secureStorage.write(
          key: 'refresh_token',
          value: result.refreshToken,
        );
        await _secureStorage.write(key: 'id_token', value: result.idToken);
        // opcional: guardar expires_in / token_type
        return true;
      }
      return false;
    } catch (e) {
      rethrow;
    }
  }

  Future<void> logout() async {
    await _secureStorage.deleteAll();
    // opcional: llamar al endpoint end_session (logout) de Keycloak si quieres invalidar sesión
  }

  Future<String?> getAccessToken() async {
    return await _secureStorage.read(key: 'access_token');
  }

  Future<bool> refreshTokenIfNeeded() async {
    final refresh = await _secureStorage.read(key: 'refresh_token');
    if (refresh == null) return false;

    try {
      final tokenResult = await _appAuth.token(
        TokenRequest(
          clientId,
          redirectUrl,
          refreshToken: refresh,
          serviceConfiguration: AuthorizationServiceConfiguration(
            authorizationEndpoint: '$issuer/protocol/openid-connect/auth',
            tokenEndpoint: '$issuer/protocol/openid-connect/token',
          ),
        ),
      );

      if (tokenResult != null) {
        await _secureStorage.write(
          key: 'access_token',
          value: tokenResult.accessToken,
        );
        if (tokenResult.refreshToken != null) {
          await _secureStorage.write(
            key: 'refresh_token',
            value: tokenResult.refreshToken!,
          );
        }
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }
}
