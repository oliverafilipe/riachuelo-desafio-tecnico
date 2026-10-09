package com.riachuelo.starwars.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    @Test
    @DisplayName("Deve inicializar com versão 1 por padrão")
    void deveInicializarComVersaoPadraoUm() {
        Movie movie = new Movie();
        assertEquals(1L, movie.getVersion());

        Movie movieComParametros = new Movie(1L, "A New Hope", 4, "Opening crawl...",
                "George Lucas", "Gary Kurtz", "1977-05-25", null);
        assertEquals(1L, movieComParametros.getVersion());
    }

    @Test
    @DisplayName("Deve incrementar a versão sucessivamente")
    void deveIncrementarVersaoSucessivamente() {
        Movie movie = new Movie(1L, "A New Hope", 4, "Opening crawl...",
                "George Lucas", "Gary Kurtz", "1977-05-25", 1L);

        assertEquals(1L, movie.getVersion());

        movie.incrementarVersao();
        assertEquals(2L, movie.getVersion());

        movie.incrementarVersao();
        assertEquals(3L, movie.getVersion());
    }

    @Test
    @DisplayName("Deve incrementar a versão de forma thread-safe sob concorrência")
    void deveIncrementarVersaoSobConcorrencia() throws InterruptedException {
        Movie movie = new Movie(1L, "A New Hope", 4, "Opening crawl...",
                "George Lucas", "Gary Kurtz", "1977-05-25", 1L);

        int threads = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executorService.submit(() -> {
                try {
                    movie.incrementarVersao();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        assertEquals(51L, movie.getVersion());
    }
}

