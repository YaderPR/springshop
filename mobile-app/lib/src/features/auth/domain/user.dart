// lib/src/features/auth/domain/entities/user.dart

class User {
  // Identificador único del usuario (sub de OIDC)
  final String userId; // Cambiado de 'id' a 'userId' para mayor claridad
  
  // Nombre de usuario preferido (o nombre de login)
  final String username;

  // Nombre completo
  final String fullName;

  // Primer nombre
  final String firstName;

  // Apellido(s)
  final String lastName;

  // Correo electrónico
  final String email;

  // Indicador de verificación
  final bool isEmailVerified;
  
  // 🔑 NUEVO: Lista de roles asignados al usuario
  final List<String> roles; 

  const User({
    required this.userId,
    required this.username,
    required this.fullName,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.isEmailVerified,
    required this.roles, // 🔑 Añadir al constructor
  });

  // Método opcional para facilitar la depuración
  @override
  String toString() {
    return 'User(id: $userId, name: $fullName, email: $email, roles: $roles)';
  }
  
  // ----------------------------------------------------
  // Factory Constructor para mapeo JSON
  // ----------------------------------------------------

  factory User.fromJson(Map<String, dynamic> json) {
    // Usamos el operador ?? '' (null-aware coalescing) para proveer un valor por defecto.
    
    // Keycloak usa 'sub' para el ID y 'preferred_username' para el nombre de usuario
    final String sub = json['sub'] ?? '';
    final String username = json['preferred_username'] ?? '';

    // Nombre completo y partes
    final String name = json['name'] ?? '';
    final String givenName = json['given_name'] ?? '';
    final String familyName = json['family_name'] ?? '';

    // Email y verificación
    final String email = json['email'] ?? '';
    final bool emailVerified = json['email_verified'] ?? false;
    
    // 🔑 Mapeo de Roles
    // Keycloak típicamente anida los roles bajo una claim específica (ej: 'resource_access' o 'realm_access').
    // Si asumes que los roles de REALM están directamente en el nivel superior bajo 'realm_access', 
    // y solo necesitamos los nombres de los roles:
    final realmAccess = json['realm_access'] as Map<String, dynamic>?;
    final List<String> userRoles = (realmAccess?['roles'] as List<dynamic>?)
        ?.map((role) => role.toString())
        .toList() ?? [];

    return User(
      userId: sub,
      username: username,
      fullName: name,
      firstName: givenName,
      lastName: familyName,
      email: email,
      isEmailVerified: emailVerified,
      roles: userRoles, // 🔑 Pasar la lista de roles
    );
  }
}