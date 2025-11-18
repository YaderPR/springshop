// src/core/services/deep_link_service.dart

import 'package:flutter/material.dart';
import 'package:app_links/app_links.dart';

// ================================================================
// 🔑 GLOBAL NAVIGATOR KEY (NECESARIO PARA NAVEGAR DESDE SERVICIOS)
// ================================================================
final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

// ================================================================
// 🔥 VARIABLE GLOBAL para capturar el orderId en Cold Start
//    Será procesada posteriormente por DeepLinkWrapperScreen.
// ================================================================
int? pendingOrderId;

// ================================================================
// 🔗 DeepLinkService
// ================================================================
class DeepLinkService {
  static final _appLinks = AppLinks();

  // ------------------------------------------------------------
  // 🚀 init(): Se llama al iniciar la app
  // ------------------------------------------------------------
  static void init() {
    print('[DeepLink] Inicializando listener de deep links...');

    // -----------------------------
    // 1. Cold Start (la app se abre desde cero por deep link)
    // -----------------------------
    _appLinks.getInitialLink().then((Uri? uri) {
      if (uri != null) {
        print('[DeepLink][COLD START] URI Inicial capturada: $uri');
        _checkAndSetOrderId(uri);
      } else {
        print('[DeepLink][COLD START] No se recibió URI inicial.');
      }
    });

    // -----------------------------
    // 2. App ya abierta (hot stream)
    // -----------------------------
    _appLinks.uriLinkStream.listen(
      (Uri? uri) {
        if (uri != null) {
          print('[DeepLink][STREAM] URI recibida: $uri');
          _checkAndSetOrderId(uri, navigateImmediately: true);
        }
      },
      onError: (error) {
        print('[DeepLink][ERROR] Error en uriLinkStream: $error');
      },
    );
  }

  // ------------------------------------------------------------
  // 📌 Navegar hacia OrderDetails
  // Regresa TRUE si se navegó correctamente.
  // ------------------------------------------------------------
  static bool _navigateToOrderDetails(int orderId) {
    final nav = navigatorKey.currentState;

    if (nav != null && nav.mounted) {
      print('[DeepLink][NAV] Navegando a /order-details con id=$orderId ...');
      nav.pushReplacementNamed('/order-details', arguments: orderId);
      return true;
    } else {
      print('[DeepLink][NAV][WARN] Navigator no montado. No se pudo navegar.');
      return false;
    }
  }

  // ------------------------------------------------------------
  // 🎯 Analiza el deep link y decide qué hacer
  // ------------------------------------------------------------
  static void _checkAndSetOrderId(Uri uri, {bool navigateImmediately = false}) {
    print('[DeepLink] Procesando URI: $uri');

    if (uri.scheme != 'springshop') {
      print('[DeepLink][IGNORE] Scheme desconocido.');
      return;
    }

    final isCheckout =
        uri.host == 'checkout' || uri.pathSegments.contains('checkout');

    if (!isCheckout) {
      print('[DeepLink][IGNORE] URI no corresponde a checkout.');
      return;
    }

    final isSuccess = uri.pathSegments.contains('success');
    final isCancel = uri.pathSegments.contains('cancel');

    print('[DeepLink] host=${uri.host}, pathSegments=${uri.pathSegments}');

    if (isSuccess) {
      final orderId = int.tryParse(uri.queryParameters['orderId'] ?? '');
      if (orderId == null) {
        print('[DeepLink] orderId inválido.');
        return;
      }

      if (navigateImmediately) {
        final ok = _navigateToOrderDetails(orderId);
        if (!ok) pendingOrderId = orderId;
      } else {
        pendingOrderId = orderId;
      }
    }

    if (isCancel) {
      print('[DeepLink] Pago cancelado.');
      if (navigateImmediately) {
        _navigateToHome();
      }
    }
  }

  // ------------------------------------------------------------
  // 🏠 Navegar al Home en caso de cancelación
  // ------------------------------------------------------------
  static void _navigateToHome() {
    final nav = navigatorKey.currentState;

    if (nav != null && nav.mounted) {
      print('[DeepLink][NAV] Enviando al Home...');
      nav.pushNamedAndRemoveUntil('/', (route) => false);
    } else {
      print(
        '[DeepLink][NAV][WARN] Navigator no montado, no se pudo ir al Home.',
      );
    }
  }
}
