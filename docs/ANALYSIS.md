# ANALYSIS

Phase A output. Date: 2026-10-07. Branch inspected: `mvp-reboot` (working tree, including uncommitted changes).
The questions raised by this analysis are answered in section 6 (Decisions). Sections 1 to 5 are the original analysis and still refer to them by number.

## 1. Repository state

### 1.1 What is there

The repo is not empty. `mvp-reboot` is a partial rewrite of the bachelor project:

| Area | State |
|---|---|
| Build | Spring Boot 3.3.3, **Java 21**, Maven wrapper. Compiles. |
| Tests | 17 tests, **9 pass, 8 fail**. All 8 failures are in `AuthControllerIntegrationTest` and return 403 instead of 200/201/400. The tests send no CSRF token while `SecurityConfig` enables CSRF, which is the likely cause (not verified further). |
| Auth | **Session based** (Spring Session + Redis), not JWT. History shows a deliberate switch away from JWT (commit `1484f0e`). Register volunteer, register organization, login, logout, `/me`. |
| Profiles | `Volunteer` and `Organization`, each sharing its primary key with `AppUser` (`@MapsId`). Update-profile endpoints for both. |
| Activity | Entity, DTOs, mapper, repository, service and controller exist but **do not work** (see 1.3). Uncommitted. |
| Application | `ActivityApplication` entity only. No repository, service or controller. |
| Skills | `Skill` dictionary entity with `Category` enum. Not linked to volunteers or activities. |
| Schema | `ddl-auto: update`. **No Flyway.** |
| Test DB | H2 in memory. **No Testcontainers.** |
| Geo | PostGIS + `hibernate-spatial`, `Point` (SRID 4326) on volunteer, organization and activity. Unused Nominatim Feign client. |
| Structure | Package by layer (`controller`, `service`, `repository`, ...), base package `com.volunnear`. |
| Docker | `postgis/postgis:16-3.4` (service name `postgres`, not `db`), `redis:latest`, app container. |

Other branches: `master` is the bachelor project (JWT, feedback, social links, subscriptions, radius-only recommendations). `reboot-core` and `archive/legacy-reboot-v1` are intermediate snapshots.

### 1.2 Reusable

- `AppUser`, `Role`, `AppUserRepository`, `CustomUserDetails`, `UserDetailServiceImpl` (login by username or email).
- `AuthService` registration flow and `AuthServiceTest` (8 passing unit tests).
- `Volunteer` and `Organization` entities, their services, mappers and `VolunteerServiceTest`.
- `GlobalExceptionHandler`, `ErrorResponse`, custom exceptions, `StringTrimAdvice`.
- `Skill`, `Category`, `Priority`, `ActivityStatus`, `ApplicationStatus` as a starting point.
- PostGIS Docker setup and `Point` mapping. The current mappers build `Coordinate(lon, lat)`, which is the correct axis order.
- From `master`, as reference only: `JwtTokenProvider` / `JwtTokenFilter` / `RefreshToken` (if we go back to JWT) and `EmailNotificationService` (Slice 7).

### 1.3 Not reusable or broken

Activity slice (all uncommitted):
- `VolunteeringActivityRepository extends JpaRepository<Organization, Long>`: wrong entity type.
- `VolunteeringActivityService`: dependencies are not `final`, so `@RequiredArgsConstructor` injects nothing and every call throws `NullPointerException`. The method prints the entity and returns `null`.
- `VolunteeringActivityController.createActivity` has no `@PostMapping`, so the endpoint does not exist.
- `VolunteeringActivity.id` has no `@GeneratedValue`. `activityStatus` has no `@Enumerated(STRING)`, so it is stored as an ordinal. The field `organizationId` holds an `Organization`, not an id.
- `VolunteeringActivityCreationRequest.lat/lon` are `Long`, so coordinates lose their fraction.
- No schedule, no required skills, no required certifications.

