package com.riachuelo.starwars.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para atualização do campo openingCrawl de um filme.
 */
public class UpdateDescriptionDTO {

    @NotBlank(message = "O campo openingCrawl é obrigatório e não pode ser vazio.")
    private String openingCrawl;

    public UpdateDescriptionDTO() {
    }

    public UpdateDescriptionDTO(String openingCrawl) {
        this.openingCrawl = openingCrawl;
    }

    public String getOpeningCrawl() {
        return openingCrawl;
    }

    public void setOpeningCrawl(String openingCrawl) {
        this.openingCrawl = openingCrawl;
    }
}

