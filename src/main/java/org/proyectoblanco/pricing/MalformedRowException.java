package org.proyectoblanco.pricing;

/**
 * Una fila individual del CSV no tiene la cantidad de columnas esperada.
 */
public class MalformedRowException extends RuntimeException {

    public MalformedRowException(String message) {
        super(message);
    }
}
