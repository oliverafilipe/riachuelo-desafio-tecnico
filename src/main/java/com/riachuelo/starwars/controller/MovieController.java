package com.riachuelo.starwars.controller;

import com.riachuelo.starwars.dto.MovieResponseDTO;
import com.riachuelo.starwars.dto.UpdateDescriptionDTO;
import com.riachuelo.starwars.exception.ErrorResponseDTO;
import com.riachuelo.starwars.model.Movie;
import com.riachuelo.starwars.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
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
@Tag(name = "Filmes", description = "Endpoints para listagem, detalhamento e atualização de filmes da saga Star Wars")
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
    @Operation(summary = "Listar todos os filmes", description = "Retorna a lista de filmes da saga Star Wars carregados em memória com suas respectivas versões e detalhes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de filmes retornada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = MovieResponseDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
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
    @Operation(summary = "Detalhar filme por ID", description = "Busca e exibe os detalhes de um filme específico a partir do seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Filme encontrado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MovieResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Filme não encontrado para o ID informado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> detalharFilme(
            @Parameter(description = "Identificador único do filme", example = "1")
            @PathVariable Long id) {
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
    @Operation(summary = "Atualizar descrição do filme", description = "Atualiza o campo 'openingCrawl' de um filme em memória e incrementa automaticamente o campo 'version'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Descrição atualizada e versão incrementada com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = MovieResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (ex.: openingCrawl em branco ou nulo)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Filme não encontrado para o ID informado",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/{id}/description")
    public ResponseEntity<MovieResponseDTO> atualizarDescricao(
            @Parameter(description = "Identificador único do filme", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateDescriptionDTO dto) {
        Movie movieAtualizado = movieService.atualizarDescricao(id, dto.getOpeningCrawl());
        return ResponseEntity.ok(MovieResponseDTO.from(movieAtualizado));
    }
}
