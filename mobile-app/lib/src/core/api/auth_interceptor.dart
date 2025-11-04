// lib/src/core/api/auth_interceptor.dart

import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class AuthInterceptor extends Interceptor {
  final _storage = const FlutterSecureStorage();
  // Incluye lógica de refresco de token si el token expiró (omito por brevedad)

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) async {
    // 1. Leer el token almacenado
    final accessToken = await _storage.read(key: 'access_token');

    // 2. Si hay token y no es una solicitud al endpoint de token de Keycloak (para evitar ciclos)
    if (accessToken != null && !options.path.contains('token')) {
      // 3. Añadir el encabezado Bearer
      options.headers['Authorization'] = 'Bearer $accessToken';
    }

    super.onRequest(options, handler);
  }
  
  // ... (Aquí iría la lógica de refresh del token en caso de error 401)
}