package com.riachuelo.starwars.controller;

import com.riachuelo.starwars.dto.MovieResponseDTO;
import com.riachuelo.starwars.dto.UpdateDescriptionDTO;
import com.riachuelo.starwars.model.Movie;
import com.riachuelo.starwars.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST que expõe os endpoints para consulta e gerenciamento de filmes Star Wars.
 */
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    /**
     * Lista todos os filmes carregados em memória.
     *
     * @return Lista com os filmes e suas respectivas versões.
     */
    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> listarFilmes() {
        List<Movie> filmes = movieService.listarFilmes();
        List<MovieResponseDTO> response = filmes.stream()
                .sorted(Comparator.comparing(Movie::getId))
                .map(MovieResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * Detalha um filme específico pelo seu identificador.
     *
     * @param id Identificador do filme.
     * @return Dados detalhados do filme.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> detalharFilme(@PathVariable Long id) {
        Movie movie = movieService.buscarPorId(id);
        return ResponseEntity.ok(MovieResponseDTO.from(movie));
    }

    /**
     * Atualiza a descrição (openingCrawl) de um filme e incrementa sua versão.
     *
     * @param id Identificador do filme.
     * @param dto DTO com a nova descrição.
     * @return Filme atualizado com versão incrementada.
     */
    @PutMapping("/{id}/description")
    public ResponseEntity<MovieResponseDTO> atualizarDescricao(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDescriptionDTO dto) {
        Movie movieAtualizado = movieService.atualizarDescricao(id, dto.getOpeningCrawl());
        return ResponseEntity.ok(MovieResponseDTO.from(movieAtualizado));
    }
}

