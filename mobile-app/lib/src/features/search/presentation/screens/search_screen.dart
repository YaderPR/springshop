// lib/src/features/search/presentation/screens/search_screen.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/core/services/search_history_service.dart';
import 'package:springshop/src/features/search/presentation/widgets/global_product_search_widget.dart';
import '../widgets/search_app_bar.dart';
import '../widgets/recent_searches_list.dart';

class SearchScreen extends StatefulWidget {
  const SearchScreen({super.key});

  @override
  State<SearchScreen> createState() => _SearchScreenState();
}

class _SearchScreenState extends State<SearchScreen> {
  
  final TextEditingController _searchController = TextEditingController();
  bool _isSearching = false; 

  @override
  void initState() {
    super.initState();
    _searchController.addListener(_onSearchQueryChanged);
  }

  void _onSearchQueryChanged() {
    final hasQuery = _searchController.text.trim().isNotEmpty;
    if (_isSearching != hasQuery) {
      setState(() {
        _isSearching = hasQuery;
      });
    }
  }

  void _performSearch(String query) {
    final trimmedQuery = query.trim();
    if (trimmedQuery.isEmpty) {
      setState(() {
        _isSearching = false;
      });
      return;
    }
    
    context.read<SearchHistoryService>().saveSearchTerm(trimmedQuery);
    
    setState(() {
      _isSearching = true; 
    });
    
    print(
      '🔎 Búsqueda real ejecutada para: $trimmedQuery. Resultados actualizados en GlobalProductSearchWidget.',
    );
  }

  @override
  void dispose() {
    _searchController.removeListener(_onSearchQueryChanged);
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    final bool showHistory = !_isSearching;
    
    return Scaffold(
      appBar: SearchScreenAppBar(
        controller: _searchController,
        onSubmitted: _performSearch,
      ),

      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (showHistory) ...[
            
            const Padding(
              padding: EdgeInsets.symmetric(
                horizontal: 16.0,
                vertical: 8.0,
              ),
              child: Text(
                'Recientes',
                style: TextStyle(
                  color: Colors.blue, 
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                ),
              ),
            ),
            
            Divider(
              height: 1,
              thickness: 1,
              color: colorScheme.onSurface.withOpacity(0.1),
            ),
            Expanded(
              child: RecentSearchesList(
                onSearchTermSelected: (term) {
                  _searchController.text = term;
                  _performSearch(term); 
                },
              ),
            ),
          ] else
            Expanded(
              child: GlobalProductSearchWidget(
                searchTerm: _searchController.text, 
              ),
            ),
        ],
      ),
    );
  }
}