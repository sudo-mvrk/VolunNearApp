# TODO

Status legend: `[ ]` not started, `[~]` in progress, `[x]` done.
Rule: one slice per session. Run `./mvnw clean verify` and commit before ticking.

Slice order is the one in `docs/PLAN.md`, approved on 2026-10-08: `0 → 6a → 6b → 1 → 2 → 3 → 4 → 5 → 6c → 5b → 6d → 7 → 8 → 9`.
Rules are in `CLAUDE.md` (repo root), decisions in `docs/ANALYSIS.md` section 6, the scoring spec in `docs/FUNCTION.md`.

## Phase A: Analysis and plan (no feature code yet)
- [x] Read `docs/FUNCTION.md` and any existing code or docs in the repo
- [x] Write `docs/ANALYSIS.md`: entities, relations, scoring inputs, open questions
- [x] Write `docs/PLAN.md`: confirmed slice order, risks, decisions needed from the developer
- [x] Developer answers Q-A … Q-H and Q1 … Q20; recorded in `docs/ANALYSIS.md` section 6
- [x] Developer reviews and approves the plan (approved 2026-10-08)

## Phase B: Baseline and scoring core
- [x] Slice 0: Stabilise the existing project: commit current work, package by feature, Flyway baseline, Testcontainers instead of H2, fix the 8 failing auth tests, health endpoint, remove dead files and unused dependencies
- [ ] Slice 6a: Pure scoring code from `FUNCTION.md` with hand-computed unit tests, including weight validation
- [ ] Slice 6b: Feasibility filter (availability, certification, capacity) and ranking with tie-break, with tests

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
- [ ] Developer: recreate the local dev database. The stopped container `volunnearapp-postgres-1` still has the schema built by `ddl-auto`, and Flyway refuses a non-empty schema without a history table: `docker compose rm -sfv postgres && docker compose up -d postgres`
- [ ] `testcontainers.version` is pinned to 1.21.4 in `pom.xml` because the version managed by Boot 3.3.3 cannot talk to Docker 29; drop the pin when Spring Boot is upgraded
- [ ] Registration and logout need a CSRF token: the client must call `GET /api/v1/auth/csrf` first, but no such endpoint exists (only the security rule). Decide in Slice 1
- [ ] Unused leftovers kept for now: `TokenRefreshException`, `AuthErrorException`, `BadUserCredentialsException`, `ValidPhoneNumber`, `spring-boot-starter-mail`, the `logging.level` block for RestTemplate/apache in `application.yml` (Slice 1 or 7)
- [ ] `Dockerfile` copies `application.yml` as `application-dev.yml` and starts with profile `dev`; check it still makes sense (Slice 9)
- [ ] `ApplicationStatus` mixes `REJECT` and `CANCELED` naming (Slice 5)
- [ ] Per-activity weight override on the activity entity and DTOs (Slice 4), used only for activity → volunteers (Slice 6c)
- [ ] PostGIS radius prefilter for volunteer → activities (Slice 6c)

## Open questions
None. All answers are in `docs/ANALYSIS.md` section 6.
