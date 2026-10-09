package com.riachuelo.starwars.dto;

import com.riachuelo.starwars.model.Movie;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Record Java 17 para resposta com os dados de um filme.
 */
@Schema(description = "Dados detalhados do filme Star Wars com controle de versão.")
public record MovieResponseDTO(
        @Schema(description = "Identificador único do filme", example = "1")
        Long id,

        @Schema(description = "Título do filme", example = "A New Hope")
        String title,

        @Schema(description = "Número do episódio da saga Star Wars", example = "4")
        Integer episodeId,

        @Schema(description = "Texto da navegação inicial de abertura (opening crawl)", example = "It is a period of civil war...")
        String openingCrawl,

        @Schema(description = "Diretor do filme", example = "George Lucas")
        String director,

        @Schema(description = "Produtor(es) do filme", example = "Gary Kurtz, Rick McCallum")
        String producer,

        @Schema(description = "Data de lançamento nos cinemas", example = "1977-05-25")
        String releaseDate,

        @Schema(description = "Versão incremental da entidade filme (inicia em 1)", example = "1")
        Long version
) {
    public static MovieResponseDTO from(Movie movie) {
        if (movie == null) {
            return null;
        }
        return new MovieResponseDTO(
                movie.getId(),
                movie.getTitle(),
                movie.getEpisodeId(),
                movie.getOpeningCrawl(),
                movie.getDirector(),
                movie.getProducer(),
                movie.getReleaseDate(),
                movie.getVersion()
        );
    }
}
