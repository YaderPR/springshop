// lib/src/core/api/auth_interceptor.dart

import 'package:dio/dio.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:springshop/src/core/auth/auth_repository.dart';
import 'dart:async'; // Necesario para Completer

class AuthInterceptor extends Interceptor {
  final AuthRepository _authRepository;
  final _storage = const FlutterSecureStorage();
  
  // 🔑 Flag para controlar el estado de refresco (solo uno puede refrescar)
  static bool _isRefreshing = false; 
  
  // 🔑 Completer para notificar a las peticiones en espera cuando el refresh termine.
  static final _lockCompleter = Completer<void>(); 

  AuthInterceptor({required AuthRepository authRepository})
      : _authRepository = authRepository;
  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    final response = err.response;
    final originalRequest = err.requestOptions;

    // Solo interceptamos 401 que no sean peticiones de token
    if (response?.statusCode == 401 && !originalRequest.path.contains('token')) {
      print('🔴 401 no autorizado. Intentando refrescar token...');
      
      // 1. Si ya se está refrescando, esperamos.
      if (_isRefreshing) {
        print('🟡 Petición en espera: Ya hay un refresh en curso.');
        // Esperamos a que el Completer de la llamada principal se complete.
        await _lockCompleter.future; 
        
        // El token ya se refrescó, reintentamos inmediatamente con el nuevo token.
        final newAccessToken = await _storage.read(key: 'access_token');
        if (newAccessToken != null) {
            originalRequest.headers['Authorization'] = 'Bearer $newAccessToken';
            // Reintentamos usando la instancia de Dio disponible a través del handler
            // o clonando la configuración (como hiciste abajo).
            // Lo más limpio es reintentar la RequestOptions actualizada.
            return _retryRequest(originalRequest, handler);
        }
        
        // Si no hay token nuevo, forzamos el error (y probablemente logout).
        return handler.next(err); 
      }
      
      // 2. Si no se está refrescando, somos la llamada principal.
      try {
        _isRefreshing = true;
        
        // 3. Llamar al método de refresco de tokens
        await _authRepository.refreshTokens();

        // 4. Obtener el nuevo Access Token
        final newAccessToken = await _storage.read(key: 'access_token');
        
        // 5. Notificar a todas las peticiones en espera que el refresh terminó con éxito
        _lockCompleter.complete(); 
        
        if (newAccessToken != null) {
          // 6. Actualizar el encabezado y reintentar la solicitud original.
          originalRequest.headers['Authorization'] = 'Bearer $newAccessToken';
          return _retryRequest(originalRequest, handler);
        }
      } catch (refreshError) {
        print('❌ Fallo al refrescar o reintentar: $refreshError');
        // Notificar a las peticiones en espera que el refresh FALLÓ.
        // Las peticiones en espera recibirán la excepción y deberían forzar logout.
        if (!_lockCompleter.isCompleted) _lockCompleter.completeError(refreshError);
        
        // Forzar el logout y limpiar el estado
        await _authRepository.logout(); 
        return handler.next(err); // Pasamos el error original al llamador.
      } finally {
        _isRefreshing = false; 
      }
    }

    // Para cualquier otro error, simplemente pasamos el error.
    return handler.next(err);
  }

  // 🔑 Función auxiliar para reintentar una solicitud después de un refresh exitoso
  Future<void> _retryRequest(RequestOptions originalRequest, ErrorInterceptorHandler handler) async {
    try {
      final Dio retryDio = Dio(BaseOptions(
        baseUrl: originalRequest.baseUrl,
        connectTimeout: originalRequest.connectTimeout,
        receiveTimeout: originalRequest.receiveTimeout,
      ));

      final Response newResponse = await retryDio.request(
          originalRequest.path,
          data: originalRequest.data,
          queryParameters: originalRequest.queryParameters,
          options: Options(
            method: originalRequest.method,
            headers: originalRequest.headers,
            // Aquí puedes copiar otras opciones necesarias
          ),
      );
      
      handler.resolve(newResponse);
    } on DioException catch (e) {
      // Si el reintento falla por otras razones (e.g., error de red), pasamos el error.
      handler.next(e);
    }
  }
}