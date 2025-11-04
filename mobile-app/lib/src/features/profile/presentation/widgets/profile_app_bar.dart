// lib/src/features/profile/presentation/widgets/profile_app_bar.dart

import 'package:flutter/material.dart';

class ProfileAppBar extends StatelessWidget implements PreferredSizeWidget {
  const ProfileAppBar({super.key});
  
  // Define la altura de la AppBar
  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight); 

  @override
  Widget build(BuildContext context) {
    // El color de fondo y el color de los iconos/texto vienen del tema
    final colorScheme = Theme.of(context).colorScheme;

    return AppBar(
      // Título actualizado
      title: const Text('Mi Perfil', style: TextStyle(fontWeight: FontWeight.bold)), 
      // Iconos en la derecha (Buzón quitado, Carrito mantenido)
      actions: [
        IconButton(
          icon: const Icon(Icons.shopping_cart_outlined),
          onPressed: () {},
          // El color lo toma del AppBarTheme definido en main.dart
        ),
        const SizedBox(width: 8),
      ],
    );
  }
}