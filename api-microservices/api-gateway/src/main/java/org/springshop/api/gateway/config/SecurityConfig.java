package org.springshop.api.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Flux;

@Configuration
@EnableWebFluxSecurity
@Profile("docker")
public class SecurityConfig {

    @Value("${ISSUER_URI_DOCKER}")
    private String issuerUri;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http,
            ReactiveJwtDecoder jwtDecoder) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Rutas públicas para Swagger
                        .pathMatchers("/webjars/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .pathMatchers("/actuator/**").permitAll()
                        
                        // Rutas protegidas de microservicios
                        .pathMatchers("/api/v2/products/**").authenticated()
                        .pathMatchers("/api/v2/carts/**").authenticated()
                        .pathMatchers("/api/v2/orders/**").authenticated()
                        .pathMatchers("/api/v2/payments/**").authenticated()
                        .pathMatchers("/api/v2/shipments/**").authenticated()
                        .pathMatchers("/api/v2/addresses/**").authenticated()
                        .pathMatchers("/api/v2/webhooks/**").authenticated()
                        .pathMatchers("/api/v2/files/**").authenticated()
                        .pathMatchers("/api/v2/users/**").authenticated()
                        
                        // Regla final: cualquier otra ruta requiere autenticación
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtDecoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${ISSUER_URI_DOCKER}") String issuerUri) {
        return ReactiveJwtDecoders.fromIssuerLocation(issuerUri);
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                var roles = (Iterable<?>) realmAccess.get("roles");
                // Prepend "ROLE_" si es necesario, Keycloak normalmente no lo necesita
                return Flux.fromIterable(
                        AuthorityUtils.commaSeparatedStringToAuthorityList(
                                String.join(",", (Iterable<String>) roles)));
            }
            return Flux.empty();
        });
        return converter;
    }
}
