// lib/src/core/theme/theme_notifier.dart

import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart'; // 💡 Importar SharedPreferences

class ThemeNotifier extends ChangeNotifier {
  // 🔑 Clave para SharedPreferences
  static const String _themeKey = 'themeMode';

  ThemeMode _themeMode = ThemeMode.system;
  ThemeMode get themeMode => _themeMode;

  // 1. Constructor: Carga el tema al iniciar
  ThemeNotifier() {
    _loadTheme();
  }

  // ----------------------------------------------------
  // Lógica de persistencia
  // ----------------------------------------------------

  // Método auxiliar para convertir ThemeMode a String
  String _themeModeToString(ThemeMode mode) {
    return mode.toString().split('.').last;
  }

  // Método auxiliar para convertir String a ThemeMode
  ThemeMode _stringToThemeMode(String? mode) {
    switch (mode) {
      case 'light':
        return ThemeMode.light;
      case 'dark':
        return ThemeMode.dark;
      case 'system':
      default:
        // Si no se encuentra clave o es inválida, se usa 'system' por defecto
        return ThemeMode.system;
    }
  }

  // Carga el tema guardado en SharedPreferences
  Future<void> _loadTheme() async {
    final prefs = await SharedPreferences.getInstance();
    final savedTheme = prefs.getString(_themeKey);
    
    _themeMode = _stringToThemeMode(savedTheme);
    print('🎨 [Theme] Tema cargado: ${_themeMode}');
    notifyListeners(); // Notifica la actualización inicial
  }

  // Guarda la preferencia de tema en SharedPreferences
  Future<void> _saveTheme(ThemeMode mode) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_themeKey, _themeModeToString(mode));
  }
  
  // ----------------------------------------------------
  // Métodos de acción del usuario (Actualizados para guardar)
  // ----------------------------------------------------

  // 1. Método para el interruptor del usuario
  void toggleTheme(bool isDark) {
    final newMode = isDark ? ThemeMode.dark : ThemeMode.light;
    if (_themeMode != newMode) {
        _themeMode = newMode;
        _saveTheme(_themeMode); // 💾 Guardar
        notifyListeners();
        print('🎨 [Theme] Tema cambiado a: ${_themeMode}');
    }
  }
  
  // 2. Método para restaurar al modo automático
  void setSystemTheme() {
    if (_themeMode != ThemeMode.system) {
        _themeMode = ThemeMode.system;
        _saveTheme(_themeMode); // 💾 Guardar
        notifyListeners();
        print('🎨 [Theme] Tema cambiado a: ${_themeMode}');
    }
  }
}