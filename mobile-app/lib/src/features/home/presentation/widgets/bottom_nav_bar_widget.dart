// lib/src/features/home/presentation/widgets/bottom_nav_bar_widget.dart

import 'package:flutter/material.dart';

class BottomNavBarWidget extends StatelessWidget {
  // 🔑 Propiedades requeridas para el nuevo patrón
  final int currentIndex;
  final Function(int) onTap;

  const BottomNavBarWidget({
    super.key,
    required this.currentIndex,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final backgroundColor = colorScheme.surface;
    final selectedColor = colorScheme.primary;
    final unselectedColor = colorScheme.onSurface;

    return BottomNavigationBar(
      type: BottomNavigationBarType.fixed,
      backgroundColor: backgroundColor,
      selectedItemColor: selectedColor,
      unselectedItemColor: unselectedColor,
      // 🔑 Usa el índice actual
      currentIndex: currentIndex,
      // 🔑 Llama al callback para cambiar el estado en HomeScreen
      onTap: onTap,
      items: const [
        BottomNavigationBarItem(
          icon: Icon(Icons.home_outlined),
          label: 'Portada',
        ),
        BottomNavigationBarItem(
          icon: Icon(Icons.person_outline),
          label: 'Mi Perfil',
        ),
        BottomNavigationBarItem(icon: Icon(Icons.search), label: 'Buscar'),
      ],
    );
  }
}