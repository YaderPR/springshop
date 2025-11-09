// lib/src/features/products/presentation/widgets/apparel_details_section.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/products/domain/entities/apparel.dart';

class ApparelDetailsSection extends StatelessWidget {
  final Apparel apparel;

  const ApparelDetailsSection({super.key, required this.apparel});

  // Widget auxiliar para cada línea de detalle
  Widget _buildDetailRow(BuildContext context, String label, String value) {
    final textTheme = Theme.of(context).textTheme;
    return Padding(
      padding: const EdgeInsets.only(bottom: 8.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 80, // Ancho fijo para las etiquetas
            child: Text(
              '$label:',
              style: textTheme.bodyMedium?.copyWith(
                fontWeight: FontWeight.bold,
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
            ),
          ),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              value,
              style: textTheme.bodyLarge,
              maxLines: 2,
              overflow: TextOverflow.ellipsis,
            ),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Divider(height: 10, thickness: 10, color: Color(0xFFF0F0F0)),
        Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Detalles de la Prenda',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onSurface,
                    ),
              ),
              const SizedBox(height: 12),
              
              // Detalles específicos de Apparel
              _buildDetailRow(context, 'Marca', apparel.brand),
              _buildDetailRow(context, 'Categoría', apparel.apparelCategoryName),
              _buildDetailRow(context, 'Talla', apparel.size),
              _buildDetailRow(context, 'Color', apparel.color),
            ],
          ),
        ),
      ],
    );
  }
}