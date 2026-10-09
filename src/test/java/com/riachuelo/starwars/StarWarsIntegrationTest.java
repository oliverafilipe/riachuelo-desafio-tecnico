package com.riachuelo.starwars;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riachuelo.starwars.client.SwapiClient;
import com.riachuelo.starwars.dto.SwapiFilmDTO;
import com.riachuelo.starwars.dto.SwapiResponseDTO;
import com.riachuelo.starwars.dto.UpdateDescriptionDTO;
import com.riachuelo.starwars.service.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StarWarsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieService movieService;

    @MockBean
    private SwapiClient swapiClient;

    @BeforeEach
    void setUp() {
        SwapiFilmDTO film1 = new SwapiFilmDTO(
                "A New Hope",
                4,
                "It is a period of civil war...",
                "George Lucas",
                "Gary Kurtz, Rick McCallum",
                "1977-05-25",
                "https://swapi.dev/api/films/1/"
        );
        SwapiFilmDTO film2 = new SwapiFilmDTO(
                "The Empire Strikes Back",
                5,
                "It is a dark time for the Rebellion...",
                "Irvin Kershner",
                "Gary Kurtz, Rick McCallum",
                "1980-05-21",
                "https://swapi.dev/api/films/2/"
        );

        when(swapiClient.buscarFilmesDaApi()).thenReturn(new SwapiResponseDTO(2, List.of(film1, film2)));
        movieService.carregarFilmesEmMemoria();
    }

    @Test
    @DisplayName("Fluxo completo de integração: listar, detalhar e atualizar descrição")
    void fluxoCompletoDeIntegracao() throws Exception {
        // 1. Listar filmes
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].version", is(1)));

        // 2. Detalhar filme por ID
        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("A New Hope")))
                .andExpect(jsonPath("$.version", is(1)));

        // 3. Atualizar descrição (versão deve ir para 2)
        UpdateDescriptionDTO updateDto1 = new UpdateDescriptionDTO("Nova descrição 1");
        mockMvc.perform(put("/api/movies/1/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.openingCrawl", is("Nova descrição 1")))
                .andExpect(jsonPath("$.version", is(2)));

        // 4. Atualizar descrição novamente (versão deve ir para 3)
        UpdateDescriptionDTO updateDto2 = new UpdateDescriptionDTO("Nova descrição 2");
        mockMvc.perform(put("/api/movies/1/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.openingCrawl", is("Nova descrição 2")))
                .andExpect(jsonPath("$.version", is(3)));

        // 5. Verificar que o detalhe reflete a versão 3 e nova descrição
        mockMvc.perform(get("/api/movies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openingCrawl", is("Nova descrição 2")))
                .andExpect(jsonPath("$.version", is(3)));

        // 6. Buscar ID inexistente retorna 404
        mockMvc.perform(get("/api/movies/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}

