package com.riachuelo.starwars.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DTO que mapeia os dados de um filme retornados pela SWAPI.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapiFilmDTO {

    private String title;

    @JsonProperty("episode_id")
    private Integer episodeId;

    @JsonProperty("opening_crawl")
    private String openingCrawl;

    private String director;
    private String producer;

    @JsonProperty("release_date")
    private String releaseDate;

    private String url;

    public SwapiFilmDTO() {
    }

    public SwapiFilmDTO(String title, Integer episodeId, String openingCrawl,
                        String director, String producer, String releaseDate, String url) {
        this.title = title;
        this.episodeId = episodeId;
        this.openingCrawl = openingCrawl;
        this.director = director;
        this.producer = producer;
        this.releaseDate = releaseDate;
        this.url = url;
    }

    /**
     * Extrai o ID do filme a partir da URL da SWAPI (ex: "https://swapi.dev/api/films/1/").
     * Caso não seja possível extrair da URL, utiliza o episodeId como fallback.
     */
    public Long extrairId() {
        if (url != null && !url.isBlank()) {
            Pattern pattern = Pattern.compile(".*/films/(\\d+)/?.*");
            Matcher matcher = pattern.matcher(url);
            if (matcher.matches()) {
                return Long.parseLong(matcher.group(1));
            }
        }
        return episodeId != null ? episodeId.longValue() : null;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

