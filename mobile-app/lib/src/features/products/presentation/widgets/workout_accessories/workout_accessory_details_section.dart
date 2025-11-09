// lib/src/features/products/presentation/widgets/workout_accessory_details_section.dart
import 'package:flutter/material.dart';
import 'package:springshop/src/features/products/domain/entities/workout_accessory.dart';

class WorkoutAccessoryDetailsSection extends StatelessWidget {
  final WorkoutAccessory accessory;

  const WorkoutAccessoryDetailsSection({super.key, required this.accessory});

  // Widget auxiliar para cada línea de detalle (reutilizando la lógica de Apparel)
  Widget _buildDetailRow(BuildContext context, String label, String value) {
    final textTheme = Theme.of(context).textTheme;
    return Padding(
      padding: const EdgeInsets.only(bottom: 8.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100, // Ajustamos el ancho para que quepan etiquetas más largas
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
        // Separador antes de la sección de detalles
        const Divider(height: 10, thickness: 10, color: Color(0xFFF0F0F0)),
        Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Especificaciones del Accesorio',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                      color: colorScheme.onSurface,
                    ),
              ),
              const SizedBox(height: 12),
              
              // Detalles específicos de WorkoutAccessory
              _buildDetailRow(context, 'Categoría', accessory.workoutAccessoryCategoryName),
              _buildDetailRow(context, 'Color', accessory.color),
              _buildDetailRow(context, 'Material', accessory.material),
              _buildDetailRow(context, 'Dimensiones', accessory.dimensions),
              _buildDetailRow(context, 'Peso', accessory.weight),
            ],
          ),
        ),
      ],
    );
  }
}