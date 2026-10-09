package com.riachuelo.starwars.exception;

/**
 * Exceção lançada quando um filme não é encontrado na base em memória.
 */
public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String message) {
        super(message);
    }

    public MovieNotFoundException(Long id) {
        super("Filme com ID " + id + " não encontrado.");
    }
}

