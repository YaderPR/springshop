import 'package:flutter/material.dart';

class ShoppingCartScreen extends StatelessWidget {
  const ShoppingCartScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Mi Carrito (En desarrollo)')),
      body: const Center(
        child: Text('Aquí irá el contenido del carrito de compras.'),
      ),
    );
  }
}