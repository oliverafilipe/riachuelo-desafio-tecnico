package com.riachuelo.starwars.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Payload padronizado para respostas de erro da API.
 */
@Schema(description = "Estrutura padronizada de resposta para erros da API.")
public class ErrorResponseDTO {

    @Schema(description = "Data e hora em que o erro ocorreu", example = "2026-10-09T18:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Schema(description = "Código de status HTTP", example = "404")
    private int status;

    @Schema(description = "Descrição reduzida do status HTTP", example = "Not Found")
    private String error;

    @Schema(description = "Mensagem detalhada do erro", example = "Filme com ID 999 não encontrado.")
    private String message;

    @Schema(description = "Caminho da requisição (URI)", example = "/api/movies/999")
    private String path;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
