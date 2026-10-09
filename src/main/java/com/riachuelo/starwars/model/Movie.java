package com.riachuelo.starwars.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Entidade de domínio representando um filme da saga Star Wars.
 */
public class Movie {

    private Long id;
    private String title;
    private Integer episodeId;
    private volatile String openingCrawl;
    private String director;
    private String producer;
    private String releaseDate;
    private final AtomicLong version;

    public Movie() {
        this.version = new AtomicLong(1L);
    }

    public Movie(Long id, String title, Integer episodeId, String openingCrawl,
                 String director, String producer, String releaseDate, Long version) {
        this.id = id;
        this.title = title;
        this.episodeId = episodeId;
        this.openingCrawl = openingCrawl;
        this.director = director;
        this.producer = producer;
        this.releaseDate = releaseDate;
        this.version = new AtomicLong(version != null ? version : 1L);
    }

    /**
     * Incrementa a versão do filme de forma atômica e thread-safe.
     * A versão inicial é 1.
     */
    public void incrementarVersao() {
        this.version.incrementAndGet();
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
        return version.get();
    }

    public void setVersion(Long version) {
        this.version.set(version != null ? version : 1L);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", episodeId=" + episodeId +
                ", openingCrawl='" + openingCrawl + '\'' +
                ", director='" + director + '\'' +
                ", producer='" + producer + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", version=" + version.get() +
                '}';
    }
}
