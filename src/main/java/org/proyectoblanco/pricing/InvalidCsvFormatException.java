package org.proyectoblanco.pricing;

/**
 * El archivo CSV recibido no tiene la estructura esperada
 * (por ejemplo, encabezado ausente o incorrecto).
 */
public class InvalidCsvFormatException extends RuntimeException {

    public InvalidCsvFormatException(String message) {
        super(message);
    }
}
