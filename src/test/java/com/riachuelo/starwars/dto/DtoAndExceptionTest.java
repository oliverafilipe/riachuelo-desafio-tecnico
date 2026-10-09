package com.riachuelo.starwars.dto;

import com.riachuelo.starwars.exception.ErrorResponseDTO;
import com.riachuelo.starwars.exception.GlobalExceptionHandler;
import com.riachuelo.starwars.exception.MovieNotFoundException;
import com.riachuelo.starwars.exception.SwapiIntegrationException;
//import com.riachuelo.starwars.model.Movie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoAndExceptionTest {

    @Test
    @DisplayName("Deve testar getters e setters do MovieResponseDTO")
    void deveTestarMovieResponseDTO() {
        MovieResponseDTO dto = new MovieResponseDTO();
        dto.setId(1L);
        dto.setTitle("A New Hope");
        dto.setEpisodeId(4);
        dto.setOpeningCrawl("Opening...");
        dto.setDirector("George Lucas");
        dto.setProducer("Gary Kurtz");
        dto.setReleaseDate("1977-05-25");
        dto.setVersion(1L);

        assertEquals(1L, dto.getId());
        assertEquals("A New Hope", dto.getTitle());
        assertEquals(4, dto.getEpisodeId());
        assertEquals("Opening...", dto.getOpeningCrawl());
        assertEquals("George Lucas", dto.getDirector());
        assertEquals("Gary Kurtz", dto.getProducer());
        assertEquals("1977-05-25", dto.getReleaseDate());
        assertEquals(1L, dto.getVersion());

        assertNull(MovieResponseDTO.from(null));
    }

    @Test
    @DisplayName("Deve testar getters e setters do ErrorResponseDTO")
    void deveTestarErrorResponseDTO() {
        ErrorResponseDTO error = new ErrorResponseDTO();
        LocalDateTime now = LocalDateTime.now();

        error.setTimestamp(now);
        error.setStatus(404);
        error.setError("Not Found");
        error.setMessage("Mensagem de erro");
        error.setPath("/api/movies/999");

        assertEquals(now, error.getTimestamp());
        assertEquals(404, error.getStatus());
        assertEquals("Not Found", error.getError());
        assertEquals("Mensagem de erro", error.getMessage());
        assertEquals("/api/movies/999", error.getPath());
    }

    @Test
    @DisplayName("Deve testar getters e setters do SwapiFilmDTO e SwapiResponseDTO")
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
    @DisplayName("Deve testar contrutor por ID de MovieNotFoundException")
    void deveTestarMovieNotFoundException() {
        MovieNotFoundException ex = new MovieNotFoundException(10L);
        assertTrue(ex.getMessage().contains("10"));
    }

    @Test
    @DisplayName("Deve testar tratamento de exceção genérica e SwapiIntegrationException no GlobalExceptionHandler")
    void deveTestarGlobalExceptionHandlerExcecoes() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");

        // SwapiIntegrationException
        SwapiIntegrationException swapiEx = new SwapiIntegrationException("Erro SWAPI");
        ResponseEntity<ErrorResponseDTO> responseSwapi = handler.handleSwapiIntegrationException(swapiEx, request);
        assertEquals(HttpStatus.BAD_GATEWAY, responseSwapi.getStatusCode());
        assertEquals("Erro SWAPI", responseSwapi.getBody().getMessage());

        // Generic Exception
        Exception genericEx = new RuntimeException("Erro genérico no servidor");
        ResponseEntity<ErrorResponseDTO> responseGeneric = handler.handleGenericException(genericEx, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseGeneric.getStatusCode());
        assertEquals("Ocorreu um erro interno no servidor.", responseGeneric.getBody().getMessage());
    }
}

