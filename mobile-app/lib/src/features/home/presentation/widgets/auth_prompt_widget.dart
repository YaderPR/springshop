// lib/src/features/home/presentation/widgets/auth_prompt_widget.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
// 🔑 Imports necesarios
import 'package:springshop/src/core/auth/auth_state_notifier.dart'; 
import 'package:springshop/src/features/auth/presentation/screens/sign_in_screen.dart'; 

class AuthPromptWidget extends StatelessWidget {
  const AuthPromptWidget({super.key});

  // 💡 Método para abrir la pantalla de login como modal
  void _navigateToSignIn(BuildContext context) {
    Navigator.of(context).push(
      MaterialPageRoute(
        // Indicamos que es modal para que sepa hacer pop() al terminar el login
        builder: (context) => const SignInScreen(isModal: true), 
        fullscreenDialog: true, // Estilo modal en iOS
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    // 🔑 Escucha los cambios de estado de autenticación
    final authNotifier = context.watch<AuthStateNotifier>();

    // ----------------------------------------------------
    // 🟢 ESTADO 1: USUARIO AUTENTICADO
    // ----------------------------------------------------
    if (authNotifier.isLoggedIn) {
      // Devolver un widget de bienvenida simple cuando el usuario está logueado.
      // (En una app real, aquí se mostraría el nombre del usuario o un enlace al perfil)
      return Container(
        color: Colors.black,
        padding: const EdgeInsets.all(15.0),
        alignment: Alignment.centerLeft,
        child: const Text(
          '👋 ¡Bienvenido de vuelta a SpringShop!',
          style: TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
        ),
      );
    } 
    
    // ----------------------------------------------------
    // 🔴 ESTADO 2: USUARIO NO AUTENTICADO (Mostrar Prompt original)
    // ----------------------------------------------------
    else {
      return Container(
        color: Colors.black,
        padding: const EdgeInsets.all(15.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              'Identifícate para que podamos\npersonalizar tu experiencia en SpringShop',
              style: TextStyle(color: Colors.white, fontSize: 16),
              textAlign: TextAlign.center, // Centrado para ajustarse mejor
            ),
            const SizedBox(height: 15),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                // Botón de Registrarse (Borde) - Asume navegación a registro
                SizedBox(
                  width: MediaQuery.of(context).size.width * 0.4,
                  child: OutlinedButton(
                    onPressed: () {
                       // 💡 Aquí podrías navegar a la pantalla de registro si la tuvieras
                       _navigateToSignIn(context); // Usamos el login por ahora
                    },
                    style: OutlinedButton.styleFrom(
                      foregroundColor: Colors.white, side: const BorderSide(color: Colors.white),
                      padding: const EdgeInsets.symmetric(vertical: 12),
                    ),
                    child: const Text('Registrarse', style: TextStyle(fontSize: 16)),
                  ),
                ),
                // Botón de Identificarse (Relleno) - Conecta a tu flujo de AppAuth
                SizedBox(
                  width: MediaQuery.of(context).size.width * 0.4,
                  child: ElevatedButton(
                    onPressed: () => _navigateToSignIn(context), // 🚀 Llamada a la función de login
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.white,
                      foregroundColor: Colors.black,
                      padding: const EdgeInsets.symmetric(vertical: 12),
                    ),
                    child: const Text('Identificarse', style: TextStyle(fontSize: 16)),
                  ),
                ),
              ],
            ),
          ],
        ),
      );
    }
  }
}