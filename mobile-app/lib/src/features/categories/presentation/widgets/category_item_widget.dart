// lib/src/features/categories/presentation/widgets/category_item_widget.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/categories/domain/entities/category.dart';

class CategoryItemWidget extends StatelessWidget {
  final Category category;
  final bool isSelected;
  final ValueChanged<String> onCategoryTap;

  const CategoryItemWidget({
    super.key,
    required this.category,
    this.isSelected = false,
    required this.onCategoryTap,
  });

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return GestureDetector(
      onTap: () => onCategoryTap(category.id),
      child: Column(
        children: [
          Container(
            width: 80, // Tamaño fijo para el círculo
            height: 80,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: colorScheme.surface, // Color de fondo del círculo
              border: Border.all(
                color: isSelected ? colorScheme.primary : colorScheme.onSurface.withOpacity(0.1),
                width: isSelected ? 2.5 : 1,
              ),
              boxShadow: isSelected
                  ? [
                      BoxShadow(
                        color: colorScheme.primary.withOpacity(0.3),
                        blurRadius: 5,
                        spreadRadius: 1,
                      )
                    ]
                  : null,
            ),
            child: ClipOval(
              child: Image.network(
                category.imageUrl!,
                fit: BoxFit.cover,
                errorBuilder: (context, error, stackTrace) {
                  return Icon(
                    Icons.broken_image,
                    color: colorScheme.error,
                    size: 40,
                  );
                },
              ),
            ),
          ),
          const SizedBox(height: 8),
          Text(
            category.name,
            style: TextStyle(
              color: isSelected ? colorScheme.primary : colorScheme.onSurface,
              fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
            ),
          ),
          if (isSelected)
            Container(
              margin: const EdgeInsets.only(top: 4),
              width: 30, // Ancho de la línea debajo del texto
              height: 2.5,
              decoration: BoxDecoration(
                color: colorScheme.primary,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
        ],
      ),
    );
  }
}