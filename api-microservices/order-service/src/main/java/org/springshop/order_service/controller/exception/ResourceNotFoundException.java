package org.springshop.order_service.controller.exception;

/**
 * Excepción lanzada cuando un recurso (entidad) solicitado por su ID no puede ser encontrado.
 * Esta excepción no necesita ser marcada (unchecked) ya que extiende RuntimeException.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor con un mensaje específico sobre el recurso no encontrado.
     * * @param message El mensaje de error que describe el recurso faltante.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor que permite envolver otra excepción (throwable).
     * * @param message El mensaje de error.
     * @param cause La causa original de la excepción.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}