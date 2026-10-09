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

    @Test
    @DisplayName("Deve testar getters, setters, equals, hashCode e toString")
    void deveTestarGettersSettersEqualsHashCodeToString() {
        Movie movie1 = new Movie();
        movie1.setId(1L);
        movie1.setTitle("A New Hope");
        movie1.setEpisodeId(4);
        movie1.setOpeningCrawl("Opening...");
        movie1.setDirector("George Lucas");
        movie1.setProducer("Gary Kurtz");
        movie1.setReleaseDate("1977-05-25");
        movie1.setVersion(1L);

        assertEquals(1L, movie1.getId());
        assertEquals("A New Hope", movie1.getTitle());
        assertEquals(4, movie1.getEpisodeId());
        assertEquals("Opening...", movie1.getOpeningCrawl());
        assertEquals("George Lucas", movie1.getDirector());
        assertEquals("Gary Kurtz", movie1.getProducer());
        assertEquals("1977-05-25", movie1.getReleaseDate());
        assertEquals(1L, movie1.getVersion());

        Movie movie2 = new Movie(1L, "A New Hope", 4, "Opening...", "George Lucas", "Gary Kurtz", "1977-05-25", 1L);

        assertEquals(movie1, movie2);
        assertEquals(movie1.hashCode(), movie2.hashCode());
        assertEquals(movie1, movie1);
        assertNotEquals(movie1, null);
        assertNotEquals(movie1, "outraString");

        Movie movie3 = new Movie();
        movie3.setId(2L);
        assertNotEquals(movie1, movie3);

        assertTrue(movie1.toString().contains("A New Hope"));
    }
}
