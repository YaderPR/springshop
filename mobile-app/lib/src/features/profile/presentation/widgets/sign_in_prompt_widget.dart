// lib/src/features/profile/presentation/widgets/sign_in_prompt_widget.dart

import 'package:flutter/material.dart';
import 'package:springshop/src/features/auth/presentation/screens/sign_in_screen.dart';

class SignInPromptWidget extends StatelessWidget {
  const SignInPromptWidget({super.key});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final isDark = Theme.of(context).brightness == Brightness.dark;

    // Color del fondo de esta sección, diferente al fondo general si es claro
    final backgroundColor = isDark ? Colors.black : colorScheme.surface;

    return Container(
      color: backgroundColor,
      padding: const EdgeInsets.only(left: 15, top: 15, bottom: 20),
      child: InkWell(
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => const SignInScreen()),
          );
        },
        child: Row(
          children: [
            // Ícono de Perfil
            Icon(
              Icons.account_circle_outlined,
              size: 30,
              color: colorScheme.onSurface,
            ),
            const SizedBox(width: 15),
            // Texto de Identificación
            Text(
              'Identificarse',
              style: Theme.of(context).textTheme.titleLarge?.copyWith(
                fontWeight: FontWeight.bold,
                color: colorScheme.onSurface,
              ),
            ),
          ],
        ),
      ),
    );
  }
}
