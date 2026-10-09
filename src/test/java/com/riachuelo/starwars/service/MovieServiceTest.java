package com.riachuelo.starwars.service;

import com.riachuelo.starwars.client.SwapiClient;
import com.riachuelo.starwars.dto.SwapiFilmDTO;
import com.riachuelo.starwars.dto.SwapiResponseDTO;
import com.riachuelo.starwars.exception.MovieNotFoundException;
import com.riachuelo.starwars.exception.SwapiIntegrationException;
import com.riachuelo.starwars.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private SwapiClient swapiClient;

    @InjectMocks
    private MovieService movieService;

    private SwapiFilmDTO film1;
    private SwapiFilmDTO film2;

    @BeforeEach
    void setUp() {
        film1 = new SwapiFilmDTO(
                "A New Hope",
                4,
                "It is a period of civil war...",
                "George Lucas",
                "Gary Kurtz, Rick McCallum",
                "1977-05-25",
                "https://swapi.dev/api/films/1/"
        );

        film2 = new SwapiFilmDTO(
                "The Empire Strikes Back",
                5,
                "It is a dark time for the Rebellion...",
                "Irvin Kershner",
                "Gary Kurtz, Rick McCallum",
                "1980-05-21",
                "https://swapi.dev/api/films/2/"
        );
    }

    @Test
    @DisplayName("Deve carregar filmes em memória com sucesso a partir da SWAPI")
    void deveCarregarFilmesEmMemoriaComSucesso() {
        SwapiResponseDTO swapiResponse = new SwapiResponseDTO(2, List.of(film1, film2));
        when(swapiClient.buscarFilmesDaApi()).thenReturn(swapiResponse);

        movieService.carregarFilmesEmMemoria();

        List<Movie> filmes = movieService.listarFilmes();
        assertEquals(2, filmes.size());

        Movie movie1 = movieService.buscarPorId(1L);
        assertNotNull(movie1);
        assertEquals("A New Hope", movie1.getTitle());
        assertEquals(4, movie1.getEpisodeId());
        assertEquals(1L, movie1.getVersion());

        Movie movie2 = movieService.buscarPorId(2L);
        assertNotNull(movie2);
        assertEquals("The Empire Strikes Back", movie2.getTitle());
        assertEquals(5, movie2.getEpisodeId());
        assertEquals(1L, movie2.getVersion());

        verify(swapiClient, times(1)).buscarFilmesDaApi();
    }

    @Test
    @DisplayName("Deve tratar falha ao carregar filmes da SWAPI sem quebrar a aplicação")
    void deveTratarFalhaAoCarregarFilmesDaSwapi() {
        when(swapiClient.buscarFilmesDaApi())
                .thenThrow(new SwapiIntegrationException("Erro de conexão"));

        assertDoesNotThrow(() -> movieService.carregarFilmesEmMemoria());
        assertTrue(movieService.listarFilmes().isEmpty());
    }

    @Test
    @DisplayName("Deve listar todos os filmes em memória")
    void deveListarFilmesEmMemoria() {
        SwapiResponseDTO swapiResponse = new SwapiResponseDTO(1, List.of(film1));
        when(swapiClient.buscarFilmesDaApi()).thenReturn(swapiResponse);
        movieService.carregarFilmesEmMemoria();

        List<Movie> filmes = movieService.listarFilmes();
        assertEquals(1, filmes.size());
        assertEquals("A New Hope", filmes.get(0).getTitle());
    }

    @Test
    @DisplayName("Deve buscar filme por ID com sucesso")
    void deveBuscarFilmePorIdExistente() {
        SwapiResponseDTO swapiResponse = new SwapiResponseDTO(1, List.of(film1));
        when(swapiClient.buscarFilmesDaApi()).thenReturn(swapiResponse);
        movieService.carregarFilmesEmMemoria();

        Movie filme = movieService.buscarPorId(1L);
        assertNotNull(filme);
        assertEquals(1L, filme.getId());
        assertEquals("A New Hope", filme.getTitle());
        assertEquals(1L, filme.getVersion());
    }

    @Test
    @DisplayName("Deve lançar MovieNotFoundException ao buscar ID inexistente")
    void deveLancarExcecaoAoBuscarIdInexistente() {
        MovieNotFoundException exception = assertThrows(
                MovieNotFoundException.class,
                () -> movieService.buscarPorId(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve atualizar descrição e incrementar versão com sucesso")
    void deveAtualizarDescricaoEIncrementarVersao() {
        SwapiResponseDTO swapiResponse = new SwapiResponseDTO(1, List.of(film1));
        when(swapiClient.buscarFilmesDaApi()).thenReturn(swapiResponse);
        movieService.carregarFilmesEmMemoria();

        String novaDescricao = "Nova descrição incrível para Star Wars.";
        Movie filmeAtualizado = movieService.atualizarDescricao(1L, novaDescricao);

        assertNotNull(filmeAtualizado);
        assertEquals(novaDescricao, filmeAtualizado.getOpeningCrawl());
        assertEquals(2L, filmeAtualizado.getVersion());

        // Segunda atualização
        String outraDescricao = "Outra atualização da saga.";
        Movie filmeAtualizadoNovamente = movieService.atualizarDescricao(1L, outraDescricao);
        assertEquals(outraDescricao, filmeAtualizadoNovamente.getOpeningCrawl());
        assertEquals(3L, filmeAtualizadoNovamente.getVersion());
    }

    @Test
    @DisplayName("Deve lançar MovieNotFoundException ao tentar atualizar filme inexistente")
    void deveLancarExcecaoAoAtualizarDescricaoDeFilmeInexistente() {
        assertThrows(
                MovieNotFoundException.class,
                () -> movieService.atualizarDescricao(999L, "Texto qualquer")
        );
    }

    @Test
    @DisplayName("Deve garantir incremento correto de versão em atualizações concorrentes")
    void deveGarantirIncrementoDeVersaoEmConcorrencia() throws InterruptedException {
        SwapiResponseDTO swapiResponse = new SwapiResponseDTO(1, List.of(film1));
        when(swapiClient.buscarFilmesDaApi()).thenReturn(swapiResponse);
        movieService.carregarFilmesEmMemoria();

        int chamadas = 30;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(chamadas);

        for (int i = 0; i < chamadas; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    movieService.atualizarDescricao(1L, "Atualização número " + index);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Movie filmeFinal = movieService.buscarPorId(1L);
        assertEquals(31L, filmeFinal.getVersion());
    }
}

