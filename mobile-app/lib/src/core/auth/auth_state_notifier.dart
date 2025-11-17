import 'package:flutter/foundation.dart';
import 'package:springshop/src/core/auth/auth_repository.dart';
import 'package:springshop/src/features/auth/domain/entities/user.dart';
import 'package:springshop/src/features/cart/data/services/cart_service.dart';

class AuthStateNotifier extends ChangeNotifier {
  final AuthRepository _authService;
  final CartService _cartService; // Se mantiene para la limpieza en logout

  // ... Estados de la Sesión y Propiedades (sin cambios) ...
  bool _isLoggedIn = false;
  bool get isLoggedIn => _isLoggedIn;
  bool _isLoading = true;
  bool get isLoading => _isLoading;
  User? _user;
  User? get user => _user;

  AuthStateNotifier(this._authService, this._cartService);

  // 1. Lógica Central para Obtener Datos del Usuario (REMOVEMOS _fetchAndSetUser)
  // Ahora la lógica de obtención y sincronización se hace directamente en el repositorio

  // 🚀 2. Verifica la sesión al inicio de la App (MODIFICADO)
  Future<void> checkInitialAuthStatus() async {
    print(
      '🔑 [Notifier.checkInitialAuthStatus] Iniciando verificación de tokens...',
    );
    _isLoading = true;
    notifyListeners();

    try {
      final hasTokens = await _authService.isAuthenticated();
      print('🔎 [Notifier] ¿Hay tokens guardados? $hasTokens');

      if (hasTokens) {
        print(
          '⏳ [Notifier] Tokens encontrados. Intentando obtener detalles de usuario y sincronizar carrito...',
        );

        // 🎯 CLAVE: Llamamos al nuevo método que hace la sincronización
        // de datos (obteniendo el ID interno) y la inicialización del carrito en una sola llamada.
        await _authService
            .syncUserAndInitializeCart()
            .then((syncedUser) {
              _user = syncedUser;
              _isLoggedIn = true;
              print(
                '✅ [Notifier] Sincronización de inicio exitosa. Usuario ID: ${_user!.id}',
              );
            })
            .catchError((e) {
              // Si falla la sincronización (ej: token Keycloak válido, pero API Gateway caído)
              print(
                '❌ [Notifier] Fallo en la sincronización del usuario/carrito durante el chequeo inicial: $e',
              );
              _user = null;
              _isLoggedIn = false;
              // Limpiar estado en caso de fallo
              _cartService.clear();
              // Esto evita que la excepción rompa el flujo de inicio de la aplicación, pero limpia la sesión.
            });
      } else {
        print('🚫 [Notifier] No se encontraron tokens. Usuario no logeado.');
        _isLoggedIn = false;
      }
    } catch (e) {
      print(
        '❌ [Notifier.checkInitialAuthStatus] Error durante la verificación/sincronización: $e',
      );
      _isLoggedIn = false;
    } finally {
      _isLoading = false;
      notifyListeners();
      print(
        '🔑 [Notifier.checkInitialAuthStatus] Finalizado. isLoggedIn: $_isLoggedIn',
      );
    }
  }

  // 🔑 3. Manejo de Inicio de Sesión (Llamada al método completo)
  Future<void> login() async {
    try {
      print('🔑 [Notifier.login] Iniciando flujo OIDC...');
      await _authService.signIn();

      print(
        '⏳ [Notifier.login] Obteniendo datos detallados de Keycloak y sincronizando ID interno/Carrito...',
      );
      // getAndSyncUser ya maneja la inicialización del carrito
      final userModel = await _authService.getAndSyncUser();

      _user = userModel;
      _isLoggedIn = true;
      notifyListeners();

      print('✅ [Notifier] Login completo. Usuario ID: ${_user!.id}');
    } catch (e) {
      print('❌ [Notifier] Fallo en el flujo de login: $e');
      _isLoggedIn = false;
      _user = null;
      _cartService.clear();
      notifyListeners();
      rethrow;
    }
  }

  // 🚪 4. Manejo de Cierre de Sesión (SIN CAMBIOS)
  Future<void> logout() async {
    try {
      await _authService.logout();
    } catch (_) {
      print(
        '⚠️ [Notifier] Advertencia: Fallo silencioso en el logout del servicio.',
      );
    } finally {
      print('🗑️ [Notifier] Limpiando estado local.');
      _isLoggedIn = false;
      _user = null;
      _cartService.clear();
      notifyListeners();
    }
  }
}
