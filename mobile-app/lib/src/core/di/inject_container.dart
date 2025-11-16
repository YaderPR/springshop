import 'package:provider/provider.dart';
import 'package:provider/single_child_widget.dart';
import 'package:dio/dio.dart';
import 'package:springshop/src/core/auth/app_auth_service.dart';
import 'package:springshop/src/core/config/app_config.dart';
import 'package:springshop/src/core/services/search_history_service.dart';
import 'package:springshop/src/features/cart/data/services/cart_service.dart';
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
import 'package:springshop/src/features/cart/data/repositories/cart_api_repository.dart';
import 'package:springshop/src/features/cart/domain/repositories/cart_repository.dart';

import '../../core/theme/theme_notifier.dart';
import '../auth/auth_repository.dart';
import '../auth/auth_state_notifier.dart';
import '../api/auth_interceptor.dart';

// --- Configuraciones Iniciales de DIO y AuthService ---

final AppConfig _appConfig = AppConfig(
  apiBaseUrl: 'http://10.203.95.191:8080/api/v2',
);
final Dio _apiDioClient = Dio(
  BaseOptions(
    baseUrl: _appConfig.apiBaseUrl,
    connectTimeout: const Duration(seconds: 15),
  ),
);

final Dio _keycloakDio = Dio(
  BaseOptions(connectTimeout: const Duration(seconds: 15)),
);
final Dio _apiDioClientNoAuth = Dio(
  BaseOptions(
    baseUrl: _appConfig.apiBaseUrl,
    connectTimeout: const Duration(seconds: 15),
  ),
);

// AppAuthService se crea primero, pero se configura al final
final AppAuthService _appAuthService = AppAuthService(
  keycloakDio: _keycloakDio,
  apiGatewayDio: _apiDioClientNoAuth,
);

void _setupDioInterceptors() {
  _apiDioClient.interceptors.add(
    AuthInterceptor(authRepository: _appAuthService),
  );
  _apiDioClient.interceptors.add(
    LogInterceptor(
      requestBody: true,
      requestHeader: true,
      responseBody: true,
      responseHeader: true,
      logPrint: (obj) {
        if (obj.toString().contains('Authorization: Bearer')) {
          print('✅ Dio está construyendo el Header COMPLETO antes de enviar:');
          print(obj);
        } else {
          print(obj);
        }
      },
    ),
  );
}

// --- Lista de Providers ---

List<SingleChildWidget> buildAppProviders() {
  _setupDioInterceptors();

  // Paso 1: Inicializar todos los Repositorios y Servicios que NO son ChangeNotifier
  final dio = _apiDioClient;
  final productRepository = ProductApiRepository(dio);
  final cartRepository = CartApiRepository(dio);

  final productService = ProductService(productRepository);

  // Paso 2: Crear el CartService como una instancia simple (fuera del árbol de Providers)
  // Esto nos permite pasarlo a AppAuthService de forma síncrona.
  final cartService = CartService(cartRepository, productService);

  // Paso 3: Configurar el CartService en AppAuthService
  _appAuthService.setCartService(cartService);

  // Paso 4: Devolver la lista de Providers.
  return [
    ChangeNotifierProvider<ThemeNotifier>(create: (_) => ThemeNotifier()),
    Provider<AppConfig>(create: (_) => _appConfig),
    ListenableProvider<SearchHistoryService>(
      create: (context) => SearchHistoryService(),
    ),
    // Repositorios base - Ahora creados fuera o como Providers simples
    Provider<Dio>(create: (_) => dio),
    Provider<CategoryRepository>(
      create: (context) => CategoryApiRepository(context.read<Dio>()),
    ),
    Provider<AccessoryCategoryRepository>(
      create: (context) => AccessoryCategoryApiRepository(context.read<Dio>()),
    ),
    Provider<ApparelCategoryRepository>(
      create: (context) => ApparelCategoryApiRepository(context.read<Dio>()),
    ),
    // Repositorios que ya creamos o que usan otros Providers:
    Provider<ProductRepository>.value(value: productRepository),
    Provider<ApparelRepository>(
      create: (context) => ApparelApiRepository(context.read<Dio>()),
    ),
    Provider<SupplementRepository>(
      create: (context) => SupplementApiRepository(context.read<Dio>()),
    ),
    Provider<WorkoutAccessoryRepository>(
      create: (context) => WorkoutAccessoryApiRepository(context.read<Dio>()),
    ),
    Provider<CartRepository>.value(value: cartRepository),

    // --- Servicios de Dominio ---
    Provider<ProductService>.value(value: productService),
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

    // 💡 SERVICIO DE CARRITO (Usamos .value porque ya fue creado)
    ChangeNotifierProvider<CartService>.value(value: cartService),

    // --- Servicios de Autenticación ---
    Provider<AppAuthService>.value(value: _appAuthService),
    Provider<AuthRepository>.value(value: _appAuthService),

    // --- Notificador de Estado de Autenticación (Debe ir al final) ---
    ChangeNotifierProvider<AuthStateNotifier>(
      create: (context) =>
          AuthStateNotifier(context.read<AuthRepository>())
            ..checkInitialAuthStatus(),
      lazy: false,
    ),
  ];
}
