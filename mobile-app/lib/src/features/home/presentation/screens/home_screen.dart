// lib/src/features/home/presentation/screens/ebay_home_screen.dart

import 'package:flutter/material.dart';
import 'package:springshop/src/features/categories/presentation/widgets/categories_list_widget.dart';
import 'package:springshop/src/features/profile/presentation/screens/user_profile_screen.dart'; // 💡 Importar pantalla de perfil
import 'package:springshop/src/features/search/presentation/screens/search_screen.dart'; // 💡 Importar pantalla de búsqueda
import '../widgets/appbar_widget.dart';
import '../widgets/auth_prompt_widget.dart';
import '../widgets/bottom_nav_bar_widget.dart';

// Cambiamos a StatefulWidget para gestionar el índice de la navegación
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  int _currentIndex = 0; // Estado para el índice seleccionado

  // Lista de widgets (pantallas) que se muestran en el body
  final List<Widget> _screens = [
    // Índice 0: Pantalla de Portada (contenido original)
    const _HomeContent(),
    // Índice 1: Pantalla de Perfil
    const UserProfileScreen(),
    // Índice 2: Pantalla de Búsqueda
    const SearchScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    // Si estamos en la portada (índice 0), mostramos el App Bar complejo.
    // Si no, la pantalla tendrá su propio App Bar (UserProfileScreen, SearchScreen).
    final PreferredSizeWidget? appBar = _currentIndex == 0
        ? const PreferredSize(
            preferredSize: Size.fromHeight(120.0),
            child: AppBarWidget(),
          )
        : null;

    return Scaffold(
      backgroundColor: Colors.black,
      
      appBar: appBar, // El App Bar solo se usa en la Portada

      // 🔑 Muestra el Widget correspondiente al índice actual
      body: _screens[_currentIndex],
      
      // 🔑 Pasamos el callback y el índice actual a la barra de navegación
      bottomNavigationBar: BottomNavBarWidget(
        currentIndex: _currentIndex,
        onTap: (index) {
          setState(() {
            _currentIndex = index;
          });
        },
      ),
    );
  }
}

// Widget auxiliar para el contenido original de la portada (índice 0)
class _HomeContent extends StatelessWidget {
  const _HomeContent();

  @override
  Widget build(BuildContext context) {
    return const SingleChildScrollView(
      child: Column(
        children: [
          SizedBox(height: 10),
          AuthPromptWidget(),
          CategoriesListWidget(),
        ],
      ),
    );
  }
}