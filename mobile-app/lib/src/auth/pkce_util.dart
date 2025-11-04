// lib/src/auth/pkce_util.dart

import 'dart:convert';
import 'dart:math';
import 'package:crypto/crypto.dart';

class PkceUtil {

  static String generateCodeVerifier() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~';
    final random = Random.secure();
    return List.generate(128, (index) => chars[random.nextInt(chars.length)]).join();
  }

  static String generateCodeChallenge(String codeVerifier) {
    final verifierBytes = utf8.encode(codeVerifier);
    final digest = sha256.convert(verifierBytes);
    final base64Url = base64UrlEncode(digest.bytes);
    
    return base64Url.replaceAll('=', '');
  }
}