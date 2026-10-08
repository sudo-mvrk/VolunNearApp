# TODO

Status legend: `[ ]` not started, `[~]` in progress, `[x]` done.
Rule: one slice per session. Run `./mvnw clean verify` and commit before ticking.

Slice order below is the one proposed in `docs/PLAN.md` and is not final until Phase A is approved.
Slice ids are unchanged; only the order changed (6a and 6b moved forward, 5b added).

## Next session: start here
State at the end of 2026-10-07. Remove this section once Slice 0 is under way.

Read first, in this order: `docs/CLAUDE.md` (rules; not auto-loaded while it is in `docs/`), `docs/ANALYSIS.md` section 6 (all decisions), `docs/PLAN.md` (slices with acceptance criteria and tests), `docs/FUNCTION.md` (scoring spec).

Where things stand:
- Phase A is finished except for one step: the developer has not yet approved `docs/PLAN.md`. `CLAUDE.md` requires approval before work starts, so ask for it first.
- All questions Q-A … Q-H and Q1 … Q20 are answered. Nothing is blocked by a question.
- No feature code was written or changed in the session of 2026-10-07; only the four files in `docs/` were edited.
- `./mvnw clean verify` was not run in that session. `ANALYSIS.md` section 1 records 8 failing auth tests; treat that as unverified until run again.

`FUNCTION.md` was changed on 2026-10-07 and differs from the earlier thesis text in two places. The developer should review both:
- 1.2: `u_skill` is the cosine projected onto the required skills (Q10); edge cases for zero vectors are written in (Q9).
- 1.3: `P_min = 1`, `P_max = 4` are fixed scale bounds, not pool bounds (Q8). The symbol `T` was removed from the notation table.

Working tree, before anything else:
- The branch `mvp-reboot` has a large uncommitted working tree (activity code, moved services, tests). Slice 0 starts by committing it; ask the developer before committing.
- `docs/ANALYSIS.md`, `docs/PLAN.md` and `docs/FUNCTION.md` exist only on disk: the unstaged `docs/` line in `.gitignore` hides them from git. `docs/CLAUDE.md` and `docs/TODO.md` are staged from before. Fixing `.gitignore` and adding the three files is the first step of Slice 0 (Q-H).

Next step after approval: Slice 0 as described in `docs/PLAN.md`, then 6a and 6b. One slice per session.

## Phase A: Analysis and plan (no feature code yet)
- [x] Read `docs/FUNCTION.md` and any existing code or docs in the repo
- [x] Write `docs/ANALYSIS.md`: entities, relations, scoring inputs, open questions
- [x] Write `docs/PLAN.md`: confirmed slice order, risks, decisions needed from the developer
- [x] Developer answers Q-A … Q-H and Q1 … Q20; recorded in `docs/ANALYSIS.md` section 6
- [x] Developer reviews and approves the plan (approved 2026-10-08)

## Phase B: Baseline and scoring core
- [~] Slice 0: Stabilise the existing project: commit current work, package by feature, Flyway baseline, Testcontainers instead of H2, fix the 8 failing auth tests, health endpoint, remove dead files and unused dependencies
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
- [ ] Fix `lat == 0` treated as "no location" in the three `toPoint` mappers (Slice 2)
- [ ] `.gitignore`: remove the unstaged `docs/` line; add `redisdata/` (Slice 0)
- [ ] Move `CLAUDE.md` from `docs/` to the repo root so Claude Code loads it (Slice 0)
- [ ] `CLAUDE.md`: compose service name is `postgres`, not `db` (Java version, sessions and capacity wording are updated)
- [ ] Per-activity weight override on the activity entity and DTOs (Slice 4), used only for activity → volunteers (Slice 6c)
- [ ] PostGIS radius prefilter for volunteer → activities (Slice 6c)

## Open questions
None. All answers are in `docs/ANALYSIS.md` section 6.
