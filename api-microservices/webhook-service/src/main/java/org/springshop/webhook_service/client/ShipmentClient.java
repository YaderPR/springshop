package org.springshop.webhook_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springshop.webhook_service.dto.shipment.ShipmentRequest;
import org.springshop.webhook_service.dto.shipment.ShipmentResponse;

@Component
public class ShipmentClient {
    private final RestTemplate restTemplate;
    
    // URL base del microservicio de usuarios (Se obtiene de application.properties)
    private final String shipmentServiceBaseUrl;

    public ShipmentClient(RestTemplateBuilder builder, 
                      @Value("${shipment.service.url}") String shipmentServiceBaseUrl) {
        this.restTemplate = builder.build();
        //${clients.user-service.url} cuando ya este configurado como arquitectura de microservicios
        this.shipmentServiceBaseUrl = shipmentServiceBaseUrl;
    }
    public Optional<ShipmentResponse> findById(Integer shipmentId) {
        
        String url = shipmentServiceBaseUrl + "/api/v2/shipments/{shipmentId}";
        
        try {
            ShipmentResponse response = restTemplate.getForObject(url, ShipmentResponse.class, shipmentId);
            return Optional.ofNullable(response);
            
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
            
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException("Error al comunicarse con el servicio de usuarios: " + ex.getStatusCode(), ex);

        } catch (Exception ex) {
            throw new RuntimeException("Error inesperado en el servicio de usuarios: ", ex);
        }
    }
    public ShipmentResponse createShipment(ShipmentRequest request) {
        String url = shipmentServiceBaseUrl + "/api/v2/shipments";

        try {
            ShipmentResponse response = restTemplate.postForObject(
                url, 
                request, 
                ShipmentResponse.class
            );

            return response;

        } catch (HttpClientErrorException ex) {
            // Manejo de errores 4xx (400 Bad Request, 409 Conflict, etc.)
            System.err.println("Error del cliente al crear el envío (HTTP " + ex.getStatusCode() + "): " + ex.getResponseBodyAsString());
            throw new RuntimeException("Fallo al crear el envío: " + ex.getMessage(), ex);

        } catch (HttpServerErrorException ex) {
            // Manejo de errores 5xx
            System.err.println("Error del servidor de envíos (HTTP " + ex.getStatusCode() + "): " + ex.getResponseBodyAsString());
            throw new RuntimeException("Servicio de envíos falló.", ex);

        } catch (ResourceAccessException ex) {
            // Manejo de errores de conexión/red (servicio caído)
            System.err.println("Error de conexión al servicio de envíos: " + ex.getMessage());
            throw new RuntimeException("Servicio de envíos no disponible.", ex);
        }
    }
}