Smaller defects in code we keep:
- All three `toPoint` mappers treat `lat == 0` as "no location" and never check `lon` for null.
- `AddressDTO` puts `@NotBlank` on `Double` fields, which throws at validation time.
- `GlobalExceptionHandler.handleBadRequestsExceptions` is registered for `GeoCodingException` but its parameter type is `BadDataInRequestException`; a `GeoCodingException` cannot be bound to it.
- Services throw `UsernameNotFoundException`, which has no handler, so the client gets 500.
- `ActivityApplication.appliedAt` uses `@UpdateTimestamp`, so it changes on every status update. `ApplicationStatus` mixes `REJECT` and `CANCELED` naming.
- `VolunNearApplicationTests` (context load) is commented out.

Dead or out of scope:
- `roles.sql` is MySQL syntax from the bachelor project and is still copied by the `Dockerfile`.
- `pom.xml.versionsBackup`, outdated `README.md`.
- Dependencies with no use in the MVP: `spring-boot-starter-websocket` (chat is out of scope), `spring-cloud-starter-openfeign` + `NominatimClient` (geocoding is not in scope), `spring-boot-starter-aop`.
- Everything on `master` around feedback, social links and subscriptions.

### 1.4 Conflicts between `CLAUDE.md` and the repo

| # | `CLAUDE.md` says | Repo has |
|---|---|---|
| C1 | Java 17 | Java 21 (pom, Dockerfile, installed JDK) |
| C2 | Spring Security with JWT | Sessions in Redis with CSRF cookie |
| C3 | Flyway for all schema changes | `ddl-auto: update`, no migrations |
| C4 | Testcontainers | H2 |
| C5 | Package by feature | Package by layer |
| C6 | `docker compose up -d db` | Service is named `postgres` |
| C7 | Organizations **and membership** | One `AppUser` account is one organization |
| C8 | Volunteer "capacity" | `capacity` is a field on the activity (number of spots) |

Housekeeping:
- The spec file is `docs/FUNCTION.md`; `CLAUDE.md` and `TODO.md` call it `Function.md`. Linux is case sensitive.
- `.gitignore` has an unstaged line `docs/`, while `docs/*.md` are staged. New files in `docs/` (including this one) are ignored by git until that line is removed or they are added with `git add -f`.
- `docs/CLAUDE.md` is not loaded automatically by Claude Code. Only `./CLAUDE.md` or `.claude/CLAUDE.md` are.
- `redisdata/` is untracked and owned by uid 999. It should be in `.gitignore`.
- The working tree has 17 modified files. They should be committed or stashed before Slice 0.

## 2. Scoring function: inputs and sources

Notation in `FUNCTION.md`: `v` volunteer, `t` task (an activity).

Formatting note: `\exp(-\lambda, d(v,t))` and `\alpha, u_{geo}` contain a comma where LaTeX `\,` (thin space) lost its backslash. I read them as products: `exp(-λ · d)` and `α · u_geo`. The set braces lost their backslashes the same way. Please confirm (Q1).

| Input | Meaning | Supplied by | Exists today |
|---|---|---|---|
| `d(v,t)` | distance volunteer to activity | `Volunteer.location`, `VolunteeringActivity.location` (`Point`, SRID 4326) | Yes. Volunteer location is nullable. |
| `λ` | decay rate, unit 1/distance | configuration | No |
| `S_v` | volunteer skill vector | new `volunteer_skill` (volunteer, skill[, level]) over the `Skill` dictionary | Dictionary only |
| `S_t` | activity skill vector | new `activity_required_skill` (activity, skill[, weight]) | No |
| `P(t)` | numeric priority | `VolunteeringActivity.priority` (`Priority` enum, nullable) | Yes, as enum |
| `P_min`, `P_max` | normalisation bounds | constants of the scale, or min/max of a set of activities (Q8) | No |
| `α, β, γ` | weights | configuration | No |
| `Available(v,t)` | time fit | new volunteer availability + new activity start/end | No (entity has `// TODO: Implement Schedule`) |
| `Certified(v,t)` | certificate fit | new `certification` dictionary, `volunteer_certification`, `activity_required_certification` | No |
| `Cap(v)` | remaining volunteer capacity | new field on `Volunteer` and/or count of accepted applications | No |

