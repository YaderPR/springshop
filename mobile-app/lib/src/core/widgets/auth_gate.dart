// lib/src/auth/auth_guard.dart (Modificado)

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../auth/auth_state_notifier.dart';
import '../../features/home/presentation/screens/home_screen.dart'; 
import '../../features/auth/presentation/screens/sign_in_screen.dart'; 

class AuthGuard extends StatelessWidget {
  const AuthGuard({super.key});

  @override
  Widget build(BuildContext context) {
    final authNotifier = context.watch<AuthStateNotifier>();
    if (authNotifier.isLoading) {
      print('⏳ [AuthGuard] Estado: Cargando. Mostrando SplashScreen.');
      return const LoadingScreen(); 
    }

    // 2. 🟢 Logueado
    if (authNotifier.isLoggedIn) {
      print('🟢 [AuthGuard] Estado: Autenticado. Mostrando HomeScreen.');
      return const HomeScreen();
    } 
    
    else {
      print('🔴 [AuthGuard] Estado: No Autenticado. Mostrando SignInScreen.');
      return const SignInScreen(isModal: false);
    }
  }
}

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