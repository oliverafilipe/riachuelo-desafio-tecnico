package com.riachuelo.starwars.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riachuelo.starwars.dto.UpdateDescriptionDTO;
import com.riachuelo.starwars.exception.GlobalExceptionHandler;
import com.riachuelo.starwars.exception.MovieNotFoundException;
import com.riachuelo.starwars.model.Movie;
import com.riachuelo.starwars.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
@Import(GlobalExceptionHandler.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @Test
    @DisplayName("GET /api/movies - Deve retornar 200 e lista de filmes em MovieResponseDTO")
    void deveRetornarListaDeFilmes() throws Exception {
        Movie movie1 = new Movie(1L, "A New Hope", 4, "Opening crawl...",
                "George Lucas", "Gary Kurtz", "1977-05-25", 1L);
        Movie movie2 = new Movie(2L, "The Empire Strikes Back", 5, "Opening crawl 2...",
                "Irvin Kershner", "Gary Kurtz", "1980-05-21", 1L);

        when(movieService.listarFilmes()).thenReturn(List.of(movie1, movie2));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("A New Hope")))
                .andExpect(jsonPath("$[0].version", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("The Empire Strikes Back")));
    }

    @Test
    @DisplayName("GET /api/movies/{id} - Deve retornar 200 quando filme for encontrado")
    void deveRetornarFilmePorIdQuandoExistir() throws Exception {
        Movie movie = new Movie(1L, "A New Hope", 4, "Opening crawl...",
                "George Lucas", "Gary Kurtz", "1977-05-25", 1L);

        when(movieService.buscarPorId(1L)).thenReturn(movie);

        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("A New Hope")))
                .andExpect(jsonPath("$.episodeId", is(4)))
                .andExpect(jsonPath("$.openingCrawl", is("Opening crawl...")))
                .andExpect(jsonPath("$.director", is("George Lucas")))
                .andExpect(jsonPath("$.producer", is("Gary Kurtz")))
                .andExpect(jsonPath("$.releaseDate", is("1977-05-25")))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("GET /api/movies/{id} - Deve retornar 404 quando filme não existir")
    void deveRetornar404AoBuscarFilmeInexistente() throws Exception {
        when(movieService.buscarPorId(999L)).thenThrow(new MovieNotFoundException(999L));

        mockMvc.perform(get("/api/movies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Filme com ID 999 não encontrado.")));
    }

    @Test
    @DisplayName("PUT /api/movies/{id}/description - Deve retornar 200 com filme atualizado e versão incrementada")
    void deveAtualizarDescricaoComSucesso() throws Exception {
        String novaDescricao = "Nova descrição épica para o filme Star Wars...";
        Movie movieAtualizado = new Movie(1L, "A New Hope", 4, novaDescricao,
                "George Lucas", "Gary Kurtz", "1977-05-25", 2L);

        when(movieService.atualizarDescricao(eq(1L), eq(novaDescricao))).thenReturn(movieAtualizado);

        UpdateDescriptionDTO dto = new UpdateDescriptionDTO(novaDescricao);

        mockMvc.perform(put("/api/movies/1/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("A New Hope")))
                .andExpect(jsonPath("$.openingCrawl", is(novaDescricao)))
                .andExpect(jsonPath("$.version", is(2)));
    }

    @Test
    @DisplayName("PUT /api/movies/{id}/description - Deve retornar 404 quando filme não existir")
    void deveRetornar404AoAtualizarFilmeInexistente() throws Exception {
        String novaDescricao = "Nova descrição";
        when(movieService.atualizarDescricao(eq(999L), eq(novaDescricao)))
                .thenThrow(new MovieNotFoundException(999L));

        UpdateDescriptionDTO dto = new UpdateDescriptionDTO(novaDescricao);

        mockMvc.perform(put("/api/movies/999/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @DisplayName("PUT /api/movies/{id}/description - Deve retornar 400 quando openingCrawl for vazio")
    void deveRetornar400QuandoDescricaoForVazia() throws Exception {
        UpdateDescriptionDTO dto = new UpdateDescriptionDTO("");

        mockMvc.perform(put("/api/movies/1/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message").exists());
    }
}

