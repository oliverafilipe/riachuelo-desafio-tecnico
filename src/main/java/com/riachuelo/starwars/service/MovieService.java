package com.riachuelo.starwars.service;

import com.riachuelo.starwars.client.SwapiClient;
import com.riachuelo.starwars.dto.SwapiFilmDTO;
import com.riachuelo.starwars.dto.SwapiResponseDTO;
import com.riachuelo.starwars.exception.MovieNotFoundException;
import com.riachuelo.starwars.model.Movie;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço responsável pelas regras de negócio e gerenciamento dos filmes em memória.
 */
@Service
public class MovieService {

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);

    private final SwapiClient swapiClient;
    private final Map<Long, Movie> movieCache = new ConcurrentHashMap<>();

    public MovieService(SwapiClient swapiClient) {
        this.swapiClient = swapiClient;
    }

    /**
     * Carrega os filmes da SWAPI para a memória na inicialização da aplicação.
     */
    @PostConstruct
    public void carregarFilmesEmMemoria() {
        try {
            log.info("Carregando filmes da SWAPI em memória...");
            SwapiResponseDTO response = swapiClient.buscarFilmesDaApi();

            if (response != null && response.getResults() != null) {
                movieCache.clear();
                for (SwapiFilmDTO dto : response.getResults()) {
                    Long id = dto.extrairId();
                    if (id != null) {
                        Movie movie = new Movie(
                                id,
                                dto.getTitle(),
                                dto.getEpisodeId(),
                                dto.getOpeningCrawl(),
                                dto.getDirector(),
                                dto.getProducer(),
                                dto.getReleaseDate(),
                                1L
                        );
                        movieCache.put(id, movie);
                    }
                }
                log.info("Carga de filmes concluída com sucesso. Total em memória: {}", movieCache.size());
            } else {
                log.warn("Nenhum filme foi retornado pela SWAPI durante a inicialização.");
            }
        } catch (Exception e) {
            log.error("Falha ao carregar filmes da SWAPI na inicialização: {}", e.getMessage(), e);
        }
    }

    /**
     * Retorna a lista de todos os filmes carregados em memória.
     *
     * @return Lista com todos os filmes em cache.
     */
    public List<Movie> listarFilmes() {
        return new ArrayList<>(movieCache.values());
    }

    /**
     * Busca um filme em memória pelo seu identificador único.
     *
     * @param id Identificador do filme.
     * @return Entidade Movie encontrada.
     * @throws MovieNotFoundException Se o filme não existir no cache.
     */
    public Movie buscarPorId(Long id) {
        Movie movie = movieCache.get(id);
        if (movie == null) {
            throw new MovieNotFoundException(id);
        }
        return movie;
    }

    /**
     * Atualiza a descrição (openingCrawl) de um filme e incrementa sua versão.
     * O acesso é sincronizado por filme para garantir concorrência thread-safe.
     *
     * @param id Identificador do filme.
     * @param novaDescricao Novo texto de openingCrawl.
     * @return Entidade Movie atualizada com versão incrementada.
     * @throws MovieNotFoundException Se o filme não for encontrado.
     */
    public Movie atualizarDescricao(Long id, String novaDescricao) {
        Movie movie = buscarPorId(id);
        synchronized (movie) {
            movie.setOpeningCrawl(novaDescricao);
            movie.incrementarVersao();
        }
        return movie;
    }
}

