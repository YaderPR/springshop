// lib/src/core/di/injector_container.dart

import 'package:provider/provider.dart';
import 'package:provider/single_child_widget.dart';
import 'package:dio/dio.dart';
import 'package:springshop/src/auth/auth_state_notifier.dart';

// Importar todos los módulos necesarios
import '../../core/theme/theme_notifier.dart';
import '../../auth/keycloak_auth_service.dart'; // 💡 NECESITAS ESTO
import '../api/auth_interceptor.dart'; // 💡 NECESITAS ESTO

// =======================================================
// 1. Instancias Globales/Singleton (Necesarias para la inyección)
// =======================================================

// 1.1. Inicializar el servicio de autenticación
final KeycloakAuthService _authService = KeycloakAuthService(); 

// 1.2. Inicializar el cliente HTTP (Dio)
// Define la URL del Servidor de Recursos (NO el de Keycloak)
final Dio _dioClient = Dio(
  BaseOptions(
    baseUrl: 'http://localhost:8080/api/v2', 
    connectTimeout: const Duration(seconds: 15),
  ),
);

// 1.3. Configurar Interceptores en el cliente Dio
void _setupDioInterceptors() {
  _dioClient.interceptors.add(
    AuthInterceptor(),
  );
}

// =======================================================
// 2. Función de Registro de Providers (Usada en main.dart)
// =======================================================

List<SingleChildWidget> buildAppProviders() {
  // Asegurarse de configurar los interceptores una sola vez
  _setupDioInterceptors(); 
  
  return [
    ChangeNotifierProvider<ThemeNotifier>(
      create: (_) => ThemeNotifier(),
    ),
    Provider<Dio>(
      create: (_) => _dioClient, 
    ),
    Provider<KeycloakAuthService>(
      create: (_) => _authService,
    ),
    ChangeNotifierProvider<AuthStateNotifier>(
      create: (context) => AuthStateNotifier(
        context.read<KeycloakAuthService>(), 
      )..checkInitialAuthStatus(), 
      lazy: false,
    ),
  ];
}