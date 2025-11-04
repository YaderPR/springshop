import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/auth/auth_state_notifier.dart';
import 'package:springshop/src/features/auth/presentation/screens/sign_in_screen.dart'; 

class AuthGate extends StatelessWidget {
  final Widget protectedContent;
  final String featureName;

  const AuthGate({
    super.key,
    required this.protectedContent,
    required this.featureName,
  });

  @override
  Widget build(BuildContext context) {
    final authNotifier = context.watch<AuthStateNotifier>();

    if (authNotifier.isLoggedIn) {
      // 🟢 Logueado: Muestra el contenido real.
      return protectedContent;
    } else {
      // 🟠 No Logueado: Muestra el botón y la invitación al login.
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Debes iniciar sesión para acceder a $featureName.'),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: () {
                // 🔑 Navegar a la pantalla de login.
                Navigator.of(context).push(
                  MaterialPageRoute(
                    // Pasamos un flag para saber si esta pantalla debe cerrarse
                    builder: (context) => const SignInScreen(isModal: true), 
                  ),
                );
              },
              child: const Text('Iniciar Sesión'),
            ),
          ],
        ),
      );
    }
  }
}