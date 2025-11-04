// lib/src/auth/auth_state_notifier.dart

import 'package:flutter/foundation.dart';
import 'keycloak_auth_service.dart'; 

class AuthStateNotifier extends ChangeNotifier {
  // 💡 El estado principal de la aplicación
  bool _isLoggedIn = false;
  bool get isLoggedIn => _isLoggedIn;

  final KeycloakAuthService _authService;

  // Constructor que recibe el servicio de Keycloak por inyección de dependencia
  AuthStateNotifier(this._authService);

  // 🚀 1. Verifica la sesión al inicio de la App
  Future<void> checkInitialAuthStatus() async {
    _isLoggedIn = await _authService.isAuthenticated();
    notifyListeners();
  }
  
  // 🔑 2. Manejo de Inicio de Sesión
  Future<void> login() async {
    try {
      // Llama a la lógica de Keycloak
      await _authService.signInWithKeycloak();
      
      // Si llega aquí, KeycloakAuthService debería haber guardado los tokens.
      _isLoggedIn = await _authService.isAuthenticated(); 
      
      // Si el login fue exitoso, notifica a la UI (ej: AuthGate)
      notifyListeners();
      
    } catch (e) {
      // Maneja errores (ej: el usuario cancela o el intercambio falla).
      _isLoggedIn = false;
      notifyListeners();
      // Re-lanza la excepción para que la SignInScreen pueda mostrar un mensaje.
      rethrow; 
    }
  }

  // 🚪 3. Manejo de Cierre de Sesión
  Future<void> logout() async {
    // Llama al servicio para invalidar y limpiar tokens.
    await _authService.logout(); 
    
    // Actualiza el estado local y notifica.
    _isLoggedIn = false;
    notifyListeners();
  }
}