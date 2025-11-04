import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/auth/auth_gate.dart';
import 'package:springshop/src/core/di/inject_container.dart';
import 'package:springshop/src/core/theme/theme_notifier.dart';


void main() {
  // Asegúrate de inicializar Flutter antes de usar el Provider (Buena práctica)
  WidgetsFlutterBinding.ensureInitialized(); 
  
  runApp(
    MultiProvider(
      providers: buildAppProviders(),
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final themeNotifier = context.watch<ThemeNotifier>();
    
    return MaterialApp(
      title: 'SpringShop Demo',
      themeMode: themeNotifier.themeMode,
      /// Tema Claro
      theme: ThemeData(
        brightness: Brightness.light,
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        scaffoldBackgroundColor: Colors.grey[100],
        appBarTheme: const AppBarTheme(backgroundColor: Colors.white, foregroundColor: Colors.black),
        useMaterial3: true,
      ),
      /// Tema Oscuro
      darkTheme: ThemeData(
        brightness: Brightness.dark,
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue, brightness: Brightness.dark),
        scaffoldBackgroundColor: Colors.black, 
        appBarTheme: const AppBarTheme(backgroundColor: Colors.black, foregroundColor: Colors.white),
        useMaterial3: true,
      ),
      
      // 🚀 ¡CLAVE! Usamos AuthGuard para manejar la redirección inicial y el estado.
      home: const AuthGuard(), 
    );
  }
}