Implied by the platform but absent from `FUNCTION.md`: activity is `OPEN`, activity has free spots, volunteer has no existing application for it, user is not banned (Q15).

## 3. Component analysis

### 3.1 `u_geo = exp(-λ · d)`

- **Wording against formula.** A logistic function is `1 / (1 + e^{k(d - d0)})`: flat near zero, a drop around `d0`, two parameters. The formula given is exponential decay: steepest at zero, one parameter. They are different curves. The formula is unambiguous and the word "logistic" is not, so I would implement the exponential and fix the wording (possibly "logistics cost" in the sense of travel was meant). Needs your confirmation (Q2).
- **Range.** `d ≥ 0` and `λ > 0` give `(0, 1]`, with 1 at `d = 0`. It never reaches 0. `λ = 0` gives a constant 1 and `λ < 0` leaves the range, so validation must require `λ > 0`.
- **Distance metric and unit.** Not specified. Great-circle (haversine) in kilometres is the natural choice and keeps the scoring code free of the database. `λ` is meaningless without the unit (Q3).
- **Choosing `λ`.** It is easier to reason about as a half-distance: `λ = ln 2 / d_half`. With `λ = 0.1 /km`, the score is 0.61 at 5 km, 0.37 at 10 km, 0.05 at 30 km (Q4).
- **Missing coordinates.** Volunteer location is nullable. Options: volunteer is infeasible, or `u_geo = 0`. `CLAUDE.md` requires a guard but not which one (Q5).
- **`Volunteer.radius`** exists in the entity and is not in the function. It could be a hard filter, or ignored (Q6).
- **Mapper defect.** `lat == 0` must stop meaning "no location" before distances are trusted.

### 3.2 `u_skill = (S_v · S_t) / (‖S_v‖ ‖S_t‖)`

- **Vector definition.** Not specified. Binary (has skill / needs skill) or weighted (proficiency level, importance) (Q7). The `[0,1]` range only holds if all components are non-negative.
- **Zero vectors.** A volunteer with no skills or an activity with no required skills gives `0/0`. The two cases are not symmetric in meaning: "activity needs nothing" suggests everyone fits (1), "volunteer has nothing" suggests 0 (Q9).
- **Cosine penalises extra skills.** With binary vectors, activity needs `{first aid}`:
  - volunteer `{first aid}` scores 1.0
  - volunteer `{first aid, driving, cooking, IT}` scores `1 / (1 · 2) = 0.5`

  The more qualified volunteer scores lower. This follows directly from the formula; it is not an implementation choice. A coverage measure `|S_v ∩ S_t| / |S_t|` would not behave this way, but it is a different function from the one in `FUNCTION.md`. You should decide this knowingly because it affects the thesis results (Q10).
- **Rounding.** Floating point can produce `1.0000000002`. Clamp to `[0,1]`.

### 3.3 `u_prio = (P(t) - P_min) / (P_max - P_min)`

- **Numeric mapping.** `Priority` is an enum. Assume `LOW=1 … URGENT=4`, equally spaced (Q8).
- **What `P_min`/`P_max` range over.** Two readings:
  1. Bounds of the scale (1 and 4). Constant, never divides by zero, and `R` stays comparable across requests.
  2. Min and max over the activities being compared. Then one activity alone, or several of equal priority, gives `0/0`, and the same activity gets different scores in different result sets. That contradicts the property "comparable across tasks" stated in `FUNCTION.md`.

  `CLAUDE.md` asks for an "equal min/max priority" guard, which suggests reading 2 was in mind (Q8).
