package org.springshop.user_service.controller.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("error", "Not Found");
        errorDetails.put("message", ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex) {

        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof EntityNotFoundException
                || (rootCause != null && rootCause.getMessage().toLowerCase().contains("not found"))) {
            return handleEntityNotFoundException(new EntityNotFoundException(rootCause.getMessage()));
        }

        // Si no es un 404, continúa con el manejo genérico de 500
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("error", "Database Error");
        errorDetails.put("message", "A data integrity error occurred. Contact support with the transaction ID.");
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMissingRequestBody(HttpMessageNotReadableException ex) {
        
        // El mensaje de error interno puede ser muy técnico.
        String detail = ex.getMessage();
        
        String errorClientMessage;
        
        if (detail != null && detail.contains("Required request body is missing")) {
            errorClientMessage = "El cuerpo de la solicitud (JSON) está ausente o vacío. Es requerido para esta operación.";
        } else {
            errorClientMessage = "El formato de la solicitud es inválido. Verifique que el JSON esté bien formado.";
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("error", "Bad Request");
        responseBody.put("message", errorClientMessage);
        // Opcional: Puedes añadir el mensaje original para debug, pero NO en producción
        // responseBody.put("technical_detail", detail); 

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
