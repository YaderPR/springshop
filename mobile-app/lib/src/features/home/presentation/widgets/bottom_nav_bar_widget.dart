// lib/src/features/home/presentation/widgets/bottom_nav_bar_widget.dart

import 'package:flutter/material.dart';
import 'package:springshop/src/features/profile/presentation/screens/user_profile_screen.dart';
import 'package:springshop/src/features/search/presentation/screens/search_screen.dart';

class BottomNavBarWidget extends StatelessWidget {
  const BottomNavBarWidget({super.key});

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
      currentIndex: 0,
      onTap: (index) {
        if (index == 1) {
          Navigator.push(
            context,
            MaterialPageRoute(builder: (context) => const UserProfileScreen()),
          );
        } else if (index == 2) {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => const SearchScreen(),
            ),
          );
        }
      },
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
