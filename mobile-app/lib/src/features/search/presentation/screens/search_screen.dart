// lib/src/features/search/presentation/screens/search_screen.dart

import 'package:flutter/material.dart';
import '../widgets/search_app_bar.dart';
import '../widgets/recent_searches_list.dart';

class SearchScreen extends StatelessWidget {
  const SearchScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // El Scaffold y el fondo se adaptarán al tema
    final colorScheme = Theme.of(context).colorScheme;

    return Scaffold(
      // 1. Barra de búsqueda personalizada
      appBar: const SearchScreenAppBar(),
      
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 2. Pestañas de Búsqueda (Solo "Recientes")
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
            child: Text(
              'Recientes',
              style: TextStyle(
                // Usa el color primario para el texto de la pestaña activa
                color: colorScheme.primary, 
                fontWeight: FontWeight.bold,
                fontSize: 16,
              ),
            ),
          ),
          
          // Línea divisoria bajo la pestaña (opcional, pero útil)
          Divider(height: 1, thickness: 1, color: colorScheme.onBackground.withOpacity(0.1)),
          
          // 3. Lista de Búsquedas Recientes
          const Expanded(
            child: RecentSearchesList(),
          ),
        ],
      ),
    );
  }
}