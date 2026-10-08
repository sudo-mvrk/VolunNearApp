# VolunNearApp

Volunteer management platform with **Smart Distribution**: it recommends and assigns volunteers to activities using a multi-attribute scoring function (distance, skills, priority). The function is specified in `docs/FUNCTION.md`.

This branch (`mvp-reboot`) is the MVP rewrite of the bachelor project. It is API first; there is no frontend.

## Stack

Java 21, Spring Boot 3.3, Maven wrapper, PostgreSQL with PostGIS, Flyway, Redis (HTTP sessions), Testcontainers.

## Run locally

1. Create `.env` in the repo root (it is not committed):

   | Name | Value |
   |--|--|
   | `POSTGRE_DB` | database name used by `docker-compose.yml` |
   | `DB_USERNAME` | database user |
   | `DB_PASSWORD` | database password |
   | `DB_URL` | JDBC url, for example `jdbc:postgresql://localhost:5432/<POSTGRE_DB>` |
   | `REDIS_PASSWORD` | Redis password |

2. Start PostgreSQL and Redis: `docker compose up -d postgres redis`
3. Start the application with the same variables in its environment: `./mvnw spring-boot:run`

Flyway creates the schema on startup (`src/main/resources/db/migration`). Hibernate only validates it.

- Health: http://localhost:8080/actuator/health
- API docs: http://localhost:8080/swagger-ui/index.html

Authentication uses a session cookie (`SESSION`) and a CSRF token (`XSRF-TOKEN` cookie, sent back in the `X-XSRF-TOKEN` header on every modifying request).

## Build and test

`./mvnw clean verify`

Integration tests start `postgis/postgis` in Testcontainers, so Docker must be running. They do not need Redis or `.env`.

## Documentation

- `CLAUDE.md`: project rules and conventions
- `docs/FUNCTION.md`: the scoring function
- `docs/ANALYSIS.md`: analysis and decisions
- `docs/PLAN.md`: slices with acceptance criteria
- `docs/TODO.md`: progress