- **`u_prio` does not depend on `v`.** In `argmax over v` for a fixed `t`, the term `γ · u_prio(t)` is the same for every volunteer and cannot change the ranking. It only shifts the totals. Priority influences results only when activities are ranked for a volunteer, or in batch assignment across activities. With `γ > 0`, the activity→volunteers ranking is effectively decided by `α` and `β` alone. Worth stating in the thesis (Q11).
- **Null priority.** The column is nullable today (Q8).

### 3.4 Weights

- `α, β, γ ≥ 0` and `α + β + γ = 1`. Check the sum with a tolerance (for example `1e-9`), since `0.4 + 0.4 + 0.2` is not exactly 1 in binary floating point.
- Scope: one global set, or per organization (Q12). `TODO.md` implies global in `application.yml`.

### 3.5 Feasibility filter

- **`Available(v,t)`.** Needs a time model on both sides. Open: is an activity a single time window; is volunteer availability a weekly pattern (day + time range) or concrete date ranges; must availability cover the whole activity or only overlap it; which time zone; does an already accepted overlapping activity make the volunteer unavailable (Q13).
- **`Certified(v,t)`.** Reading: every certification the activity requires is held by the volunteer. No required certifications means certified. Open: do certifications expire, and are they verified by anyone or self-declared (Q14).
- **`Cap(v) > 0`.** Not defined. Candidates: maximum number of simultaneous active assignments, or hours per week. Also open whether it is a stored counter that is decremented, or derived as `max − count(accepted, not finished)`. `CLAUDE.md` says "decrement spots and capacity", which implies stored counters (Q16).

### 3.6 Argmax

- **Single winner against several spots.** `v*` is one volunteer. An activity with `k` spots needs the top `k`. Recommendation endpoints should return a ranked list (Q17).
- **Ties.** No rule given. A deterministic tie-break is needed for reproducible tests and thesis results (Q17).
- **Empty feasible set.** Return an empty list, not an error.
- **Reverse direction.** The function defines only `argmax over v`. "Activities for a volunteer" ranks `t` by the same `R(v,t)` with the same feasibility predicate; that is an extension, and it is the direction where `u_prio` matters.
- **Batch assignment (6d).** Applying the argmax per activity in sequence is greedy and not globally optimal; the result depends on the order in which activities are processed (Q18).

### 3.7 Claimed properties

| Claim | Holds? |
|---|---|
| `R ∈ [0,1]` | Yes, given non-negative skill vectors, `λ > 0`, guarded zero cases and valid weights. |
| Comparable across tasks | Only with fixed priority bounds (reading 1 in 3.3). |
| Convex combination | Yes, if weights are validated. |
| Dimensionless attributes | Yes, provided `λ` and `d` use the same unit. |
| Well-defined argmax | Only with a tie-break rule and a defined result for the empty set. |

## 4. Domain entities and relations (proposed)

Names in *italics* are new. Items marked (Q) depend on an open question.

```
AppUser 1──1 Volunteer            (shared PK, exists)
AppUser 1──1 Organization         (shared PK, exists)

Volunteer    N──M Skill           via volunteer_skill [level (Q7)]
Volunteer    N──M Certification   via volunteer_certification [valid_until (Q14)]
Volunteer    1──N AvailabilitySlot  (Q13)

Organization 1──N Activity
Activity     N──M Skill           via activity_required_skill [weight (Q7)]
Activity     N──M Certification   via activity_required_certification

Volunteer 1──N ActivityApplication N──1 Activity
```

