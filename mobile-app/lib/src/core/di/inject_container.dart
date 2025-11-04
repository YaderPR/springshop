import 'package:provider/provider.dart';
import 'package:provider/single_child_widget.dart';
import 'package:dio/dio.dart';
import 'package:springshop/src/auth/app_auth_service.dart';

import '../../core/theme/theme_notifier.dart';
import '../../auth/auth_repository.dart'; 
import '../../auth/auth_state_notifier.dart'; 

import '../api/auth_interceptor.dart'; 

// =======================================================
final AppAuthService _appAuthService = AppAuthService(); 

final Dio _dioClient = Dio(
  BaseOptions(
    baseUrl: 'http://localhost:8080/api/v2', 
    connectTimeout: const Duration(seconds: 15),
  ),
);

void _setupDioInterceptors() {
  _dioClient.interceptors.add(
    AuthInterceptor(),
  );
}


List<SingleChildWidget> buildAppProviders() {
  _setupDioInterceptors(); 
  
  return [
    ChangeNotifierProvider<ThemeNotifier>(
      create: (_) => ThemeNotifier(),
    ),
    Provider<AppAuthService>(
      create: (_) => _appAuthService,
    ),
    Provider<AuthRepository>(
      create: (_) => _appAuthService,
    ),

    Provider<Dio>(
      create: (_) => _dioClient, 
    ),
    ChangeNotifierProvider<AuthStateNotifier>(
      create: (context) => AuthStateNotifier(
        context.read<AuthRepository>(), 
      )..checkInitialAuthStatus(), 
      lazy: false,
    ),
  ];
}