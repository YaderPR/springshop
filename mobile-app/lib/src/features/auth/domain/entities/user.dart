// lib/src/features/auth/domain/entities/user.dart

class User {
  // 🔑 ID interno del User Service (el que queremos guardar)
  final String id;
  // Identificador único del usuario (sub de OIDC)
  final String sub; 
  
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
    required this.id,
    required this.sub,
    required this.username,
    required this.fullName,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.isEmailVerified,
    required this.roles,
  });
  User copyWith({
    String? id,
    String? sub,
    String? username,
    String? fullName,
    String? firstName,
    String? lastName,
    String? email,
    bool? isEmailVerified,
    List<String>? roles,
  }) {
    return User(
      id: id ?? this.id,
      sub: sub ?? this.sub,
      username: username ?? this.username,
      fullName: fullName ?? this.fullName,
      firstName: firstName ?? this.firstName,
      lastName: lastName ?? this.lastName,
      email: email ?? this.email,
      isEmailVerified: isEmailVerified ?? this.isEmailVerified,
      roles: roles ?? this.roles,
    );
  }
  // Método opcional para facilitar la depuración
  @override
  String toString() {
    return 'User(id: $id, name: $fullName, email: $email, roles: $roles)';
  }
  
  // ----------------------------------------------------
  // Factory Constructor para mapeo JSON
  // ----------------------------------------------------

  factory User.fromJson(Map<String, dynamic> json) {
    // 🔑 FIX: Manejar el ID que puede ser int (0) o String
    final dynamic rawId = json['id'];
    final String id = (rawId is int) 
        ? rawId.toString() 
        : (rawId as String? ?? '');

    // Keycloak usa 'sub' para el ID.
    final String sub = json['sub'] ?? '';
    
    // FIX: Priorizar 'username' del backend simple, sino usar 'preferred_username' de Keycloak
    final String username = json['username'] ?? json['preferred_username'] ?? '';

    // Nombre completo y partes
    final String name = json['name'] ?? '';
    final String givenName = json['given_name'] ?? '';
    final String familyName = json['family_name'] ?? '';

    // Email y verificación
    final String email = json['email'] ?? '';
    final bool emailVerified = json['email_verified'] ?? false;
    
    // Mapeo de Roles (asumiendo que viene de realm_access)
    final realmAccess = json['realm_access'] as Map<String, dynamic>?;
    final List<String> userRoles = (realmAccess?['roles'] as List<dynamic>?)
        ?.map((role) => role.toString())
        .toList() ?? [];

    return User(
      id: id,
      sub: sub,
      username: username,
      fullName: name,
      firstName: givenName,
      lastName: familyName,
      email: email,
      isEmailVerified: emailVerified,
      roles: userRoles, 
    );
  }
}