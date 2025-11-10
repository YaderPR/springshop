// lib/src/features/search/presentation/widgets/search_app_bar.dart

import 'package:flutter/material.dart';

class SearchScreenAppBar extends StatelessWidget implements PreferredSizeWidget {
  // 🔑 Propiedades requeridas para controlar el estado y las acciones
  final TextEditingController controller;
  final ValueChanged<String> onSubmitted;

  const SearchScreenAppBar({
    super.key,
    required this.controller,
    required this.onSubmitted,
  });

  @override
  Size get preferredSize => const Size.fromHeight(kToolbarHeight);

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final colorScheme = theme.colorScheme;
    
    return AppBar(
      automaticallyImplyLeading: true, // Muestra la flecha de retroceso
      backgroundColor: colorScheme.surface, // Usa el fondo del tema
      
      title: Container(
        height: 40,
        decoration: BoxDecoration(
          color: colorScheme.surfaceContainerHighest.withOpacity(0.5),
          borderRadius: BorderRadius.circular(4.0),
        ),
        child: TextField(
          // 🔑 Usar el controller proporcionado por SearchScreen
          controller: controller, 
          
          // 🔑 Llamar a la función onSubmitted de SearchScreen al presionar Enter/Buscar
          onSubmitted: onSubmitted, 
          
          autofocus: true,
          textInputAction: TextInputAction.search, // Cambia el botón del teclado a "Buscar"
          
          decoration: InputDecoration(
            hintText: 'Buscar en SpringShop',
            hintStyle: theme.textTheme.bodyLarge?.copyWith(
              color: colorScheme.onSurface.withOpacity(0.5),
            ),
            prefixIcon: Icon(Icons.search, color: colorScheme.onSurface.withOpacity(0.7)),
            
            // Opcional: Si quieres un botón de "Limpiar" o "Cerrar" a la derecha
            suffixIcon: controller.text.isNotEmpty
                ? IconButton(
                    icon: Icon(Icons.clear, color: colorScheme.onSurface.withOpacity(0.7)),
                    onPressed: () {
                      controller.clear();
                      onSubmitted(''); // Ejecuta la búsqueda vacía para limpiar resultados
                    },
                  )
                : null,
            
            border: InputBorder.none,
            contentPadding: const EdgeInsets.symmetric(vertical: 8.0),
          ),
          style: theme.textTheme.bodyLarge?.copyWith(
            color: colorScheme.onSurface,
          ),
          
          // Opcional: Reconstruir la barra si el texto cambia para mostrar el icono 'clear'
          onChanged: (value) {
            // Esto fuerza la reconstrucción de la barra para mostrar/ocultar el icono de limpieza
            // Se puede optimizar usando un ValueListenableBuilder si es necesario, pero este es más simple
            (context as Element).markNeedsBuild(); 
          },
        ),
      ),
      
      // Ícono de Carrito (mantenido)
      actions: [
        IconButton(
          icon: const Icon(Icons.shopping_cart_outlined),
          onPressed: () {},
        ),
      ],
    );
  }
}