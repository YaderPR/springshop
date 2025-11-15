// lib/src/features/profile/presentation/widgets/user_detail_info_widget.dart

import 'package:flutter/material.dart';
import 'package:springshop/src/features/auth/domain/entities/user.dart';

class UserDetailInfoWidget extends StatelessWidget {
  final User user;

  const UserDetailInfoWidget({super.key, required this.user});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Información Detallada del Usuario'),
        backgroundColor: Theme.of(context).colorScheme.surface,
        elevation: 1,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Título Principal
            Text(
              'Perfil de Usuario (${user.username})',
              style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                    fontWeight: FontWeight.bold,
                    color: Theme.of(context).colorScheme.primary,
                  ),
            ),
            const Divider(height: 30),

            // Tarjeta de Datos Principales
            Card(
              elevation: 4,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
              child: Padding(
                padding: const EdgeInsets.all(20.0),
                child: Column(
                  children: [
                    _buildInfoRow(context, Icons.person, 'Nombre Completo', user.fullName),
                    _buildInfoRow(context, Icons.email, 'Correo Electrónico', user.email),
                    _buildInfoRow(context, Icons.badge, 'ID de Keycloak', user.sub),
                    
                    // Solo si existen en la entidad User
                    if (user.firstName.isNotEmpty) 
                      _buildInfoRow(context, Icons.person_outline, 'Nombre', user.firstName),
                    if (user.lastName.isNotEmpty) 
                      _buildInfoRow(context, Icons.person_outline, 'Apellido', user.lastName),
                    
                  ],
                ),
              ),
            ),
            
            const SizedBox(height: 20),

            // Sección de Roles (Si Keycloak devuelve roles, los listamos)
            if (user.roles.isNotEmpty) ...[
              Text(
                'Roles Asignados',
                style: Theme.of(context).textTheme.titleLarge,
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8.0,
                runSpacing: 4.0,
                children: user.roles.map((role) => Chip(
                  label: Text(role),
                  backgroundColor: Theme.of(context).colorScheme.secondary.withOpacity(0.1),
                )).toList(),
              ),
            ]
          ],
        ),
      ),
    );
  }

  // Widget auxiliar para las filas de información
  Widget _buildInfoRow(BuildContext context, IconData icon, String label, String value) {
    final theme = Theme.of(context);
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, color: theme.colorScheme.primary, size: 20),
          const SizedBox(width: 12),
          Expanded(
            flex: 2,
            child: Text(
              label,
              style: theme.textTheme.bodyLarge?.copyWith(fontWeight: FontWeight.w600),
            ),
          ),
          Expanded(
            flex: 3,
            child: SelectableText( // Usamos SelectableText por si el usuario quiere copiar el valor
              value,
              style: theme.textTheme.bodyLarge,
            ),
          ),
        ],
      ),
    );
  }
}