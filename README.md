# Estapar Parking System (Spring Boot)

Sistema backend para gerenciar um estacionamento (setores, vagas e sessões de estacionamento) integrando com um **simulador** que envia eventos de veículos. Projeto baseado no **Estapar Backend Developer Test (V1.4)**.

## Sumário
- [Visão Geral](#visão-geral)
- [Arquitetura & Tecnologias](#arquitetura--tecnologias)
- [Como Rodar](#como-rodar)
    - [Simulador](#simulador)
    - [Aplicação (Spring Boot)](#aplicação-spring-boot)
- [Configuração](#configuração)
- [Migrações (Flyway)](#migrações-flyway)
- [Swagger / OpenAPI](#swagger--openapi)
- [Testes](#testes)
- [Checklist do Desafio](#checklist-do-desafio)

---

## Visão Geral

- Na inicialização, a aplicação consome `GET /garage` do simulador e **sincroniza setores e vagas** no banco.
- O simulador envia **eventos de veículos** para `POST /webhook`:
    - `ENTRY`: abre sessão e reserva uma vaga de um **setor aberto** com **menor ocupação**.
    - `PARKED`: associa lat/lng a uma vaga livre e marca `occupied = true`.
    - `EXIT`: encerra a sessão, calcula o valor devido e libera a vaga.
- A API expõe `GET /revenue` para **receita total por setor e data**.

## Arquitetura & Tecnologias

- **Java 21**, **Spring Boot 3.5**
- **Spring Web**, **Spring Data JPA**
- **MySQL** (persistência), **Flyway** (migrações)
- **springdoc-openapi** (Swagger UI)
- **Maven**

Estrutura sugerida:
```
src.main.java 
  ├─ resources/db/migration  # scripts Flyway
  └─ br.com.estapar.parking
      ├─ controller          # /webhook, /revenue
      ├─ service             # regras de negócio
      ├─ repository          # Spring Data JPA
      ├─ model               # entidades JPA
      ├─ dto                 # DTOs request/response
      └─ config              # configurações (Swagger etc.)
```

## Como Rodar

### Simulador

Suba o simulador (Linux recomendado com `--network="host"`):
```bash
docker run -d --network="host" cfontes0estapar/garage-sim:1.0.0
```

### Aplicação (Spring Boot)

Pré-requisitos:
- JDK 21+
- Maven 3.9+

Build & run:
```bash
mvn clean package
java -jar target/parking-system-0.0.1-SNAPSHOT.jar
# ou
mvn spring-boot:run
```

A aplicação sobe em `http://localhost:3003`.

## Configuração

Arquivo: `src/main/resources/application.yml`
```yaml
server:
  port: 3003
spring:
  application:
    name: parking-system
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: /** URL DO SEU BANCO DE DADOS **/
    username: /** USER DO SEU BANCO DE DADOS **/
    password: /** SENHA DO SEU BANCO DE DADOS **/
  jpa:
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
    show-sql: false
    open-in-view: false
```

**Dependências principais (pom.xml)**: Spring Web, Spring Data JPA, MySQL Connector/J, Flyway, Springdoc OpenAPI, Starter Test (JUnit).

## Migrações (Flyway)

Scripts em `src/main/resources/db/migration`, por exemplo:
```
V1__create_tables.sql
```

O Flyway executa as migrações automaticamente.

## Swagger / OpenAPI

- Swagger UI: `http://localhost:3003/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:3003/v3/api-docs`

## Testes

- **Unitários** com `spring-boot-starter-test` (JUnit 5)
- (Opcional) **Testcontainers** para integração com MySQL em Docker

Rodar:
```bash
mvn test
```
## Checklist do Desafio

- [x] Java 21 / Spring Boot 3.5
- [x] MySQL + JPA
- [x] Flyway
- [x] `POST /webhook` (ENTRY, PARKED, EXIT)
- [x] `GET /revenue`
- [x] Sincronização `GET /garage` na inicialização
- [x] Regras: 30 min grátis, ceil/hora, preço dinâmico, lotação
- [x] Swagger/OpenAPI
- [x] Testes básicos