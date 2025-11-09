import 'package:flutter/material.dart';

class SubcategoryCard extends StatelessWidget {
  final String title;
  // Este String ahora puede ser una URL (http/https) o un String simple (emoji/icono).
  final String icon; 
  final Color color;
  final VoidCallback onTap;

  const SubcategoryCard({
    super.key,
    required this.title,
    required this.icon,
    required this.color,
    required this.onTap,
  });
  
  // 💡 Widget auxiliar para determinar si se muestra una imagen de red, un icono o un emoji.
  Widget _buildIcon(BuildContext context) {
    // 1. Verificar si el string parece ser una URL HTTP/HTTPS
    final isNetworkImage = icon.startsWith('http://') || icon.startsWith('https://');
    final colorScheme = Theme.of(context).colorScheme;

    if (isNetworkImage && icon.isNotEmpty) {
      return ClipRRect(
        borderRadius: BorderRadius.circular(8.0),
        child: Image.network(
          icon,
          width: 48,
          height: 48,
          fit: BoxFit.cover,
          // 💡 Mostrar un indicador de progreso mientras se carga la imagen
          loadingBuilder: (context, child, loadingProgress) {
            if (loadingProgress == null) return child;
            return SizedBox(
              width: 48,
              height: 48,
              child: Center(
                child: CircularProgressIndicator(
                  color: colorScheme.onSurface,
                  value: loadingProgress.expectedTotalBytes != null
                      ? loadingProgress.cumulativeBytesLoaded / loadingProgress.expectedTotalBytes!
                      : null,
                ),
              ),
            );
          },
          // 💡 Mostrar un icono de fallback si la carga de la imagen falla
          errorBuilder: (context, error, stackTrace) {
            return Icon(
              Icons.image_not_supported_outlined, 
              size: 32, 
              color: colorScheme.error,
            );
          },
        ),
      );
    } else {
      // 2. Si no es una URL, lo trata como un emoji o un icono de texto.
      return Text(
        icon,
        style: const TextStyle(fontSize: 30),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final colorScheme = Theme.of(context).colorScheme;
    
    return Card(
      color: color,
      elevation: 0, 
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: InkWell( 
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 16.0, horizontal: 16.0),
          child: Row(
            // 🔑 CAMBIO: Alineación al inicio (start) para agrupar icono y texto
            mainAxisAlignment: MainAxisAlignment.start,
            children: [
              // 🔑 Icono/Imagen (Renderizado condicional)
              _buildIcon(context),
              
              const SizedBox(width: 16),
              
              // Nombre de la subcategoría
              Expanded(
                child: Text(
                  title,
                  style: Theme.of(context).textTheme.titleLarge?.copyWith(
                    fontWeight: FontWeight.w600,
                    color: colorScheme.onSurface,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              
              // Flecha (Se mantendrá a la derecha gracias al Expanded anterior)
              const Icon(Icons.arrow_forward_ios, size: 18, color: Colors.grey),
            ],
          ),
        ),
      ),
    );
  }
}