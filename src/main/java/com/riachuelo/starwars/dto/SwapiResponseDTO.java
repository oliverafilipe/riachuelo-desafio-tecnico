package com.riachuelo.starwars.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que encapsula a resposta paginada/em lista da SWAPI ao consultar filmes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapiResponseDTO {

    private Integer count;
    private List<SwapiFilmDTO> results = new ArrayList<>();

    public SwapiResponseDTO() {
    }

    public SwapiResponseDTO(Integer count, List<SwapiFilmDTO> results) {
        this.count = count;
        this.results = results != null ? results : new ArrayList<>();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<SwapiFilmDTO> getResults() {
        return results;
    }

    public void setResults(List<SwapiFilmDTO> results) {
        this.results = results != null ? results : new ArrayList<>();
    }
}

