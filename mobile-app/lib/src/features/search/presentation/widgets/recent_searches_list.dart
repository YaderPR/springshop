// lib/src/features/search/presentation/widgets/recent_searches_list.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
// Asegúrate de que la ruta sea correcta a tu servicio
import 'package:springshop/src/core/services/search_history_service.dart'; 

class RecentSearchesList extends StatelessWidget {
  
  // 🔑 Función de callback que se ejecuta al seleccionar un término
  final ValueChanged<String> onSearchTermSelected; 

  const RecentSearchesList({
    super.key,
    required this.onSearchTermSelected,
  });

  // Función auxiliar para capitalizar el término para la UI
  String _capitalize(String text) {
    if (text.isEmpty) return '';
    return text[0].toUpperCase() + text.substring(1).toLowerCase();
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    
    // 1. Acceder al servicio. Usamos watch para escuchar los cambios de notifyListeners
    final historyService = context.watch<SearchHistoryService>();
    
    // 2. Acceder al historial a través del ValueNotifier
    // Usamos watch en el ValueNotifier para que el widget se reconstruya SOLO cuando .history.value cambie.
    final List<String> historyList = historyService.history.value; 

    if (historyList.isEmpty) {
      return const SizedBox.shrink();
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Lista de ítems recientes
        ...historyList.map((term) => ListTile(
          leading: Icon(Icons.history, color: colorScheme.onSurface.withOpacity(0.7)),
          title: Text(
            _capitalize(term), // Muestra el término con la primera letra en mayúscula
            style: TextStyle(color: colorScheme.onSurface, fontSize: 16),
          ),
          onTap: () {
            // 🔑 Llama al callback para ejecutar la búsqueda
            onSearchTermSelected(term); 
          },
        )),

        const SizedBox(height: 10), // Espacio ajustado

        // Botón BORRAR LAS BÚSQUEDAS RECIENTES
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16.0),
          child: InkWell(
            onTap: () async {
              // 🔑 Lógica para borrar las búsquedas usando el servicio
              await historyService.clearHistory();
              
              // Opcional: Mostrar confirmación
              if (context.mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Búsquedas recientes borradas.')),
                );
              }
            },
            child: Text(
              'BORRAR LAS BÚSQUEDAS RECIENTES',
              style: TextStyle(
                color: colorScheme.primary,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
      ],
    );
  }
}