# Acceptance Checklist

This document summarizes what the HBV501G instructors ask for in A4 and how to prove it quickly.

## 1. Boot the service

```bash
./mvnw spring-boot:run
```

The app listens on `http://localhost:8080`. Swagger UI lives at `/swagger` (redirects to `/swagger-ui/index.html`) and the OpenAPI JSON is at `/v3/api-docs`.

## 2. Run the smoke script (hits every endpoint)

```powershell
pwsh ./scripts/smoke-all.ps1
```

What it does:

1. Registers two temporary users, logs one in, and drives profile CRUD (PUT/PATCH/DELETE).
2. Exercises fixtures (`/api/v1/games` with pagination + filters), game details, standings, search.
3. Exercises the user-specific flows: `/api/v1/users/me`, favorites follow/unfollow, notifications list/read/delete.
4. Logs in as the seeded admin (`admin@basketmob.is` / `Admin123!`) to patch a game score and mutate another user.
5. Logs everyone out and deletes the temporary accounts.

The script prints each endpoint as it runs; everything should return 2xx except the final deletes, which return 204 No Content. If any step fails the script throws, so a green run is an acceptance proof.

## 3. Rate limit verification

The filter is on by default (`basketmob.rate-limit.limit=120`, `basketmob.rate-limit.window=PT1M`). You can tighten the threshold when needed, e.g.:

```bash
./mvnw spring-boot:run -Dbasketmob.rate-limit.limit=10 -Dbasketmob.rate-limit.window=PT30S
```

The integration test `RateLimitFilterTest` locks this behaviour by asserting the 3rd rapid call to `/api/v1/games` receives HTTP 429.

## 4. Data provenance

`src/main/resources/data/dominos-2025.json` contains the Icelandic Bónus deild karla schedule you provided (completed + upcoming fixtures). Restarting the app reseeds the league/teams/games so every endpoint stays in sync with that dataset.

Keep this checklist handy during demos: start the app, run the script, and show `/swagger` for a quick tour.
