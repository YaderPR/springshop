package org.springshop.api.gateway.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
@Configuration("docker")
public class TestOpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // URLs públicas de Keycloak (accesibles desde el navegador)
        String authUrl = "http://keycloak:9090/realms/master/protocol/openid-connect/auth";
        String tokenUrl = "http://keycloak:9090/realms/master/protocol/openid-connect/token";

        SecurityScheme oAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("Autenticación mediante Keycloak (Authorization Code Flow)")
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl(authUrl)
                                .tokenUrl(tokenUrl)
                                .scopes(new Scopes()
                                        .addString("openid", "Acceso básico"))));

        return new OpenAPI()
                .info(new Info()
                        .title("API protegida con Keycloak")
                        .version("1.0.0")
                        .description("Swagger con flujo Authorization Code (OAuth2 + Keycloak)"))
                .components(new Components()
                        .addSecuritySchemes("keycloak", oAuthScheme))
                .security(List.of(new SecurityRequirement().addList("keycloak")));
    }
}
