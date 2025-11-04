// lib/src/auth/auth_guard.dart (Modificado)

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'auth_state_notifier.dart';
import '../features/home/presentation/screens/home_screen.dart'; 
import '../features/auth/presentation/screens/sign_in_screen.dart'; 
//import '../screens/common/loading_screen.dart'; // 💡 Asume esta pantalla existe

class AuthGuard extends StatelessWidget {
  const AuthGuard({super.key});

  @override
  Widget build(BuildContext context) {
    final authNotifier = context.watch<AuthStateNotifier>();

    // 1. ⏳ Estado de Carga (Verificación Asíncrona)
    if (authNotifier.isLoading) {
      print('⏳ [AuthGuard] Estado: Cargando. Mostrando SplashScreen.');
      // Puedes usar un CircularProgressIndicator o una pantalla con logo
      return const LoadingScreen(); 
    }

    // 2. 🟢 Logueado
    if (authNotifier.isLoggedIn) {
      print('🟢 [AuthGuard] Estado: Autenticado. Mostrando HomeScreen.');
      return const HomeScreen();
    } 
    
    // 3. 🔴 No Logueado (Carga terminada)
    else {
      print('🔴 [AuthGuard] Estado: No Autenticado. Mostrando SignInScreen.');
      return const SignInScreen(isModal: false);
    }
  }
}

// 💡 Ejemplo simple para la pantalla de carga (crea un archivo si no existe)
class LoadingScreen extends StatelessWidget {
  const LoadingScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: 16),
            Text('Verificando sesión...'),
          ],
        ),
      ),
    );
  }
}