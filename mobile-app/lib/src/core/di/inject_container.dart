import 'package:provider/provider.dart';
import 'package:provider/single_child_widget.dart';
import 'package:dio/dio.dart';
import 'package:springshop/src/core/auth/app_auth_service.dart';
import 'package:springshop/src/core/config/app_config.dart';
import 'package:springshop/src/core/services/search_history_service.dart';
import 'package:springshop/src/features/categories/data/repositories/accessory_category_api_repository.dart';
import 'package:springshop/src/features/categories/data/repositories/apparel_category_api_repository.dart';
import 'package:springshop/src/features/categories/data/repositories/category_api_repository.dart';
import 'package:springshop/src/features/categories/domain/repositories/accessory_category_repository.dart';
import 'package:springshop/src/features/categories/domain/repositories/apparel_category_repository.dart';
import 'package:springshop/src/features/categories/domain/repositories/category_repository.dart';
import 'package:springshop/src/features/products/data/repositories/apparel_api_repository.dart';
import 'package:springshop/src/features/products/data/repositories/product_api_repository.dart';
import 'package:springshop/src/features/products/data/repositories/supplement_api_repository.dart';
import 'package:springshop/src/features/products/data/repositories/workout_accessory_api_repository.dart';
import 'package:springshop/src/features/products/data/services/apparel_service.dart';
import 'package:springshop/src/features/products/data/services/product_service.dart';
import 'package:springshop/src/features/products/data/services/supplement_service.dart';
import 'package:springshop/src/features/products/data/services/workout_accessory_service.dart';
import 'package:springshop/src/features/products/domain/repositories/apparel_repository.dart';
import 'package:springshop/src/features/products/domain/repositories/product_repository.dart';
import 'package:springshop/src/features/products/domain/repositories/supplement_repository.dart';
import 'package:springshop/src/features/products/domain/repositories/workout_accessory_repository.dart';

import '../../core/theme/theme_notifier.dart';
import '../auth/auth_repository.dart';
import '../auth/auth_state_notifier.dart';

import '../api/auth_interceptor.dart';

// =======================================================
// 1. CONFIGURACIÓN Y CLIENTES DIO (Inicialización de Singletone)
// =======================================================
final AppConfig _appConfig = AppConfig(
  // Configuración base de la API de tu backend
  apiBaseUrl: 'http://10.203.95.191:8080/api/v2',
);

// 1.1 CLIENTE DIO PARA EL BACKEND (Con BaseURL e Interceptor)
final Dio _apiDioClient = Dio(
  BaseOptions(
    baseUrl: _appConfig.apiBaseUrl,
    connectTimeout: const Duration(seconds: 15),
  ),
);


// 1.2 CLIENTE DIO PARA SERVICIOS EXTERNOS (Keycloak)
final Dio _keycloakDio = Dio(
  BaseOptions(connectTimeout: const Duration(seconds: 15)),
);

// 🔑 NUEVO: CLIENTE DIO PARA LLAMADAS DE AUTH/SYNC (Misma BaseURL, PERO SIN INTERCEPTOR)
final Dio _apiDioClientNoAuth = Dio(
  BaseOptions(
    baseUrl: _appConfig.apiBaseUrl,
    connectTimeout: const Duration(seconds: 15),
  ),
);

// 1.4 Inicialización del servicio de autenticación
final AppAuthService _appAuthService = AppAuthService(
  keycloakDio: _keycloakDio,
  // 🔑 USAMOS LA INSTANCIA AISLADA PARA LA SINCRONIZACIÓN
  apiGatewayDio: _apiDioClientNoAuth,
);

void _setupDioInterceptors() {
  // ⚠️ SÓLO EL CLIENTE PRINCIPAL (_apiDioClient) OBTIENE EL INTERCEPTOR
  _apiDioClient.interceptors.add(
    AuthInterceptor(authRepository: _appAuthService),
  );
  _apiDioClient.interceptors.add(LogInterceptor(
    requestBody: true,
    requestHeader: true,
    responseBody: true,
    responseHeader: true,
    logPrint: (obj) {
        // Imprime el token en un bloque para verificar que Dio lo construye completo
        if (obj.toString().contains('Authorization: Bearer')) {
            print('✅ Dio está construyendo el Header COMPLETO antes de enviar:');
            print(obj);
        } else {
            print(obj);
        }
    }
));
}

// =======================================================
// 2. CONFIGURACIÓN DE PROVIDERS
// =======================================================

List<SingleChildWidget> buildAppProviders() {
  _setupDioInterceptors(); // Configura el interceptor antes de usar _apiDioClient

  return [
    // --- Servicios de Configuración y Core ---
    ChangeNotifierProvider<ThemeNotifier>(create: (_) => ThemeNotifier()),
    Provider<AppConfig>(create: (_) => _appConfig),
    ListenableProvider<SearchHistoryService>(
      create: (context) => SearchHistoryService(),
    ),

    // --- Autenticación (Core) ---
    Provider<AppAuthService>(create: (_) => _appAuthService),
    Provider<AuthRepository>(create: (_) => _appAuthService),

    // --- Clientes HTTP ---
    // Proveer el cliente principal (con interceptor) para el resto de la API.
    Provider<Dio>(create: (_) => _apiDioClient),

    // --- Repositorios (Capa de Datos) ---
    Provider<CategoryRepository>(
      create: (context) => CategoryApiRepository(context.read<Dio>()),
    ),
    Provider<AccessoryCategoryRepository>(
      create: (context) => AccessoryCategoryApiRepository(context.read<Dio>()),
    ),
    Provider<ApparelCategoryRepository>(
      create: (context) => ApparelCategoryApiRepository(context.read<Dio>()),
    ),
    Provider<ProductRepository>(
      create: (context) => ProductApiRepository(context.read<Dio>()),
    ),

    Provider<ApparelRepository>(
      create: (context) => ApparelApiRepository(context.read<Dio>()),
    ),
    Provider<SupplementRepository>(
      create: (context) => SupplementApiRepository(context.read<Dio>()),
    ),
    Provider<WorkoutAccessoryRepository>(
      create: (context) => WorkoutAccessoryApiRepository(context.read<Dio>()),
    ),

    // --- Servicios de Dominio ---
    Provider<ProductService>(
      create: (context) => ProductService(context.read<ProductRepository>()),
    ),
    Provider<ApparelService>(
      create: (context) => ApparelService(
        context.read<ApparelRepository>(),
        context.read<ProductService>(),
      ),
    ),
    Provider<SupplementService>(
      create: (context) => SupplementService(
        context.read<SupplementRepository>(),
        context.read<ProductService>(),
      ),
    ),
    Provider<WorkoutAccessoryService>(
      create: (context) => WorkoutAccessoryService(
        context.read<WorkoutAccessoryRepository>(),
        context.read<ProductService>(),
      ),
    ),

    // --- Notificador de Estado de Autenticación ---
    ChangeNotifierProvider<AuthStateNotifier>(
      create: (context) =>
          AuthStateNotifier(context.read<AuthRepository>())
            ..checkInitialAuthStatus(),
      lazy: false,
    ),
  ];
}
