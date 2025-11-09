# BasketMob

BasketMob is a Spring Boot REST API and lightweight Thymeleaf UI that serves Icelandic basketball fixtures, standings, search, and personalized notifications. It is built for the HBV501G Software Project course (Construction II deliverables).

## Features

- Pre-seeded Dominos League 2025–2026 schedule (fixtures, standings, teams, scores).
- Advanced fixtures endpoint with pagination, additive date filters, league/status filters, and cached responses.
- Detailed game payloads with enriched team metadata and stale flags for stub mode.
- Secure user management (`POST /api/v1/users`, profile CRUD, password change) plus auth tokens with logout.
- Favorites + notifications flow: follow teams, get alerts when an admin finalizes a game, view/clear notifications.
- Search endpoint that returns top team and game hits for a query.
- Minimal dashboard/fixtures/standings views rendered with Thymeleaf for in-class demos.
- Course alignment checklists in `checks/` for A3/A4 and course-wide requirements.

## Requirements

- Java 17
- Maven 3.8+ (the repo ships with `mvnw`)
- H2 (embedded) for dev/test – no external DB needed

## Running locally

```bash
git clone https://github.com/tomasandri99/basketmob.git
cd basketmob
./mvnw spring-boot:run
```

The app starts on <http://localhost:8080>. Useful URLs:

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- Thymeleaf UI: `/`, `/games`, `/standings`, `/dashboard`
- H2 console: <http://localhost:8080/h2-console> (JDBC `jdbc:h2:mem:bm`, user `sa`, blank password)

### Seed data & accounts

- The JSON schedule at `src/main/resources/data/dominos-2025.json` seeds leagues, teams (with city/short name/logo), and fixtures on startup (`SeedConfig` + `LeagueDataClient`). Delete the H2 file or restart the process to reseed.
- A dev user is bootstrapped via `DevData`: `tomas@example.com` / `Password123!`
- The first user you register (via UI or `POST /api/v1/users`) becomes an admin by default.

## Testing & CI

Run the JUnit suite locally:

```bash
./mvnw -q test
```

GitHub Actions (`.github/workflows/build.yml`) runs the same command on every push/PR, so please keep tests green.

## Key endpoints

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/v1/users` | Register a new user (validated, returns 201 + body) |
| `POST` | `/api/v1/auth/login` | Password login; returns bearer token + user info |
| `POST` | `/api/v1/auth/logout` | Revokes token (header/cookie) and clears auth cookie |
| `GET` | `/api/v1/games` | Fixtures list with pagination + optional `date`, `dateFrom`, `dateTo`, `leagueId`, `status` filters |
| `GET` | `/api/v1/games/{id}` | Detailed game payload (teams, status, league, stale flag) |
| `GET` | `/api/v1/leagues/{id}/standings?season=` | Computed standings with wins/losses/points and cached proxy |
| `GET` | `/api/v1/search?q=` | Search teams + games (top 5–25 matches) |
| `GET/POST/DELETE` | `/api/v1/me/favorites` | List/follow/unfollow teams (auth required, idempotent) |
| `GET/POST/DELETE` | `/api/v1/me/notifications` | List, mark read, and clear notifications |
| `POST` | `/api/v1/admin/games/{id}/finalize` | Admin-only score update + notification fan-out |
| `GET/PUT/PATCH/DELETE` | `/api/v1/users/{id}`, `/api/v1/users/me` | Full profile management with ownership enforcement |

The OpenAPI doc in Swagger lists every available response schema.

## Frontend preview

The `/games`, `/standings`, and `/dashboard` Thymeleaf pages call the REST API using Fetch, so instructors can demo the project without Postman:

- `/games` applies filters and renders the `/api/v1/games` response.
- `/standings` fetches `/api/v1/leagues/{id}/standings` and builds a table.
- `/dashboard` shows the authenticated user’s favorites and notifications.

## Course checklists

Two machine-readable checklists live in `checks/`:

- `a3_a4_checklist.json` – Construction I/II scope, acceptance matrix, and autotest hints.
- `course_wide_requirements.json` – Global HBV501G expectations (endpoint types, UP phases, Git workflow).

You can point an automation agent to these files to verify criteria or to spin up API smoke tests.

## Contributing

The repo now works off the `part4_aok` branch; all feature branches have been merged. Feel free to create short-lived branches for bug fixes, but please run `./mvnw test` before pushing and include Tomas (`@tomasandri99`) in every PR so both contributors are credited.​
