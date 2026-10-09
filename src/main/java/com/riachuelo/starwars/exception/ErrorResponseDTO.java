package com.riachuelo.starwars.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Record Java 17 para respostas de erro padronizadas da API.
 */
@Schema(description = "Estrutura padronizada de resposta para erros da API.")
public record ErrorResponseDTO(
        @Schema(description = "Data e hora em que o erro ocorreu", example = "2026-10-09T18:00:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,

        @Schema(description = "Código de status HTTP", example = "404")
        int status,

        @Schema(description = "Descrição reduzida do status HTTP", example = "Not Found")
        String error,

        @Schema(description = "Mensagem detalhada do erro", example = "Filme com ID 999 não encontrado.")
        String message,

        @Schema(description = "Caminho da requisição (URI)", example = "/api/movies/999")
        String path
) {
}
