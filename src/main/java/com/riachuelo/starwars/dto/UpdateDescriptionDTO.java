package com.riachuelo.starwars.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Record Java 17 para atualização do campo openingCrawl de um filme.
 */
@Schema(description = "Payload para atualização da descrição (openingCrawl) de um filme.")
public record UpdateDescriptionDTO(
        @Schema(description = "Novo texto do openingCrawl. Não pode ser nulo ou vazio.",
                example = "Nova descrição épica para o filme Star Wars...",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "O campo openingCrawl é obrigatório e não pode ser vazio.")
        String openingCrawl
) {
}
