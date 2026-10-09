package com.riachuelo.starwars.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração da documentação da API utilizando Swagger / OpenAPI 3.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Star Wars Movies API")
                        .version("1.0.0")
                        .description("API REST desenvolvida em Java 17 com Spring Boot que consome a SWAPI (The Star Wars API) " +
                                "para gerenciar filmes da saga Star Wars. Os dados são mantidos em memória com controle de versão incremental.")
                        .contact(new Contact()
                                .name("Desafio Técnico Riachuelo")
                                .url("https://github.com/seu-usuario/starwars-backend-challenge")));
    }
}

