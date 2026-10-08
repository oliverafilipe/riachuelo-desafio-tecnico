# GEMINI.md — Star Wars Movies Backend API

## Visão Geral do Projeto

API REST em **Java 17** com **Spring Boot** que consome a **SWAPI (The Star Wars API)** para gerenciar filmes da saga Star Wars. Ao iniciar, a aplicação carrega os filmes **em memória** e permite listar, detalhar e atualizar a descrição (`openingCrawl`) de cada filme, com **controle de versão incremental** (`version`).

Não há banco de dados: todo o estado vive em memória (`Map<Long, Movie>`).

## Stack Tecnológica

- Java 17+
- Spring Boot (Web, Rest, Actuator)
- RestTemplate / WebClient (integração com a SWAPI)
- Maven (wrapper `./mvnw` disponível)
- JUnit 5 e Mockito (testes unitários e de integração)

## Comandos Essenciais

```bash
# Executar a aplicação (porta 8080)
mvn spring-boot:run

# Executar todos os testes
mvn test

# Build do projeto
mvn clean package
```

## Arquitetura

Arquitetura em camadas, com as seguintes classes principais:

| Classe | Responsabilidade |
| --- | --- |
| `MovieController` | Expõe os endpoints REST; depende de `MovieService`. Converte entidades em `MovieResponseDTO`. |
| `MovieService` | Regras de negócio; mantém `Map<Long, Movie> movieCache`. Métodos: `carregarFilmesEmMemoria()`, `listarFilmes()`, `buscarPorId(Long id)`, `atualizarDescricao(Long id, String novaDescricao)`. |
| `SwapiClient` | Integração com a SWAPI via `RestTemplate`. Método: `buscarFilmesDaApi()` retorna `SwapiResponseDTO`. |
| `Movie` | Entidade de domínio. Campos: `id`, `title`, `episodeId`, `openingCrawl`, `director`, `producer`, `releaseDate`, `version`. Método: `incrementarVersao()`. |

Relações:

- `MovieController` → `MovieService` (injeção de dependência)
- `MovieService` → `SwapiClient` (consome a SWAPI)
- `MovieService` → `Movie` (gerencia em memória)

DTOs envolvidos: `MovieResponseDTO`, `UpdateDescriptionDTO`, `SwapiResponseDTO`.

## Endpoints da API

| Método | Rota | Descrição |
| --- | --- | --- |
| `GET` | `/api/movies` | Lista todos os filmes carregados em memória |
| `GET` | `/api/movies/{id}` | Detalha um filme pelo ID |
| `PUT` | `/api/movies/{id}/description` | Atualiza o `openingCrawl` e incrementa `version` |

Corpo da requisição do `PUT`:

```json
{
  "openingCrawl": "Nova descrição épica para o filme Star Wars..."
}
```

Formato de resposta de um filme:

```json
{
  "id": 1,
  "title": "A New Hope",
  "episodeId": 4,
  "openingCrawl": "...",
  "director": "George Lucas",
  "producer": "Gary Kurtz, Rick McCallum",
  "releaseDate": "1977-05-25",
  "version": 1
}
```

## Regras de Negócio

- Os filmes são carregados da SWAPI **uma única vez**, na inicialização da aplicação (`carregarFilmesEmMemoria()`).
- Apenas o campo `openingCrawl` pode ser alterado via API.
- Cada atualização bem-sucedida **deve** chamar `Movie.incrementarVersao()` (a versão inicial é `1`).
- Alterações são voláteis: reiniciar a aplicação recarrega os dados originais da SWAPI.

## Diretrizes de Código

- Seguir as convenções do Java 17 e do ecossistema Spring Boot.
- Manter a separação de camadas: controllers não contêm regra de negócio; services não conhecem detalhes HTTP.
- Nunca expor a entidade `Movie` diretamente nas respostas; usar sempre DTOs.
- Usar injeção por construtor (evitar `@Autowired` em campos).
- Nomes de classes, métodos de domínio e endpoints seguem o padrão já existente no projeto (métodos em português, como `listarFilmes` e `buscarPorId`).
- Garantir segurança de concorrência no acesso ao `movieCache` (ex.: `ConcurrentHashMap`), pois a atualização de versão pode ocorrer em requisições simultâneas.
- Tratar falhas da SWAPI e filmes não encontrados com respostas HTTP adequadas (por exemplo, `404` para ID inexistente).

## Diretrizes de Testes

- Escrever testes com **JUnit 5** e **Mockito**.
- Testes unitários: mockar `SwapiClient` em `MovieService` e `MovieService` em `MovieController`.
- Cobrir, no mínimo: listagem, busca por ID (existente e inexistente), atualização de descrição e incremento de `version`.
- Executar `mvn test` antes de concluir qualquer alteração.

## Referências

Para detalhes adicionais do projeto (diagrama UML e instruções de execução), consulte:

@./README.md
