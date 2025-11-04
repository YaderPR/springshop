// lib/src/core/theme/theme_notifier.dart

import 'package:flutter/material.dart';

class ThemeNotifier extends ChangeNotifier {
  // 💡 Por defecto, el modo es System (automático)
  ThemeMode _themeMode = ThemeMode.system;

  ThemeMode get themeMode => _themeMode;

  // 1. Método para el interruptor del usuario
  void toggleTheme(bool isDark) {
    // Si isDark es true, el modo es oscuro, sino es claro.
    // Si queremos darle la opción de volver a System, tendríamos que usar un enum
    // pero para empezar, vamos con Dark/Light forzado.
    _themeMode = isDark ? ThemeMode.dark : ThemeMode.light;
    notifyListeners();
  }
  
  // 2. Método para restaurar al modo automático (opcional, pero útil)
  void setSystemTheme() {
    _themeMode = ThemeMode.system;
    notifyListeners();
  }
}