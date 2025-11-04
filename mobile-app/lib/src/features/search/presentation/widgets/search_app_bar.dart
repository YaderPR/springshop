// lib/src/features/search/presentation/widgets/search_app_bar.dart

import 'package:flutter/material.dart';

class SearchScreenAppBar extends StatelessWidget implements PreferredSizeWidget {
  const SearchScreenAppBar({super.key});

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    
    // El color de fondo y el color de los iconos/texto vienen del tema
    return AppBar(
      automaticallyImplyLeading: true, // Muestra la flecha de retroceso
      backgroundColor: colorScheme.surface, // Usa el fondo del tema
      
      title: Container(
        height: 40,
        decoration: BoxDecoration(
          color: colorScheme.surfaceContainerHighest.withOpacity(0.5), // Color para el campo de texto
          borderRadius: BorderRadius.circular(4.0),
        ),
        child: TextField(
          autofocus: true, // Para que el teclado se abra automáticamente
          decoration: InputDecoration(
            hintText: 'Buscar en SpringShop', // Nombre actualizado
            hintStyle: theme.textTheme.bodyLarge?.copyWith(
              color: colorScheme.onSurface.withOpacity(0.5),
            ),
            prefixIcon: Icon(Icons.search, color: colorScheme.onSurface.withOpacity(0.7)),
            
            // ❌ ICONO DE CÁMARA OMITIDO
            // suffixIcon: Icon(Icons.camera_alt_outlined, ...), 
            
            border: InputBorder.none,
            contentPadding: const EdgeInsets.symmetric(vertical: 8.0),
          ),
          style: theme.textTheme.bodyLarge?.copyWith(
            color: colorScheme.onSurface,
          ),
        ),
      ),
      
      // Ícono de Carrito (si decides mantenerlo, sino se puede remover)
      actions: [
        IconButton(
          icon: const Icon(Icons.shopping_cart_outlined),
          onPressed: () {},
        ),
      ],
    );
  }
}