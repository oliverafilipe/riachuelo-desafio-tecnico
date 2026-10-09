package com.riachuelo.starwars.client;

import com.riachuelo.starwars.dto.SwapiResponseDTO;
import com.riachuelo.starwars.exception.SwapiIntegrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SwapiClientTest {

    private static final String SWAPI_URL = "https://swapi.dev/api/films/";

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private SwapiClient swapiClient;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        swapiClient = new SwapiClient(restTemplate, SWAPI_URL);
    }

    @Test
    @DisplayName("Deve buscar filmes da SWAPI e deserializar com sucesso")
    void deveBuscarFilmesComSucesso() {
        String jsonResponse = """
                {
                    "count": 1,
                    "next": null,
                    "previous": null,
                    "results": [
                        {
                            "title": "A New Hope",
                            "episode_id": 4,
                            "opening_crawl": "It is a period of civil war...",
                            "director": "George Lucas",
                            "producer": "Gary Kurtz, Rick McCallum",
                            "release_date": "1977-05-25",
                            "url": "https://swapi.dev/api/films/1/"
                        }
                    ]
                }
                """;

        mockServer.expect(requestTo(SWAPI_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        SwapiResponseDTO response = swapiClient.buscarFilmesDaApi();

        mockServer.verify();
        assertNotNull(response);
        assertEquals(1, response.getCount());
        assertNotNull(response.getResults());
        assertEquals(1, response.getResults().size());

        var film = response.getResults().get(0);
        assertEquals("A New Hope", film.getTitle());
        assertEquals(4, film.getEpisodeId());
        assertEquals(1L, film.extrairId());
    }

    @Test
    @DisplayName("Deve lançar SwapiIntegrationException quando a SWAPI retornar erro HTTP")
    void deveLancarExcecaoQuandoSwapiRetornarErroHttp() {
        mockServer.expect(requestTo(SWAPI_URL))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(SwapiIntegrationException.class, () -> swapiClient.buscarFilmesDaApi());
        mockServer.verify();
    }
}

