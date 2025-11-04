// lib/src/features/home/presentation/widgets/info_section_widget.dart

import 'package:flutter/material.dart';

class InfoSectionWidget extends StatelessWidget {
  final Color backgroundColor;
  final String title;
  final String subtitle;
  final String buttonText;
  final bool isPrimary;

  const InfoSectionWidget({
    super.key,
    required this.backgroundColor,
    required this.title,
    required this.subtitle,
    required this.buttonText,
    required this.isPrimary,
  });

  @override
  Widget build(BuildContext context) {
    final textColor = isPrimary ? Colors.black : Colors.white;
    final buttonColor = isPrimary ? Colors.black : Colors.white;
    final buttonTextColor = isPrimary ? Colors.white : Colors.black;

    return Container(
      width: double.infinity,
      color: backgroundColor,
      padding: const EdgeInsets.all(18.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: textColor,
            ),
          ),
          const SizedBox(height: 5),
          Text(
            subtitle,
            style: TextStyle(fontSize: 16, color: textColor),
          ),
          const SizedBox(height: 15),
          ElevatedButton(
            onPressed: () {},
            style: ElevatedButton.styleFrom(
              backgroundColor: buttonColor,
              foregroundColor: buttonTextColor,
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(4),
              ),
            ),
            child: Text(buttonText, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }
}