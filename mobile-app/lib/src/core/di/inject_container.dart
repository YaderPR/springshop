import 'package:provider/provider.dart';
import 'package:provider/single_child_widget.dart';
import 'package:dio/dio.dart';
import 'package:springshop/src/core/auth/app_auth_service.dart';
import 'package:springshop/src/core/config/app_config.dart';
import 'package:springshop/src/features/categories/data/repositories/category_api_repository.dart';
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
final AppAuthService _appAuthService = AppAuthService();
final AppConfig _appConfig = AppConfig(
  apiBaseUrl: 'http://10.183.167.191:8085/api/v2',
);

final Dio _dioClient = Dio(
  BaseOptions(
    baseUrl: _appConfig.apiBaseUrl,
    connectTimeout: const Duration(seconds: 15),
  ),
);

void _setupDioInterceptors() {
  _dioClient.interceptors.add(AuthInterceptor());
}

List<SingleChildWidget> buildAppProviders() {
  _setupDioInterceptors();

  return [
    ChangeNotifierProvider<ThemeNotifier>(create: (_) => ThemeNotifier()),
    Provider<AppAuthService>(create: (_) => _appAuthService),
    Provider<AuthRepository>(create: (_) => _appAuthService),
    Provider<AppConfig>(create: (_) => _appConfig),
    Provider<Dio>(create: (_) => _dioClient),
    Provider<CategoryRepository>(
      create: (context) => CategoryApiRepository(context.read<Dio>()),
    ),
    Provider<ProductRepository>(
      create: (context) => ProductApiRepository(context.read<Dio>()),
    ),
    Provider<ApparelRepository>(
      create: (context) => ApparelApiRepository(_dioClient),
    ),
    Provider<SupplementRepository>(
      create: (context) => SupplementApiRepository(_dioClient),
    ),
    Provider<WorkoutAccessoryRepository>(
      create: (context) => WorkoutAccessoryApiRepository(_dioClient),
    ),
    Provider<ProductService>(
      create: (context) => ProductService(context.read<ProductRepository>()),
    ),
    Provider<ApparelService>(
      create: (context) => ApparelService(context.read<ApparelRepository>()),
    ),
    Provider<SupplementService>(
      create: (context) => SupplementService(context.read<SupplementRepository>()),
    ),
    Provider<WorkoutAccessoryService>(
      create: (context) =>
          WorkoutAccessoryService(context.read<WorkoutAccessoryRepository>()),
    ),
    ChangeNotifierProvider<AuthStateNotifier>(
      create: (context) =>
          AuthStateNotifier(context.read<AuthRepository>())
            ..checkInitialAuthStatus(),
      lazy: false,
    ),
  ];
}
