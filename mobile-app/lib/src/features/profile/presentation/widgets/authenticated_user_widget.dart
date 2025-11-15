// lib/src/features/profile/presentation/widgets/authenticated_user_widget.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/core/auth/auth_state_notifier.dart';
import 'package:springshop/src/features/auth/domain/entities/user.dart';

class AuthenticatedUserWidget extends StatelessWidget {
  final User user;
  
  const AuthenticatedUserWidget({super.key, required this.user});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final authNotifier = context.read<AuthStateNotifier>();

    return Padding(
      padding: const EdgeInsets.all(16.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 1. Avatar y Nombre
          Row(
            children: [
              const CircleAvatar(
                radius: 30,
                child: Icon(Icons.person, size: 30),
              ),
              const SizedBox(width: 16),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    // Mostramos el nombre completo (fullName) del UserInfo
                    user.fullName.isNotEmpty ? user.fullName : user.username,
                    style: theme.textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: theme.colorScheme.primary,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    user.email,
                    style: theme.textTheme.bodyMedium,
                  ),
                ],
              ),
            ],
          ),
          
          const SizedBox(height: 20),
          
          // 2. Botón de Cerrar Sesión
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: () {
                authNotifier.logout();
              },
              icon: const Icon(Icons.logout),
              label: const Text('Cerrar Sesión'),
              style: ElevatedButton.styleFrom(
                backgroundColor: theme.colorScheme.error,
                foregroundColor: theme.colorScheme.onError,
                padding: const EdgeInsets.symmetric(vertical: 12),
              ),
            ),
          ),
        ],
      ),
    );
  }
}