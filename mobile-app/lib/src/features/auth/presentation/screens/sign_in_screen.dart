import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/home/presentation/screens/home_screen.dart';
// 💡 Ya no necesitamos importar el servicio directamente,
//    usaremos el Notifier para llamar a la acción de login.
// import 'package:springshop/src/auth/auth_service.dart';
import '../../../../auth/auth_state_notifier.dart';

class SignInScreen extends StatelessWidget {
  final bool isModal;

  const SignInScreen({super.key, this.isModal = false});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    // 💡 Obtenemos la instancia del Notifier (ya tiene la dependencia inyectada)
    final authNotifier = context.read<AuthStateNotifier>();

    return Scaffold(
      appBar: AppBar(title: const Text('Identifícate')),
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 30.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text(
              'Accede con tu cuenta Keycloak',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 30),
            SizedBox(
              width: double.infinity,
              child: OutlinedButton(
                onPressed: () async {
                  print('🟦 [SignInScreen] Botón "Continuar" presionado.');
                  // 💡 ¡CORRECCIÓN CLAVE! Llamamos al método login del Notifier.
                  //    El Notifier se encarga de llamar a AuthService.signIn().
                  print(
                    '🔹 Iniciando proceso de login a través de AuthStateNotifier.login()...',
                  );

                  try {
                    // El método .login() del Notifier llama a signIn()
                    await authNotifier.login();

                    print(
                      '✅ [SignInScreen] authNotifier.login() finalizó SIN excepciones.',
                    );
                    print('🔍 Usuario autenticado: ${authNotifier.isLoggedIn}');

                    if (isModal) {
                      print(
                        '🟢 isModal=true → Cerrando pantalla de login (Navigator.pop).',
                      );
                      Navigator.of(context).pop();
                    } else {
                      print('🟢 Navegación normal: no se cerrará modal.');
                    }

                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('Inicio de sesión exitoso ✅'),
                      ),
                    );
                    if (!isModal) {
                      // 🚀 Fuerza la navegación a la vista principal y elimina la actual de la pila.
                      // Esto solo se aplica si la pantalla no fue abierta como modal.
                      Navigator.of(context).pushAndRemoveUntil(
                        MaterialPageRoute(
                          builder: (context) => const HomeScreen(),
                        ),
                        (Route<dynamic> route) =>
                            false, // Elimina todas las rutas anteriores
                      );
                    }
                  } on PlatformException catch (pe) {
                    // Mantener manejo de PlatformException para errores de AppAuth (ej. CANCELED)
                    print('❌ [SignInScreen] PlatformException atrapada.');
                    print('📄 Código: ${pe.code}');
                    print('📄 Mensaje: ${pe.message}');
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text('Error de plataforma: ${pe.message}'),
                        backgroundColor: Colors.redAccent,
                      ),
                    );
                  } on Exception catch (ex, st) {
                    // El Notifier rethrow cualquier error de servidor/red/argumento.
                    print('❌ [SignInScreen] Excepción genérica atrapada.');
                    print('📄 Tipo: ${ex.runtimeType}');
                    print('📄 Mensaje: $ex');
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(
                        content: Text('Error al iniciar sesión: $ex'),
                        backgroundColor: Colors.redAccent,
                      ),
                    );
                  } catch (err) {
                    print('💥 [SignInScreen] Error no controlado: $err');
                    // No rethrow aquí, ya que queremos mostrar el Snackbar
                  }
                },
                style: OutlinedButton.styleFrom(
                  foregroundColor: colorScheme.primary,
                  side: BorderSide(color: colorScheme.primary, width: 2.0),
                  padding: const EdgeInsets.symmetric(vertical: 18),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(6.0),
                  ),
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