| Entity | Fields | Notes |
|---|---|---|
| `AppUser` | id, username, email, password, banned, createdAt, roles | exists |
| `Volunteer` | id, firstName, lastName, dateOfBirth, location, locationName, countryCode, city, region, radius (Q6), *maxActiveAssignments* (Q16) | exists + 1 field |
| `Organization` | id, organizationName, description, websiteUrl, location, city, fullAddress, countryCode | exists |
| `Skill` | id, skillName, category | exists |
| *`Certification`* | id, name | dictionary |
| *`AvailabilitySlot`* | id, volunteer, dayOfWeek, startTime, endTime | shape depends on Q13 |
| `Activity` (`VolunteeringActivity`) | id, organization, title, shortDescription, fullDescription, priority, location, *startsAt*, *endsAt*, *spotsNeeded* (rename of `capacity`, see C8), status, updatedAt | rebuild |
| `ActivityApplication` | id, volunteer, activity, status, appliedAt, *decidedAt*, *origin* (APPLICATION or INVITATION) | unique (volunteer, activity) |

Not modelled as entities in the MVP: notifications (log stub), history (accepted applications on `COMPLETED` activities).

Pure domain records for scoring (no JPA, no Spring):

```
GeoPoint(lat, lon)
VolunteerProfile(id, GeoPoint location?, Map<skillId, weight> skills,
                 Set<certId> certifications, availability, remainingCapacity)
ActivityProfile(id, GeoPoint location, Map<skillId, weight> requiredSkills,
                Set<certId> requiredCertifications, start, end, priority)
ScoringWeights(alpha, beta, gamma, lambda)
ScoreBreakdown(geo, skill, priority, total)
```

## 5. Data needed per component

| Component | From volunteer | From activity | From config |
|---|---|---|---|
| `u_geo` | location (lat, lon) | location (lat, lon) | `λ`, distance unit |
| `u_skill` | skill ids [+ level] | required skill ids [+ weight] | zero-vector rule |
| `u_prio` | none | priority | numeric mapping, `P_min`, `P_max` |
| Available | availability slots, accepted activities' time windows | startsAt, endsAt | time zone |
| Certified | certification ids [+ valid_until] | required certification ids | none |
| Cap | maxActiveAssignments, count of active accepted applications | none | none |
| Platform filter | banned flag, existing applications | status, free spots | none |
| Aggregate | | | `α`, `β`, `γ` |

## 6. Decisions

All questions were answered by the developer on 2026-10-07. "Changed" marks an answer that differs from the original recommendation. Where sections 1 to 5 disagree with this section, this section wins.

### Project setup (Slice 0)

- **Q-A. Evolve `mvp-reboot`.** No fresh skeleton.
- **Q-B. Java 21.**
- **Q-C. Keep sessions.** No JWT; Redis and CSRF handling stay.
- **Q-D. Package by feature now**, as one behaviour-free refactor commit in Slice 0, base package `com.volunnear`.
- **Q-E. Membership table now (changed).** One account per organization in the MVP, but the basis for several accounts is built in from the start so no key migration is needed later:
  - `Organization` gets its own generated id; the `@MapsId` link to `AppUser` is removed.
  - `organization_member (organization_id, user_id, role)` exists from the start, with exactly one `OWNER` row per organization created at registration.
  - Every ownership check goes through one method (for example `OrganizationAccess.canManage(userId, organizationId)`) that reads the member table.
  - No invite or role-management endpoints in the MVP.
- **Q-F. Keep PostGIS `Point`.** Scoring distance is computed in pure Java; PostGIS does the radius prefilter (Q6).
- **Q-G. Remove all three unused dependencies:** websocket, aop, openfeign + `NominatimClient`.
- **Q-H. Track `docs/` in git and move `CLAUDE.md` to the repo root.** Remove the `docs/` line from `.gitignore`, add `redisdata/`.

### Scoring (Slice 6a)

