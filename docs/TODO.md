# TODO

Status legend: `[ ]` not started, `[~]` in progress, `[x]` done.
Rule: finish one slice before starting the next. Run `./mvnw clean verify` and commit before ticking.

Slice order is the one in `docs/PLAN.md`, approved on 2026-10-08: `0 → 6a → 6b → 1 → 2 → 3 → 4 → 5 → 6c → 5b → 6d → 7 → 8 → 9`.
Rules are in `CLAUDE.md` (repo root), decisions in `docs/ANALYSIS.md` section 6, the scoring spec in `docs/FUNCTION.md`.

## Next session: start here
State at the end of 2026-10-08. Rewrite this section at the end of every session.

Read first: `CLAUDE.md` (loaded automatically), this file, then for the slice at hand its entry in `docs/PLAN.md` and the decisions in `docs/ANALYSIS.md` section 6. `docs/FUNCTION.md` only when touching scoring.

Where things stand:
- Done: Slice 0 (baseline), 6a (pure scoring), 6b (feasibility and ranking). `./mvnw clean verify` is green with 134 tests.
- Next: **Slice 1, auth hardening.** Then 2, 3, 4, 5, 6c, 5b, (6d), 7, 8, 9, evaluation.
- Git: branch `mvp-reboot`. Everything up to 6a is pushed; the 6b commit and later doc commits may be local only, check with `git status -sb`. Push only when the developer says so.
- `docs/` is tracked and pushed on purpose (decided 2026-10-08).
- Local services: `docker compose up -d postgres redis`. The dev database is fresh and migrated by Flyway (V1). Redis requires `REDIS_PASSWORD`.

How the developer works:
- Says "lets move to slice X" to start a slice; the plan is approved, so no new plan is needed for a slice that follows `docs/PLAN.md`.
- Edge cases the spec does not settle are asked before coding, with a recommendation (see Q13a, Q13b, Q14a). Small implementation choices are made and reported.
- Commits follow the existing style: `feat/ ...`, `ref/ ...`, `docs/ ...`, one logical step per commit.

What exists in code, so it is not rebuilt:
- Packages under `com.volunnear`: `auth`, `organization`, `volunteer`, `dictionary` (Skill), `activity` (only the `Priority` and `ActivityStatus` enums), `application` (only `ApplicationStatus`), `common` (`config`, `exception`, `validation`), `distribution` (`scoring`, `model`, `feasibility`, `ranking`).
- `distribution` is finished pure code with no Spring or JPA and is not wired to anything yet. Slice 6c adds the entity adapter, configuration properties and endpoints; it must not change the scoring or feasibility rules.
- Schema: `V1__baseline.sql` only (`app_users`, `user_role`, `volunteers`, `organizations`, `skills`). There are no activity or application tables or entities; Slices 4 and 5 create them. Every schema change is a new migration.
- Tests: integration tests extend `support.AbstractIntegrationTest` (PostGIS in Testcontainers, in-memory sessions, `MockMvc`) and send `.with(csrf())` on every modifying request. Distribution tests build data with `distribution.Profiles`.

Known facts for Slice 1 (found while reading the code, none fixed yet):
- Banned users can log in: `AppUser.isBanned` exists but `CustomUserDetails.isAccountNonLocked()` always returns true.
- `OrganizationService` and `VolunteerService` throw `UsernameNotFoundException`, which has no handler, so the client gets 500.
- `GlobalExceptionHandler` handles `java.nio.file.AccessDeniedException`, not Spring Security's `AccessDeniedException`; the handler never fires.
- `SecurityConfig`: `/api/v1/volunteers/**` requires role VOLUNTEER, but `PUT /api/v1/organizations/me` only requires authentication, so a volunteer reaches the service and gets the 500 above instead of 403.
- `SecurityConfig` permits `/api/v1/auth/csrf`, but no controller serves it. A real client has no clean way to get its first CSRF token.
- In `AuthControllerIntegrationTest` the fixture user `testVol` is created with `ROLE_ORGANIZATION`.
- Already done ahead of Slice 1: registration returns 201; invalid login returns 401 and is tested.

## Phase A: Analysis and plan (no feature code yet)
- [x] Read `docs/FUNCTION.md` and any existing code or docs in the repo
- [x] Write `docs/ANALYSIS.md`: entities, relations, scoring inputs, open questions
- [x] Write `docs/PLAN.md`: confirmed slice order, risks, decisions needed from the developer
- [x] Developer answers Q-A … Q-H and Q1 … Q20; recorded in `docs/ANALYSIS.md` section 6
- [x] Developer reviews and approves the plan (approved 2026-10-08)

