import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/core/auth/auth_state_notifier.dart';
import 'package:springshop/src/features/order/presentation/screens/order_history_screen.dart';
import 'package:springshop/src/features/profile/presentation/widgets/authenticated_user_widget.dart';
import 'package:springshop/src/features/profile/presentation/widgets/user_detail_info_widget.dart';
import 'package:springshop/src/features/profile/presentation/widgets/help_link_widget.dart'; // 💡 Nuevo Import: Ayuda
import 'package:springshop/src/features/profile/presentation/screens/theme_setting_screen.dart'; // 💡 Nuevo Import: Configuración
import '../widgets/profile_app_bar.dart';
import '../widgets/sign_in_prompt_widget.dart';
import '../widgets/quick_links_section.dart'; // Se mantiene el import aunque se elimine una instancia

class UserProfileScreen extends StatelessWidget {
  const UserProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final authNotifier = context.watch<AuthStateNotifier>();

    final bool isLoggedIn = authNotifier.isLoggedIn;
    final user = authNotifier.user;

    if (authNotifier.isLoading) {
      return const Scaffold(
        appBar: ProfileAppBar(),
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: const ProfileAppBar(),

      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 1. SECCIÓN DE IDENTIFICACIÓN
            if (isLoggedIn && user != null)
              AuthenticatedUserWidget(user: user)
            else
              const SignInPromptWidget(),

            const Divider(),

            // 2. Sección de Cuenta y Configuración (Única sección de links)
            const QuickLinksSection(title: 'Cuenta y Configuración'),

            // Link de Información Detallada (solo si está logeado)
            if (isLoggedIn && user != null)
              ListTile(
                title: const Text('Información detallada'),
                leading: const Icon(
                  Icons.info_outline,
                  color: Colors.blueAccent,
                ),
                onTap: () {
                  Navigator.of(context).push(
                    MaterialPageRoute(
                      builder: (context) => UserDetailInfoWidget(user: user),
                    ),
                  );
                },
              ),

            // Historial de Compras (solo si está logeado)
            if (isLoggedIn)
              ListTile(
                title: const Text('Historial de compras'),
                leading: const Icon(Icons.history, color: Colors.blueAccent),
                onTap: () {
                  Navigator.of(context).push(
                    MaterialPageRoute(
                      builder: (_) => const OrderHistoryScreen(),
                    ),
                  );
                  debugPrint('Navegando a Historial de Compras...');
                },
              ),

            // 3. Link de Ayuda (Usando el nuevo widget)
            const HelpLinkWidget(
              title: 'Ayuda',
              icon: Icons.help_outline,
              url:
                  'https://www.ejemplo.com/ayuda-app-springshop', // URL de prueba
            ),

            // 4. Link de Configuración de Tema (Usando el nuevo widget)
            ListTile(
              title: const Text('Configuración de Tema'),
              leading: const Icon(Icons.settings_brightness_outlined),
              onTap: () {
                Navigator.of(context).push(
                  MaterialPageRoute(
                    builder: (context) => const ThemeSettingScreen(),
                  ),
                );
              },
            ),

            const SizedBox(height: 100),
          ],
        ),
      ),
    );
  }
}
