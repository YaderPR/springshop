// lib/src/auth/keycloak_auth_service.dart

import 'package:dio/dio.dart';
import 'package:flutter/services.dart'; 
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:flutter_web_auth_2/flutter_web_auth_2.dart';
import 'pkce_util.dart'; 

class KeycloakAuthService {
  final String _clientId = 'springshop-app-client';
  final String _redirectUri = 'http://springshop.auth/callback';
  final String _keycloakAuthority = 'http://10.185.74.191:9090/realms/master'; 

  String _codeVerifier = '';
  String _codeChallenge = '';
  final _storage = const FlutterSecureStorage();
  
  // =======================================================
  // 1. GESTIÓN DEL INICIO DE SESIÓN
  // =======================================================

  Future<void> signInWithKeycloak() async {
    print("🔑 INICIO: Llamada a signInWithKeycloak.");
    
    _codeVerifier = PkceUtil.generateCodeVerifier();
    _codeChallenge = PkceUtil.generateCodeChallenge(_codeVerifier);
    print("🔑 LOG: PKCE generado (Verifier: $_codeVerifier)");

    final authorizeUrl = Uri.parse('$_keycloakAuthority/protocol/openid-connect/auth')
      .replace(
        queryParameters: {
          'client_id': _clientId,
          'response_type': 'code',
          'scope': 'openid profile email',
          'redirect_uri': _redirectUri,
          'code_challenge': _codeChallenge,
          'code_challenge_method': 'S256',
          'prompt': 'login',
        },
      ).toString();
      
    print("🔑 LOG: URL de Autorización (Browser): $authorizeUrl");

    try {
      print("🔑 PASO 1: Lanzando FlutterWebAuth2.authenticate...");
      final result = await FlutterWebAuth2.authenticate(
        url: authorizeUrl,
        callbackUrlScheme: 'springshop.auth',
      );
      
      // 🚀 ¡CRÍTICO! ESTE LOG DEBERÍA APARECER AHORA:
      print("🔑 PASO 2: Callback recibido. Resultado: $result"); 
      
      final code = Uri.parse(result).queryParameters['code'];
      // ... (El resto del código)
      
    } catch (e) {
      // ❌ Este catch genérico ahora atrapará la PlatformException y la imprimirá
      //     con su formato completo, incluyendo, posiblemente, el URI defectuoso.
      print("❌ FALLO GENÉRICO EN AUTHENTICATE: $e");
      rethrow; 
    }
  }

  // =======================================================
  // 2. MÉTODOS DE SOPORTE (isAuthenticated, logout)
  // =======================================================
  
  Future<bool> isAuthenticated() async {
    final refreshToken = await _storage.read(key: 'refresh_token');
    print("🔑 ESTADO: Verificando autenticación. Refresh Token: ${refreshToken != null ? 'ENCONTRADO' : 'AUSENTE'}");
    return refreshToken != null;
  }
  
  Future<void> logout() async {
    print("🚪 LOGOUT: Limpiando tokens.");
    await _storage.delete(key: 'access_token');
    await _storage.delete(key: 'refresh_token');
  }

  // =======================================================
  // 3. INTERCAMBIO Y ALMACENAMIENTO DE TOKENS (DIAGNÓSTICO CLAVE)
  // =======================================================
  
  Future<void> _exchangeCodeForToken(String code) async {
    print("🔑 PASO 4: Iniciando intercambio de código por tokens.");
    final tokenEndpoint = '$_keycloakAuthority/protocol/openid-connect/token';
    final dio = Dio(); 

    try {
      print("🔑 PASO 5: POST a $tokenEndpoint con código.");
      final response = await dio.post(
        tokenEndpoint,
        options: Options(contentType: Headers.formUrlEncodedContentType),
        data: {
          'grant_type': 'authorization_code',
          'client_id': _clientId,
          'redirect_uri': _redirectUri,
          'code': code,
          'code_verifier': _codeVerifier, 
        },
      );

      print("🔑 PASO 6: Respuesta del servidor recibida (Status: ${response.statusCode}).");

      final accessToken = response.data['access_token'];
      final refreshToken = response.data['refresh_token'];

      if (accessToken != null && refreshToken != null) {
        await _securelyStoreTokens(accessToken, refreshToken);
        print("🔑 ÉXITO: Tokens guardados correctamente.");
      } else {
        throw Exception("Respuesta de token incompleta (faltan access_token o refresh_token).");
      }
      
    } on DioException catch (e) {
      // 🛑 Este bloque de error captura problemas de red o del servidor Keycloak.
      final errorBody = e.response?.data.toString() ?? 'Error sin cuerpo de respuesta.';
      
      print('❌ FALLO DIO (TOKEN ENDPOINT) -----------------');
      print('Status: ${e.response?.statusCode}');
      print('Cuerpo del error: $errorBody');
      print('URL: $tokenEndpoint');
      print('-------------------------------------------');

      throw Exception('Fallo en la obtención de tokens. Causa del servidor: $errorBody');
    } catch (e) {
      print('❌ FALLO INESPERADO: Error durante el intercambio: $e');
      rethrow;
    }
  }
  
  Future<void> _securelyStoreTokens(String accessToken, String refreshToken) async {
    print("🔑 LOG: Guardando tokens en Secure Storage.");
    await _storage.write(key: 'access_token', value: accessToken);
    await _storage.write(key: 'refresh_token', value: refreshToken);
  }
}