package org.springshop.cart_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server serverLocal = new Server()
                .url("http://localhost:8086")
                .description("Servidor local");

        Server gatewayServer = new Server()
            .url("http://localhost:8080/api/v2/carts") // URL del gateway
            .description("Acceso vía API Gateway");

        return new OpenAPI()
                .info(new Info()
                        .title("Mi API")
                        .version("1.0.0")
                        .description("Documentación de mi API con Springdoc"))
                .servers(List.of(serverLocal, gatewayServer));
    }
}

