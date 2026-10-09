package com.riachuelo.starwars.exception;

/**
 * Exceção lançada em caso de falhas na comunicação ou resposta da SWAPI.
 */
public class SwapiIntegrationException extends RuntimeException {

    public SwapiIntegrationException(String message) {
        super(message);
    }

    public SwapiIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

