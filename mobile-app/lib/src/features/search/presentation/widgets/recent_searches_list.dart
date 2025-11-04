// lib/src/features/search/presentation/widgets/recent_searches_list.dart

import 'package:flutter/material.dart';

class RecentSearchesList extends StatelessWidget {
  const RecentSearchesList({super.key});

  // Mock de datos recientes
  final List<String> recentSearches = const [
    'holala doll', 
    'zapatillas deportivas', 
    'cámara vintage'
  ];

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Lista de ítems recientes
        ...recentSearches.map((term) => ListTile(
          leading: Icon(Icons.history, color: colorScheme.onBackground.withOpacity(0.7)),
          title: Text(
            term,
            style: TextStyle(color: colorScheme.onBackground, fontSize: 16),
          ),
          onTap: () {
            // Lógica para ejecutar la búsqueda
          },
        )).toList(),

        const SizedBox(height: 20),

        // Botón BORRAR LAS BÚSQUEDAS RECIENTES
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16.0),
          child: InkWell(
            onTap: () {
              // Lógica para borrar las búsquedas
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('Búsquedas borradas (mock)')),
              );
            },
            child: Text(
              'BORRAR LAS BÚSQUEDAS RECIENTES',
              style: TextStyle(
                color: colorScheme.primary, // Color primario (azul, etc.)
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
      ],
    );
  }
}