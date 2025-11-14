// lib/src/features/profile/presentation/screens/theme_setting_screen.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/core/theme/theme_notifier.dart'; // 💡 Importamos el Notifier

class ThemeSettingScreen extends StatelessWidget {
  const ThemeSettingScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final themeNotifier = context.watch<ThemeNotifier>();
    final currentThemeMode = themeNotifier.themeMode;
    
    // 🔑 ACCIÓN: Obtenemos solo el Notifier para llamar a los métodos de cambio
    final themeChanger = context.read<ThemeNotifier>();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Configuración de Tema'),
        backgroundColor: Theme.of(context).colorScheme.surface,
        elevation: 1,
      ),
      body: ListView(
        children: [
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Text(
              'Selecciona tu modo de visualización',
              style: Theme.of(context).textTheme.titleMedium,
            ),
          ),
          
          // 1. Modo Claro
          ListTile(
            leading: const Icon(Icons.wb_sunny_outlined),
            title: const Text('Claro'),
            trailing: currentThemeMode == ThemeMode.light
                ? Icon(Icons.check_circle, color: Theme.of(context).colorScheme.primary)
                : null,
            onTap: () {
              // 🚀 Lógica para cambiar a tema claro: Llamamos a toggleTheme(false)
              themeChanger.toggleTheme(false); 
              Navigator.pop(context); // Opcional: Volver atrás al seleccionar
            },
          ),

          // 2. Modo Oscuro
          ListTile(
            leading: const Icon(Icons.nightlight_round),
            title: const Text('Oscuro'),
            trailing: currentThemeMode == ThemeMode.dark
                ? Icon(Icons.check_circle, color: Theme.of(context).colorScheme.primary)
                : null,
            onTap: () {
              // 🚀 Lógica para cambiar a tema oscuro: Llamamos a toggleTheme(true)
              themeChanger.toggleTheme(true);
              Navigator.pop(context); // Opcional: Volver atrás al seleccionar
            },
          ),
          
          // 3. Modo Predeterminado del Sistema
          ListTile(
            leading: const Icon(Icons.settings_suggest_outlined),
            title: const Text('Sistema'),
            trailing: currentThemeMode == ThemeMode.system
                ? Icon(Icons.check_circle, color: Theme.of(context).colorScheme.primary)
                : null,
            onTap: () {
              // 🚀 Lógica para volver a usar el tema del sistema
              themeChanger.setSystemTheme();
              Navigator.pop(context); // Opcional: Volver atrás al seleccionar
            },
          ),
        ],
      ),
    );
  }
}