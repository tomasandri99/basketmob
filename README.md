# BasketMob

BasketMob is a course project for HBV501G that delivers a secure backend (with a light Thymeleaf UI) for tracking the 2025‑2026 Bónus deild karla season. It exposes a full REST API, persistence layer, OpenAPI docs, and three required design patterns (Singleton, Observer, Proxy).

## Features

- Token-based authentication (API + UI cookie) with user self-service profile.
- League standings computed from final game results (wins/losses, PF/PA, win%).
- Games endpoint (date-filtered + pagination) and detail view.
- Favorites per user with validation and Observer-driven notifications when a followed team finishes a game.
- Admin API to finalize games, triggering notification fanout.
- Seeded Icelandic league data (teams, logos, 30+ real fixtures/results).
- Minimal UI (home, games, standings, dashboard) for demoing login, profile edits, favorites, and notifications.

## Getting Started

```bash
git clone https://github.com/tomasandri99/basketmob.git
cd basketmob
./mvnw spring-boot:run
```

Requirements: JDK 17+. The embedded H2 database auto-loads the league dataset and a single admin user on startup.

- UI: http://localhost:8080
- Swagger / OpenAPI: http://localhost:8080/swagger
- H2 console (dev only): http://localhost:8080/h2-console (JDBC `jdbc:h2:mem:bm`)

### Default Admin

| Email              | Password   |
|--------------------|------------|
| `admin@basketmob.is` | `Admin123!` |

Use the admin token (login via UI or API) to finalize games through `/api/v1/admin/games/{id}`.

## REST Highlights

| Area      | Endpoint (excerpt)                                   | Notes                                |
|-----------|-------------------------------------------------------|--------------------------------------|
| Auth      | `POST /api/v1/auth/register`, `POST /api/v1/auth/login` | Returns bearer token (24h TTL)       |
| Users     | `GET /api/v1/users/me`, `PUT/PATCH /api/v1/users/{id}`, `DELETE /api/v1/users/{id}` | Owner-only + admin override          |
| Games     | `GET /api/v1/games?date=YYYY-MM-DD`, `GET /api/v1/games/{id}` | Paginated, sorted by tipoff          |
| Standings | `GET /api/v1/leagues/{id}/standings`                  | Includes wins/losses/PF/PA/win%      |
| Favorites | `GET/POST/DELETE /api/v1/me/favorites`                | Prevents duplicates, persists in DB  |
| Notifications | `GET/POST/DELETE /api/v1/me/notifications`       | Observer pattern output              |
| Admin     | `PATCH /api/v1/admin/games/{id}`                      | Update status + scores (auth admin)  |

Swagger shows every request/response model for demoing via Postman/cURL.

## UI Walkthrough

1. **Register or log in** (Thymeleaf form posts to the same auth stack as the API).
2. **Dashboard** lets you:
   - Update profile (display name, avatar, gender).
   - Follow/unfollow any team in the league.
   - Review and clear notifications triggered by finished games.
3. **Games / Standings** pages reuse the production API but offer a simple view for live demos.

Tokens are stored in an HTTP-only `BM_TOKEN` cookie for UI sessions; API consumers continue to send `Authorization: Bearer <token>`.

## Data & Seed

`src/main/resources/data/dominos-2025.json` holds:
- 12 real teams (Álftanes, Ármann, KR, Stjarnan, Keflavík, ÍR, ÍA, Þór Þ., Grindavík, Njarðvík, Valur, Tindastóll).
- 30+ fixtures (Oct–Nov 2025) with actual scores and venues, ensuring standings reflect real win/loss permutations.

On first boot the `SeedConfig` imports this file and persists it via Spring Data JPA. Delete your DB (or restart, since H2 is in-memory) to reseed.

## Design Patterns

- **Singleton** – `AuthTokenService` (central token store, concurrency-safe, one instance per app).
- **Observer** – `GameUpdatePublisher` + `NotificationService`. When an admin marks a game FINAL, every follower receives a persisted notification.
- **Proxy** – `CachingStandingsProxy` wraps `LeagueService` and memoizes standings for five minutes to avoid recomputing aggregates on every request.

These are referenced in the docs and visible in code for the Construction II deliverables.

## Testing

```bash
./mvnw test
```

Unit tests cover critical business logic (standings computation and notification fanout). Swagger/UI flows were exercised manually (login → follow team → admin finalizes game → notification appears → standings update).

## Next Steps (optional)

- Swap H2 for PostgreSQL (supply JDBC URL + credentials).
- Extend the UI with richer live updates or integrate a public stats feed.
- Add player-level stats or additional leagues (the seeding pipeline already supports multiple leagues per file).

Have fun building on BasketMob! 🎯
