// lib/src/features/profile/presentation/widgets/quick_links_section.dart

import 'package:flutter/material.dart';

class QuickLinksSection extends StatelessWidget {
  final String title;

  const QuickLinksSection({super.key, required this.title});

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;

    return Padding(
      padding: const EdgeInsets.only(left: 15, right: 15, top: 25, bottom: 5),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Título de la sección (ej: "Accesos directos")
          Text(
            title,
            style: Theme.of(context).textTheme.titleSmall?.copyWith(
              color: colorScheme.secondary, // Un color que resalte, como el azul de eBay o un color secundario
              fontWeight: FontWeight.bold,
            ),
          ),
          const Divider(height: 20, thickness: 0.5),
        ],
      ),
    );
  }
}