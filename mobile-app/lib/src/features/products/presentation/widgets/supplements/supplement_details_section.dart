// lib/src/features/products/presentation/widgets/supplements/supplement_details_section.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/products/domain/entities/supplement.dart';

class SupplementDetailsSection extends StatelessWidget {
  final Supplement supplement;

  const SupplementDetailsSection({super.key, required this.supplement});

  // Widget auxiliar para cada línea de detalle, similar a los anteriores
  Widget _buildDetailRow(BuildContext context, String label, String value, {int maxLines = 2}) {
    final textTheme = Theme.of(context).textTheme;
    return Padding(
      padding: const EdgeInsets.only(bottom: 8.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 120, // Ancho fijo para las etiquetas
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
              maxLines: maxLines, // Permite más líneas para descripciones largas
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
        // Separador antes de la sección de detalles
        const Divider(height: 10, thickness: 10, color: Color(0xFFF0F0F0)),
        Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Detalles del Suplemento',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onSurface,
                    ),
              ),
              const SizedBox(height: 12),
              
              // Detalles específicos del Suplemento
              _buildDetailRow(context, 'Marca', supplement.brand),
              _buildDetailRow(context, 'Tamaño/Peso', supplement.size),
              _buildDetailRow(context, 'Sabor', supplement.flavor),
              _buildDetailRow(context, 'Ingredientes', supplement.ingredients, maxLines: 5), // Más líneas para ingredientes
              _buildDetailRow(context, 'Instrucciones de Uso', supplement.usageInstructions, maxLines: 5),
              _buildDetailRow(context, 'Advertencias', supplement.warnings, maxLines: 5),
            ],
          ),
        ),
      ],
    );
  }
}