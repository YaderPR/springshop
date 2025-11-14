import 'package:flutter/foundation.dart';
import 'package:springshop/src/features/auth/domain/user.dart'; // 💡 Importar la entidad User
import 'auth_repository.dart'; // 💡 Usar la interfaz, no la clase concreta

class AuthStateNotifier extends ChangeNotifier {
  final AuthRepository _authService;
  
  // --- Estados de la Sesión ---
  bool _isLoggedIn = false;
  bool get isLoggedIn => _isLoggedIn;
  
  bool _isLoading = true; 
  bool get isLoading => _isLoading;
  
  // 🔑 NUEVA PROPIEDAD: Entidad de Usuario
  User? _user;
  User? get user => _user; 

  AuthStateNotifier(this._authService);

  // 1. Lógica Central para Obtener Datos del Usuario (Reutilizable)
  Future<void> _fetchAndSetUser() async {
    try {
      // 🚀 Llama al nuevo método del repositorio
      _user = await _authService.getUserInfo(); 
      _isLoggedIn = true;
      print('✅ [Notifier] Datos de usuario cargados: ${_user?.fullName}');
    } catch (e) {
      // Si falla getUserInfo (ej: token inválido), limpiamos la sesión.
      print('❌ [Notifier] Fallo al cargar datos de usuario: $e');
      _user = null;
      _isLoggedIn = false;
      // Opcional: Llamar a logout para limpiar tokens inválidos si el error lo requiere.
    }
  }

  // 🚀 2. Verifica la sesión al inicio de la App (Actualizado)
  @override
  Future<void> checkInitialAuthStatus() async {
    print('🔑 [Notifier.checkInitialAuthStatus] Iniciando verificación de tokens...');
    _isLoading = true; 
    notifyListeners();

    try {
      // Primero verificamos si hay tokens
      final hasTokens = await _authService.isAuthenticated();
      
      if (hasTokens) {
        // Si hay tokens, intentamos cargar los datos del usuario.
        await _fetchAndSetUser(); 
      } else {
        _isLoggedIn = false;
      }

    } catch (e) {
      print('❌ [Notifier.checkInitialAuthStatus] Error durante la verificación: $e');
      _isLoggedIn = false;
    } finally {
      _isLoading = false; 
      notifyListeners();
      print('🔑 [Notifier.checkInitialAuthStatus] Finalizado. isLoggedIn: $_isLoggedIn');
    }
  }
  
  // 🔑 3. Manejo de Inicio de Sesión (Actualizado)
  @override
  Future<void> login() async {
    try {
      await _authService.signIn(); 
      
      // 🚀 Después de un signIn exitoso, OBTENEMOS la información del usuario
      await _fetchAndSetUser();
      
      notifyListeners();
      
    } catch (e) {
      _isLoggedIn = false;
      _user = null; // Limpiar el usuario en caso de fallo
      notifyListeners();
      rethrow; 
    }
  }

  // 🚪 4. Manejo de Cierre de Sesión (Actualizado)
  @override
  Future<void> logout() async {
    try {
      await _authService.logout(); 
    } catch (_) {
      // Ignorar errores de logout para asegurar la limpieza local
    } finally {
      // 🚀 Limpiamos el usuario y el estado
      _isLoggedIn = false;
      _user = null; 
      notifyListeners();
    }
  }
}