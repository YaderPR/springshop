import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

// Asegúrate de usar la importación correcta para tu HomeScreen
import 'package:springshop/src/features/home/presentation/screens/home_screen.dart'; // 💡 Nuevo Home
import 'package:springshop/src/core/di/inject_container.dart';
import 'package:springshop/src/core/theme/theme_notifier.dart';

// NOTA: El import de AuthGate se mantiene, pero ya no se usa como 'home'
// import 'package:springshop/src/core/widgets/auth_gate.dart'; 


void main() {
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
      
      // 🚀 CORRECCIÓN: El punto de entrada es ahora la pantalla pública.
      // Las secciones protegidas serán envueltas por AuthGate dentro de HomeScreen.
      home: const HomeScreen(), 
    );
  }
}