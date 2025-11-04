import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/auth/auth_service.dart';
import '../../../../auth/keycloak_auth_service.dart';
import '../../../../auth/auth_state_notifier.dart'; // Necesario para acceder al estado

class SignInScreen extends StatelessWidget {
  // 💡 Nuevo parámetro para saber si la pantalla se abrió modalmente
  final bool isModal; 
  
  const SignInScreen({
    super.key,
    this.isModal = false, // Por defecto, no es modal
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final authNotifier = context.read<AuthStateNotifier>(); // Usar el Notifier

    return Scaffold(
      appBar: AppBar(
        title: const Text('Identifícate'),
      ),
      
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 30.0),
        child: Column(
          // ... (otros widgets)
          
          children: [
            // ... (Textos y espaciadores)
            const SizedBox(height: 50),
            SizedBox(
              width: double.infinity,
              child: OutlinedButton(
                onPressed: () async {
                  try {
                    // 1. Iniciar el proceso de login de Keycloak
                    await AuthService().login(); 

                    // 2. Si el login fue exitoso (es decir, el estado cambió a isLoggedIn=true),
                    // y fuimos abiertos modalmente (desde AuthGate), cerramos la pantalla de login.
                    if (isModal) {
                      Navigator.of(context).pop(); 
                    }
                  } catch (e) {
                    // Manejar la cancelación o error, por ejemplo, mostrando un snackbar
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Inicio de sesión cancelado o fallido.')),
                    );
                  }
                },
                // ... (Estilo del botón)
                style: OutlinedButton.styleFrom(
                  foregroundColor: colorScheme.primary, 
                  side: BorderSide(color: colorScheme.primary, width: 2.0),
                  padding: const EdgeInsets.symmetric(vertical: 18),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(6.0)),
                ),
                child: const Text(
                  'Continuar',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}