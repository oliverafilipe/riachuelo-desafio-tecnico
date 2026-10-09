package com.riachuelo.starwars.dto;

import com.riachuelo.starwars.exception.ErrorResponseDTO;
import com.riachuelo.starwars.exception.GlobalExceptionHandler;
import com.riachuelo.starwars.exception.MovieNotFoundException;
import com.riachuelo.starwars.exception.SwapiIntegrationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoAndExceptionTest {

    @Test
    @DisplayName("Deve testar Record MovieResponseDTO")
    void deveTestarMovieResponseDTO() {
        MovieResponseDTO dto = new MovieResponseDTO(1L, "A New Hope", 4, "Opening...", "George Lucas", "Gary Kurtz", "1977-05-25", 1L);

        assertEquals(1L, dto.id());
        assertEquals("A New Hope", dto.title());
        assertEquals(4, dto.episodeId());
        assertEquals("Opening...", dto.openingCrawl());
        assertEquals("George Lucas", dto.director());
        assertEquals("Gary Kurtz", dto.producer());
        assertEquals("1977-05-25", dto.releaseDate());
        assertEquals(1L, dto.version());

        assertNull(MovieResponseDTO.from(null));
    }

    @Test
    @DisplayName("Deve testar Record ErrorResponseDTO")
    void deveTestarErrorResponseDTO() {
        LocalDateTime now = LocalDateTime.now();
        ErrorResponseDTO error = new ErrorResponseDTO(now, 404, "Not Found", "Mensagem de erro", "/api/movies/999");

        assertEquals(now, error.timestamp());
        assertEquals(404, error.status());
        assertEquals("Not Found", error.error());
        assertEquals("Mensagem de erro", error.message());
        assertEquals("/api/movies/999", error.path());
    }

    @Test
    @DisplayName("Deve testar SwapiFilmDTO e SwapiResponseDTO")
    void deveTestarSwapiFilmDTOESwapiResponseDTO() {
        SwapiFilmDTO film = new SwapiFilmDTO();
        film.setTitle("A New Hope");
        film.setEpisodeId(4);
        film.setOpeningCrawl("Opening...");
        film.setDirector("George Lucas");
        film.setProducer("Gary Kurtz");
        film.setReleaseDate("1977-05-25");
        film.setUrl("https://swapi.dev/api/films/1/");

        assertEquals("A New Hope", film.getTitle());
        assertEquals(4, film.getEpisodeId());
        assertEquals("Opening...", film.getOpeningCrawl());
        assertEquals("George Lucas", film.getDirector());
        assertEquals("Gary Kurtz", film.getProducer());
        assertEquals("1977-05-25", film.getReleaseDate());
        assertEquals("https://swapi.dev/api/films/1/", film.getUrl());

        SwapiResponseDTO response = new SwapiResponseDTO();
        response.setCount(1);
        response.setResults(List.of(film));

        assertEquals(1, response.getCount());
        assertEquals(1, response.getResults().size());

        // Test fallback id extrairId when URL is null
        SwapiFilmDTO filmNoUrl = new SwapiFilmDTO("Title", 6, "Opening", "Dir", "Prod", "1983-05-25", null);
        assertEquals(6L, filmNoUrl.extrairId());
    }

    @Test
    @DisplayName("Deve testar construtor por ID de MovieNotFoundException")
    void deveTestarMovieNotFoundException() {
        MovieNotFoundException ex = new MovieNotFoundException(10L);
        assertTrue(ex.getMessage().contains("10"));
    }

    @Test
    @DisplayName("Deve testar tratamento de exceções no GlobalExceptionHandler")
    void deveTestarGlobalExceptionHandlerExcecoes() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");

        // SwapiIntegrationException
        SwapiIntegrationException swapiEx = new SwapiIntegrationException("Erro SWAPI");
        ResponseEntity<ErrorResponseDTO> responseSwapi = handler.handleSwapiIntegrationException(swapiEx, request);
        assertEquals(HttpStatus.BAD_GATEWAY, responseSwapi.getStatusCode());
        assertEquals("Erro SWAPI", responseSwapi.getBody().message());

        // Generic Exception
        Exception genericEx = new RuntimeException("Erro genérico no servidor");
        ResponseEntity<ErrorResponseDTO> responseGeneric = handler.handleGenericException(genericEx, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseGeneric.getStatusCode());
        assertEquals("Ocorreu um erro interno no servidor.", responseGeneric.getBody().message());
    }
}
