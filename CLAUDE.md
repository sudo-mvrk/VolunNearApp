# CLAUDE.md

## Project
Volunteer management platform with **Smart Distribution**: it recommends and assigns volunteers to activities using a multi-attribute scoring function. The function is specified in `docs/FUNCTION.md` and is the source of truth for the matching logic.

This is a master's degree project (an update of a bachelor's project) and an MVP. Prefer simple, working, tested code over completeness.

## Stack
- Java 21, Spring Boot 3.x, Maven (use `./mvnw`)
- Spring Web, Spring Security (sessions in Redis, CSRF), Spring Data JPA, Bean Validation
- PostgreSQL (Docker Compose for local), Flyway for all schema changes
- JUnit 5, Mockito, Testcontainers for integration tests
- springdoc-openapi for API docs

## Commands
- Build and test: `./mvnw clean verify` (integration tests start PostgreSQL in Testcontainers, so Docker must be running)
- Run tests only: `./mvnw test`
- Start local DB and Redis: `docker compose up -d postgres redis`
- Run app: `set -a; . ./.env; set +a; ./mvnw spring-boot:run` (`.env` is ignored by git; `.env.example` lists the variables)
- Run everything in containers: `./mvnw package -DskipTests && docker compose up -d --build`

(Update this section if the commands change.)

## Architecture and conventions
- Package by feature under the base package `com.volunnear`: `auth`, `organization`, `volunteer`, `activity`, `application`, `distribution`, `notification`. Shared code: `dictionary` (skills, later certifications) and `common` (`config`, `exception`, `validation`). DTOs of a feature live in its `dto` subpackage.
- Integration tests extend `support.AbstractIntegrationTest` (shared PostGIS container, in-memory sessions) and send a CSRF token on every modifying request.
- Layers per feature: `controller` -> `service` -> `repository`. No business logic in controllers.
- Controllers use DTOs (records). Never expose JPA entities in the API.
- Every schema change is a new Flyway migration. Never edit an applied migration.
- Authorization is checked in the service layer: a caller may only modify resources of their own organization or profile. Add a test for each rule.
- Multi-step state changes (accept application, check free spots and volunteer capacity) run in one `@Transactional` method and must be safe against concurrent requests.
- Constructor injection only. No field `@Autowired`.
- Configuration through `application.yml` and `@ConfigurationProperties`, not hard-coded constants.

## Smart Distribution rules
- Implement exactly what `docs/FUNCTION.md` specifies: components `u_geo`, `u_skill`, `u_prio`, weights on the simplex, feasibility filter, argmax.
- The scoring code is pure, with no database access. It works on plain domain records and is mapped from entities in a separate adapter class.
- Every component must stay in [0,1]. Guard edge cases: zero skill vectors, missing coordinates.
- Always return the score breakdown (geo, skill, priority, total), not only the total.
- Unit-test scoring with hand-computed examples before wiring it into endpoints.
- If the function or its edge cases are ambiguous, list the ambiguity and ask. Do not silently choose. Decisions already taken are in `docs/ANALYSIS.md` section 6.

## MVP scope
In scope: accounts and roles, volunteer profiles (location, skills, certifications, availability, capacity), organization profiles, activities (with spots needed), applications and invitations, recommendations, email notifications (stub is fine), completion and history.

Out of scope for now: chat, ratings, payments, mobile app, analytics dashboards, learning weights from data, social features, frontend (API first).

## Working agreement
0. At the start of a session read `docs/TODO.md`: its "Next session: start here" section says where the work stands. Rewrite that section at the end of the session.
1. For any task larger than a small edit, propose a plan first and wait for approval.
2. Work in small vertical slices. One slice = migration + entity + repository + service + controller + tests.
3. Run `./mvnw clean verify` before declaring a slice done. Report failures honestly.
4. Do not add features, dependencies or abstractions beyond the current slice.
5. Keep `docs/TODO.md` up to date: tick completed items and add newly discovered work.
6. Explain non-obvious decisions briefly in your reply. The developer is learning the codebase.
