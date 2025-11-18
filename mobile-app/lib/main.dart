// lib/main.dart (VERSIÓN FINAL Y CORREGIDA)

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/features/home/presentation/screens/home_screen.dart';
import 'package:springshop/src/core/di/inject_container.dart';
import 'package:springshop/src/core/theme/theme_notifier.dart';

import 'package:springshop/src/core/services/deep_link_service.dart';
import 'package:springshop/src/features/order/presentation/screens/order_details_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // ⚠️ Importante: inicializar deep links ANTES del runApp.
  DeepLinkService.init();

  runApp(MultiProvider(providers: buildAppProviders(), child: const MyApp()));
}

// =============================
//   Pantalla de espera
// =============================
class PaymentWaitingScreen extends StatelessWidget {
  const PaymentWaitingScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: 20),
            Text('Procesando pago, por favor espera...'),
          ],
        ),
      ),
    );
  }
}

// =============================
//   WRAPPER CORREGIDO
// =============================
class DeepLinkWrapperScreen extends StatefulWidget {
  const DeepLinkWrapperScreen({super.key});

  @override
  State<DeepLinkWrapperScreen> createState() => _DeepLinkWrapperScreenState();
}

class _DeepLinkWrapperScreenState extends State<DeepLinkWrapperScreen> {
  bool _navigated = false;

  @override
  void initState() {
    super.initState();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      _checkAndNavigate();
    });
  }

  void _checkAndNavigate() {
    if (_navigated) return; // Previene múltiples ejecuciones

    final orderId = pendingOrderId;

    // Usar SIEMPRE Navigator.of(context)
    final nav = Navigator.of(context);

    if (orderId != null) {
      _navigated = true;

      nav.pushNamedAndRemoveUntil(
        '/order-details',
        (route) => false,
        arguments: orderId,
      );

      pendingOrderId = null;
    } else {
      // No hay deep link → ir al Home
      _navigated = true;
      nav.pushReplacementNamed('/');
    }
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(child: CircularProgressIndicator()),
    );
  }
}

// =============================
//      MaterialApp
// =============================
class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    final themeNotifier = context.watch<ThemeNotifier>();

    return MaterialApp(
      title: 'SpringShop Demo',
      navigatorKey: navigatorKey,
      themeMode: themeNotifier.themeMode,

      theme: ThemeData(
        brightness: Brightness.light,
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.blue),
        scaffoldBackgroundColor: Colors.grey[100],
        appBarTheme: const AppBarTheme(
          backgroundColor: Colors.white,
          foregroundColor: Colors.black,
        ),
        useMaterial3: true,
      ),

      darkTheme: ThemeData(
        brightness: Brightness.dark,
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.blue,
          brightness: Brightness.dark,
        ),
        scaffoldBackgroundColor: Colors.black,
        appBarTheme: const AppBarTheme(
          backgroundColor: Colors.black,
          foregroundColor: Colors.white,
        ),
        useMaterial3: true,
      ),

      initialRoute: '/wrapper',

      routes: {
        '/wrapper': (context) => const DeepLinkWrapperScreen(),
        '/': (context) => const HomeScreen(),
        '/payment-wait': (context) => const PaymentWaitingScreen(),
        '/order-details': (context) {
          final orderId = ModalRoute.of(context)!.settings.arguments as int;
          return OrderDetailsScreen(orderId: orderId);
        },
      },
    );
  }
}
