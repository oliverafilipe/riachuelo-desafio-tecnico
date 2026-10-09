package com.riachuelo.starwars.client;

import com.riachuelo.starwars.dto.SwapiResponseDTO;
import com.riachuelo.starwars.exception.SwapiIntegrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente responsável pela integração HTTP com a The Star Wars API (SWAPI).
 */
@Component
public class SwapiClient {

    private static final Logger log = LoggerFactory.getLogger(SwapiClient.class);

    private final RestTemplate restTemplate;
    private final String swapiFilmsUrl;

    public SwapiClient(RestTemplate restTemplate,
                       @Value("${swapi.films.url:https://swapi.dev/api/films/}") String swapiFilmsUrl) {
        this.restTemplate = restTemplate;
        this.swapiFilmsUrl = swapiFilmsUrl;
    }

    /**
     * Busca a lista de filmes disponíveis na SWAPI.
     *
     * @return SwapiResponseDTO contendo o total e a lista de filmes.
     */
    public SwapiResponseDTO buscarFilmesDaApi() {
        try {
            log.info("Iniciando requisição para a SWAPI na URL: {}", swapiFilmsUrl);
            ResponseEntity<SwapiResponseDTO> response = restTemplate.getForEntity(swapiFilmsUrl, SwapiResponseDTO.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Filmes recuperados da SWAPI com sucesso. Total retornado: {}",
                        response.getBody().getResults() != null ? response.getBody().getResults().size() : 0);
                return response.getBody();
            }

            throw new SwapiIntegrationException("A SWAPI retornou status não esperado: " + response.getStatusCode());
        } catch (RestClientException e) {
            log.error("Falha ao comunicar com a SWAPI: {}", e.getMessage(), e);
            throw new SwapiIntegrationException("Erro de comunicação com a SWAPI: " + e.getMessage(), e);
        }
    }
}

