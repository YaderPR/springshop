// lib/src/features/profile/presentation/screens/user_profile_screen.dart

import 'package:flutter/material.dart';
import '../widgets/profile_app_bar.dart';
import '../widgets/sign_in_prompt_widget.dart';
import '../widgets/quick_links_section.dart';

class UserProfileScreen extends StatelessWidget {
  const UserProfileScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // El Scaffold y el fondo se adaptarán al tema
    return Scaffold(
      appBar: const ProfileAppBar(),
      
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Sección de Identificación
            const SignInPromptWidget(),
            
            // Sección de Favoritos y Compras (Opcional - La quitamos como pediste)
            // const SizedBox(height: 10),

            // Sección de Accesos Directos (La parte marcada en la segunda imagen)
            const QuickLinksSection(title: 'Accesos directos'),
            
            // Sección de Cuenta y Configuración
            const QuickLinksSection(title: 'Cuenta'),
            ListTile(
              title: const Text('Ayuda'),
              leading: const Icon(Icons.help_outline),
              onTap: () {},
            ),
            ListTile(
              title: const Text('Configuración'),
              leading: const Icon(Icons.settings_outlined),
              onTap: () {},
            ),
            
            // Espacio inferior de relleno
            const SizedBox(height: 100),
          ],
        ),
      ),
    );
  }
}