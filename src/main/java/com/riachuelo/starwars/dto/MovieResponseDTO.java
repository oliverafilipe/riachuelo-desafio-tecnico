package com.riachuelo.starwars.dto;

import com.riachuelo.starwars.model.Movie;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para resposta com os dados de um filme.
 */
@Schema(description = "Dados detalhados do filme Star Wars com controle de versão.")
public class MovieResponseDTO {

    @Schema(description = "Identificador único do filme", example = "1")
    private Long id;

    @Schema(description = "Título do filme", example = "A New Hope")
    private String title;

    @Schema(description = "Número do episódio da saga Star Wars", example = "4")
    private Integer episodeId;

    @Schema(description = "Texto da navegação inicial de abertura (opening crawl)", example = "It is a period of civil war...")
    private String openingCrawl;

    @Schema(description = "Diretor do filme", example = "George Lucas")
    private String director;

    @Schema(description = "Produtor(es) do filme", example = "Gary Kurtz, Rick McCallum")
    private String producer;

    @Schema(description = "Data de lançamento nos cinemas", example = "1977-05-25")
    private String releaseDate;

    @Schema(description = "Versão incremental da entidade filme (inicia em 1)", example = "1")
    private Long version;

    public MovieResponseDTO() {
    }

    public MovieResponseDTO(Long id, String title, Integer episodeId, String openingCrawl,
                            String director, String producer, String releaseDate, Long version) {
        this.id = id;
        this.title = title;
        this.episodeId = episodeId;
        this.openingCrawl = openingCrawl;
        this.director = director;
        this.producer = producer;
        this.releaseDate = releaseDate;
        this.version = version;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Integer episodeId) {
        this.episodeId = episodeId;
    }

    public String getOpeningCrawl() {
        return openingCrawl;
    }

    public void setOpeningCrawl(String openingCrawl) {
        this.openingCrawl = openingCrawl;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