## Phase B: Baseline and scoring core
- [x] Slice 0: Stabilise the existing project: commit current work, package by feature, Flyway baseline, Testcontainers instead of H2, fix the 8 failing auth tests, health endpoint, remove dead files and unused dependencies
- [x] Slice 6a: Pure scoring code from `FUNCTION.md` with hand-computed unit tests, including weight validation
- [x] Slice 6b: Feasibility filter (availability, certification, capacity) and ranking with tie-break, with tests

## Phase C: Platform
- [ ] Slice 1: Auth hardening (register and login exist; role rules, banned users, error mapping, tests)
- [ ] Slice 2: Organizations (profile exists; read endpoints, ownership tests; own id instead of `@MapsId`, `organization_member` with one `OWNER` row, single access-check method)
- [ ] Slice 3: Volunteer profile (location, skills, certifications, availability, capacity)
- [ ] Slice 4: Activities, rebuilt (location, time, required skills and certs, priority, spots needed)
- [ ] Slice 5: Applications (apply, accept, reject, withdraw; spot and capacity checks in one transaction; concurrency test)

## Phase D: Smart Distribution integration
- [ ] Slice 6c: Entity to domain adapter, weights alpha, beta, gamma, lambda in `application.yml` validated at startup, recommendation endpoints (activity -> volunteers, volunteer -> activities) with score breakdown
- [ ] Slice 5b: Invitations (organization invites from the recommendation list; volunteer accepts or declines)
- [ ] Slice 6d: Optional batch auto-assign (greedy first), organization reviews result

## Phase E: Finish
- [ ] Slice 7: Email notifications (log stub first)
- [ ] Slice 8: Completion and volunteer history
- [ ] Slice 9: Seed data and five-minute demo script
- [ ] Evaluation: compare recommendations against a simple baseline on seed data (for the thesis)

## Discovered work
- [ ] Fix `lat == 0` treated as "no location" in the two `toPoint` mappers, volunteer and organization (Slice 2)
- [x] `.gitignore`: `docs/` line is gone; `redisdata/` added (Slice 0)
- [x] Move `CLAUDE.md` from `docs/` to the repo root so Claude Code loads it (Slice 0)
- [x] `CLAUDE.md`: compose service name is `postgres`, not `db` (Slice 0)
- [x] Local dev database recreated on 2026-10-08; the app starts against it, Flyway applies V1 and `/actuator/health` returns 200 (Slice 0)
- [ ] `testcontainers.version` is pinned to 1.21.4 in `pom.xml` because the version managed by Boot 3.3.3 cannot talk to Docker 29; drop the pin when Spring Boot is upgraded
- [ ] Registration and logout need a CSRF token: the client must call `GET /api/v1/auth/csrf` first, but no such endpoint exists (only the security rule). Decide in Slice 1
- [ ] Unused leftovers kept for now: `TokenRefreshException`, `AuthErrorException`, `BadUserCredentialsException`, `ValidPhoneNumber`, `spring-boot-starter-mail`, the `logging.level` block for RestTemplate/apache in `application.yml` (Slice 1 or 7)
- [x] `docker-compose.yml` uses `.env` for every service: required-variable checks, Redis really requires `REDIS_PASSWORD`, the app container gets `.env` via `env_file`; `.env.example` and `.dockerignore` added (2026-10-08)
- [ ] `Dockerfile` copies `application.yml` as `application-dev.yml` and starts with profile `dev`; it works but the copy is redundant, the jar already contains the file (Slice 9)
- [ ] `ApplicationStatus` mixes `REJECT` and `CANCELED` naming (Slice 5)
- [x] `VolunteerProfile` / `ActivityProfile` records exist in `distribution.model` (Slice 6b)
- [ ] `Ranker` takes one `ScoringWeights` per call: the caller passes the activity's override for activity → volunteers and the global weights for volunteer → activities (Slice 6c)
- [ ] Adapter contract for `VolunteerProfile` (Slice 6c): `activeAssignments` = windows of accepted applications on activities that are not completed or cancelled (its size is the used capacity, Q16); `appliedActivityIds` = every activity with an application or invitation from this volunteer; times converted to the one configured zone
- [ ] A slot cannot end at 24:00 (`LocalTime` stops at 23:59) and an activity ending at midnight counts as multi-day, so it is never feasible. Validation in Slices 3 and 4 must reject or document this
- [ ] The adapter must map the `Priority` enum as it is; the numeric scale 1..4 lives only in `ScoringFunction.priorityValue` (Slice 6c)
- [ ] Per-activity weight override on the activity entity and DTOs (Slice 4), used only for activity → volunteers (Slice 6c)
- [ ] PostGIS radius prefilter for volunteer → activities (Slice 6c)

## Open questions
None. All answers are in `docs/ANALYSIS.md` section 6.
