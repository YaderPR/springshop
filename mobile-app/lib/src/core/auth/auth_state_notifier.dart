import 'package:flutter/foundation.dart';
import 'auth_repository.dart'; // 💡 Usar la interfaz, no la clase concreta

class AuthStateNotifier extends ChangeNotifier {
  bool _isLoggedIn = false;
  bool get isLoggedIn => _isLoggedIn;
  
  bool _isLoading = true; 
  bool get isLoading => _isLoading;

  final AuthRepository _authService; 

  AuthStateNotifier(this._authService);

  // 🚀 1. Verifica la sesión al inicio de la App
  Future<void> checkInitialAuthStatus() async {
    print('🔑 [Notifier.checkInitialAuthStatus] Iniciando verificación de tokens...');
    _isLoading = true; // Empezar cargando
    notifyListeners();

    try {
      _isLoggedIn = await _authService.isAuthenticated();
      print('🔑 [Notifier.checkInitialAuthStatus] Verificación completada. isLoggedIn: $_isLoggedIn');
    } catch (e) {
      print('❌ [Notifier.checkInitialAuthStatus] Error durante la verificación: $e');
      _isLoggedIn = false;
    } finally {
      _isLoading = false; // Finalizar la carga
      notifyListeners();
    }
  }
  
  // 🔑 2. Manejo de Inicio de Sesión
  Future<void> login() async {
    try {
      // Usar signIn del contrato
      await _authService.signIn(); 
      
      // La verificación debe ser inmediata después del éxito
      _isLoggedIn = await _authService.isAuthenticated(); 
      
      notifyListeners();
      
    } catch (e) {
      // Maneja errores y notifica
      _isLoggedIn = false;
      notifyListeners();
      rethrow; 
    }
  }

  // 🚪 3. Manejo de Cierre de Sesión
  Future<void> logout() async {
    try {
      // Usar logout del contrato
      await _authService.logout(); 
    } catch (_) {
      // Ignorar errores de logout para asegurar la limpieza local
    } finally {
      _isLoggedIn = false;
      notifyListeners();
    }
  }
}