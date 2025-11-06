import 'package:flutter/material.dart';
import 'package:provider/provider.dart'; // 💡 NECESARIO para acceder al AuthStateNotifier
import 'package:springshop/src/features/search/presentation/screens/search_screen.dart';
import 'package:springshop/src/features/cart/presentation/screens/shopping_cart_screen.dart'; // Importar el Carrito
import 'package:springshop/src/features/auth/presentation/screens/sign_in_screen.dart'; // Importar la pantalla de Login
import 'package:springshop/src/core/auth/auth_state_notifier.dart'; // Importar el Notifier

class AppBarWidget extends StatelessWidget {
  const AppBarWidget({super.key});
  final String logoPath = 'assets/icons/springshop-logo-text.png';

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    
    final backgroundColor = isDark ? Colors.black : theme.appBarTheme.backgroundColor ?? theme.colorScheme.surface;
    final foregroundColor = isDark ? Colors.white : theme.appBarTheme.foregroundColor ?? theme.colorScheme.onSurface;
    final searchFieldColor = isDark ? const Color(0xFF222222) : Colors.grey[200];
    final hintColor = isDark ? Colors.white54 : Colors.grey;

    // 💡 Usamos context.read<T>() porque el AppBar no necesita re-renderizarse 
    // cuando el login cambia, solo necesita leer el estado en el onTap.
    final authNotifier = context.read<AuthStateNotifier>();

    return Container(
      padding: const EdgeInsets.only(top: 40, left: 15, right: 15, bottom: 10),
      color: backgroundColor,
      child: Column(
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Image.asset(logoPath, height: 30, fit: BoxFit.contain),
              IconButton(
                icon: Icon(Icons.shopping_cart_outlined, color: foregroundColor),
                onPressed: () {
                  // 🚀 LÓGICA DE PROTECCIÓN DEL CARRITO
                  if (authNotifier.isLoggedIn) {
                    print('🛒 [AppBar] Acceso al carrito: Usuario logueado. ✅');
                    // 🟢 El usuario está logueado: Navegar al carrito
                    Navigator.of(context).push(
                      MaterialPageRoute(
                        builder: (context) => const ShoppingCartScreen(),
                      ),
                    );
                  } else {
                    print('🛒 [AppBar] Acceso al carrito: Usuario NO logueado. Forzando login. 🚫');
                    // 🟠 El usuario NO está logueado: Forzar el login
                    Navigator.of(context).push(
                      MaterialPageRoute(
                        // Usamos isModal: true para que la pantalla de login se cierre sola al éxito
                        builder: (context) => const SignInScreen(isModal: true),
                      ),
                    );
                  }
                },
              ),
            ],
          ),
          const SizedBox(height: 10),
          
          // ... (Search Bar Widget)
          InkWell(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => const SearchScreen()),
              );
            },
            child: Container(
              height: 40,
              decoration: BoxDecoration(
                color: searchFieldColor,
                borderRadius: BorderRadius.circular(4.0),
              ),
              child: TextField(
                enabled: false, 
                cursorColor: foregroundColor, 
                
                decoration: InputDecoration(
                  hintText: 'Buscar en SpringShop',
                  hintStyle: TextStyle(color: hintColor, fontSize: 16),
                  prefixIcon: Icon(Icons.search, color: hintColor),
                  suffixIcon: Icon(Icons.camera_alt_outlined, color: hintColor),
                  border: InputBorder.none,
                  contentPadding: const EdgeInsets.symmetric(vertical: 8.0),
                ),
                style: TextStyle(color: foregroundColor),
              ),
            ),
          ),
        ],
      ),
    );
  }
}