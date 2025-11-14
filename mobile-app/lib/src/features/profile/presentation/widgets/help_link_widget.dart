// lib/src/features/profile/presentation/widgets/help_link_widget.dart

import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';

class HelpLinkWidget extends StatelessWidget {
  final String title;
  final IconData icon;
  final String url;
  
  const HelpLinkWidget({
    super.key, 
    required this.title, 
    required this.icon, 
    required this.url,
  });

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(title),
      leading: Icon(icon),
      onTap: () => _launchURL(context, url),
    );
  }

  void _launchURL(BuildContext context, String url) async {
    final uri = Uri.parse(url);
    if (await canLaunchUrl(uri)) {
      await launchUrl(uri, mode: LaunchMode.externalApplication);
    } else {
      // Mostrar un mensaje de error si no se puede abrir la URL
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('No se pudo abrir la URL: $url')),
      );
    }
  }
}