- **Q1. Commas are multiplication.** `FUNCTION.md` now writes them as `\,`.
- **Q2. `u_geo` is exponential decay.** `FUNCTION.md` no longer says "logistic".
- **Q3. Distance is haversine, in km.**
- **Q4. `λ` is configured as a half-distance, default 10 km** (`λ = ln 2 / 10 ≈ 0.0693 /km`).
- **Q5. Volunteer without location: `u_geo = 0`, still listed.**
- **Q6. `Volunteer.radius` is not used in scoring (refined).** It is a PostGIS prefilter (`ST_DWithin`) in the volunteer→activities direction only: open activities within the volunteer's radius are selected, then scored. In the activity→volunteers direction the radius is not applied, so an organization can be shown a volunteer whose own radius would not include the activity. The `d_max` variant in `FUNCTION.md` 1.1 is not used.
- **Q7. Binary skill vectors.** The scoring record holds doubles so levels can be added later without changing the function.
- **Q8. Priority (revised the same day).** Mapping `LOW=1 … URGENT=4`; priority is `NOT NULL` with default `MEDIUM`. `P_min = 1` and `P_max = 4` are the fixed bounds of the scale, giving `0, 1/3, 2/3, 1`. `FUNCTION.md` 1.3 is updated. The first answer was pool-based bounds with `u_prio = 1` for equal priorities; it was dropped because the spec did not define the pool, the score of an activity would change as other activities open or close, and thesis results would not be reproducible.
- **Q9. Zero skill vectors.** Activity requires no skills → `u_skill = 1`. Volunteer has none of the required skills → `u_skill = 0`.
- **Q10. Cosine is projected onto the required skills (changed; the function itself changes).** `u_skill` is the cosine between `S_t` and `S_v` restricted to the dimensions where `S_t > 0`. Extra unrelated skills no longer lower the score; only missing skills do. With binary vectors this equals `sqrt(k / n)`, where `n` is the number of required skills and `k` the number of them the volunteer has. `FUNCTION.md` 1.2 is updated. The example in 3.2 now gives 1 for both volunteers.
- **Q11. `u_prio` stays in the formula in both directions.** It cannot change the ranking of volunteers for one activity; this is stated in the thesis, and the breakdown in the API shows it.
- **Q12. Weights (changed).**
  - Global default in `application.yml`: `α = 0.4`, `β = 0.4`, `γ = 0.2`, validated at startup (non-negative, sum 1).
  - An organization can override the weights for a concrete activity. All three are given together and must be non-negative and sum to 1, otherwise 400. Absent means the default applies.
  - The override is used only when ranking volunteers for that activity. Activities shown to a volunteer are always ranked with the global weights, so an organization cannot inflate its position.
  - The score breakdown returns the weights that were used.

### Feasibility (Slice 6b, schema of Slices 3 and 4)

- **Q13. Availability.** An activity has one `startsAt`/`endsAt`. A volunteer has weekly slots (day of week + time range). Available means the slots fully cover the activity window, in one configured time zone. An accepted activity that overlaps makes the volunteer unavailable. Multi-day activities are out of scope.
- **Q14. Certifications.** Optional `validUntil`, checked against the activity start. Self-declared, no verification.
- **Q15. Platform conditions** (activity `OPEN`, free spots, no existing application, not banned) are part of the feasible set as a separate named predicate, next to the `FUNCTION.md` filter.
- **Q16. `Cap(v)` is the remaining capacity of volunteer `v`, counted in activities and calculated.** The volunteer stores `maxActiveAssignments`; `Cap(v)` = that minus the accepted applications on activities not yet completed or cancelled. No stored counter is decremented.

### Assignment (Slices 6c and 6d)

- **Q17. Ranked top-N list** with breakdown and a limit parameter; `v*` is the first element. Tie-break as in `FUNCTION.md` 4: larger `u_skill`, then larger `u_geo`, then lower id for a deterministic order.
- **Q18. Batch auto-assign (6d) stays optional.** Decide after 6c.

### Scope

- **Q19. Invitations are Slice 5b, after 6c.** An organization invites from the recommendation list.
- **Q20. Evaluation baselines: both** nearest-distance and random, over the same feasible set.
