# BasketMob

BasketMob is a Spring Boot REST API for Icelandic basketball fixtures, standings, global search, and personalized notifications. It powers the HBV501G Software Project deliverables without any server-side HTML.

## Features

- Real 2025–2026 Bónus deild karla dataset (`src/main/resources/data/dominos-2025.json`) with scores for completed games and future fixtures for the rest of the season.
- Advanced fixtures endpoint with pagination, additive date filters (`date`, `dateFrom`, `dateTo`), league/status filters, caching, and ISO-8601 timestamps.
- Detailed game payloads, computed standings, and a search API covering teams plus games.
- Secure account management: register, login/logout, profile PUT/PATCH/DELETE, and token-based auth.
- Favorites + notifications flow (follow/unfollow teams, mark notifications read, clear inbox).
- Admin-only finalize endpoint to lock scores and push notifications.
- Configurable rate limiting (`basketmob.rate-limit.*`) enforced by `RateLimitFilter` with integration coverage.
- Swagger/OpenAPI exposed at `/swagger` (redirect to `/swagger-ui/index.html`) and `/v3/api-docs`.
- Reproducible smoke script (`scripts/smoke-all.ps1`) that drives every endpoint end-to-end.

## Requirements

- Java 17
- Maven 3.8+ (wrapper included)
- No external database — H2 in-memory is used for dev/test

## Running locally

```bash
git clone https://github.com/tomasandri99/basketmob.git
cd basketmob
./mvnw spring-boot:run
```

The service listens on <http://localhost:8080>.

- Swagger UI: <http://localhost:8080/swagger>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>
- H2 console: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:bm`, user `sa`, blank password)

### Seed data & built-in accounts

- `SeedConfig` loads `src/main/resources/data/dominos-2025.json` on startup. Restarting the app resets the league, teams, and fixtures to match that dataset.
- `DevData` injects two helper accounts:
  - `tomas@example.com / Password123!` (standard user)
  - `admin@basketmob.is / Admin123!` (ROLE_ADMIN, used for `/api/v1/admin/games/{id}`)
- `scripts/smoke-all.ps1` spins up two disposable users, exercises every endpoint, and deletes them. Run it with `pwsh ./scripts/smoke-all.ps1` once the app is running.

### Rate limit configuration

`RateLimitFilter` guards all `/api/**` routes. Defaults live in `src/main/resources/application.properties`:

```properties
basketmob.rate-limit.enabled=true
basketmob.rate-limit.limit=120
basketmob.rate-limit.window=PT1M
```

Override them via system properties or environment variables, e.g. `./mvnw spring-boot:run -Dbasketmob.rate-limit.limit=60`.

## Testing & CI

```bash
./mvnw -q test
```

Tests cover service layers plus the rate-limiting filter. GitHub Actions (see `.github/workflows/build.yml`) runs the same command on every push/PR.

## Key endpoints

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/v1/users` | Register a user |
| `POST` | `/api/v1/auth/login` | Exchange credentials for a bearer token |
| `POST` | `/api/v1/auth/logout` | Invalidate the current token |
| `GET` | `/api/v1/games` | Fixtures (pagination, sort, additive filters) |
| `GET` | `/api/v1/games/{id}` | Detailed game payload |
| `GET` | `/api/v1/leagues/{id}/standings?season=` | Computed standings |
| `GET` | `/api/v1/search?q=` | Global search |
| `GET/POST/DELETE` | `/api/v1/me/favorites` | List/follow/unfollow teams |
| `GET/POST/DELETE` | `/api/v1/me/notifications` | List, mark read, clear notifications |
| `PATCH` | `/api/v1/admin/games/{id}` | Admin-only status/score update (triggers notifications) |
| `GET/PUT/PATCH/DELETE` | `/api/v1/users/{id}` & `/api/v1/users/me` | Full profile management |

Swagger lists every response schema and error shape.

## Acceptance checklist / smoke test

[`docs/acceptance.md`](docs/acceptance.md) documents the instructor scenarios. TL;DR:

```powershell
pwsh ./scripts/smoke-all.ps1
```

The script registers two accounts, runs through auth, fixtures, standings, search, favorites/notifications, admin finalize, and the delete flows. It fails fast if any endpoint deviates from the expected 2xx/204 responses.

## Course checklists

Machine-readable progress trackers live in `checks/`:

- `a3_a4_checklist.json` – Construction I & II requirements
- `course_wide_requirements.json` – global HBV501G requirements (endpoint types, sprint cadence, etc.)

## Contributing

Development happens on `part4_aok`. Create short-lived feature branches, run `./mvnw test`, and tag Tomas (`@tomasandri99`) in pull requests so both contributors receive credit.
