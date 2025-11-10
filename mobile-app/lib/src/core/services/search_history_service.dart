// lib/src/core/services/search_history_service.dart

import 'package:shared_preferences/shared_preferences.dart';
import 'package:flutter/foundation.dart'; // Para ValueNotifier

class SearchHistoryService extends ChangeNotifier {
  static const String _historyKey = 'search_history_list';
  static const int _maxHistorySize = 10;
  
  // ValueNotifier expone el historial actual y notifica a los widgets
  final ValueNotifier<List<String>> history = ValueNotifier([]);

  SearchHistoryService() {
    _loadHistory();
  }

  // Carga el historial desde Shared Preferences
  Future<void> _loadHistory() async {
    final prefs = await SharedPreferences.getInstance();
    // getList() devuelve null si la clave no existe, por eso usamos [] como fallback
    final loadedHistory = prefs.getStringList(_historyKey) ?? [];
    
    // Actualizamos el ValueNotifier, lo que refrescará los widgets asociados.
    history.value = loadedHistory; 
    notifyListeners(); 
  }

  // Agrega un nuevo término de búsqueda al historial
  Future<void> saveSearchTerm(String term) async {
    final prefs = await SharedPreferences.getInstance();
    final normalizedTerm = term.trim().toLowerCase();
    
    if (normalizedTerm.isEmpty) return;

    // 1. Crear una copia modificable y asegurar unicidad
    final List<String> currentHistory = List.from(history.value);
    
    // 2. Remover el término si ya existe para moverlo al inicio (más reciente)
    currentHistory.remove(normalizedTerm);
    
    // 3. Añadir el nuevo término al principio
    currentHistory.insert(0, normalizedTerm);
    
    // 4. Limitar el tamaño del historial
    if (currentHistory.length > _maxHistorySize) {
      currentHistory.removeLast();
    }

    // 5. Persistir y actualizar
    await prefs.setStringList(_historyKey, currentHistory);
    history.value = currentHistory;
    notifyListeners();
  }

  // Limpia completamente el historial
  Future<void> clearHistory() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_historyKey);
    history.value = [];
    notifyListeners();
  }
}