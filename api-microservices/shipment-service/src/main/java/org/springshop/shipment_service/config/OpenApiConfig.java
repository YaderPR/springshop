package org.springshop.shipment_service.config;

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
                .url("http://localhost:8090")
                .description("Servidor local");

        Server gatewayAddressServer = new Server()
            .url("http://localhost:8080/api/v2/addresses") // URL del gateway
            .description("Acceso vía API Gateway");
        Server gatewayShipmentServer = new Server()
            .url("http://localhost:8080/api/v2/shipments") // URL del gateway
            .description("Acceso vía API Gateway");
        return new OpenAPI()
                .info(new Info()
                        .title("Mi API")
                        .version("1.0.0")
                        .description("Documentación de mi API con Springdoc"))
                .servers(List.of(serverLocal, gatewayAddressServer, gatewayShipmentServer));
    }
}

