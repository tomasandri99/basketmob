#BasketMob is
A basketball match tracker app for Iceland.


#The features


- Live scores from Icelandic basketball games
- Team and player stats
- Notifications for your favorite teams

#Clone this repository
- git clone https://github.com/tomasandri99/basketmob.git

## How to run
- Java 11
- In IntelliJ: run `BasketballApplication`
- Or with Maven: `./mvnw spring-boot:run` (if wrapper added)
- App: http://localhost:8080

## Endpoints (A2 slice)
- GET `/api/v1/games?date=YYYY-MM-DD&page=&size=&sort=` → List<GameListItemDto>
- GET `/api/v1/games/{id}` → GameDto (404 if missing)

### Example curl
curl "http://localhost:8080/api/v1/games?date=2025-10-01&page=0&size=10&sort=tipoff,asc"
curl "http://localhost:8080/api/v1/games/1"
curl -i "http://localhost:8080/api/v1/games/999999"

## Status (A2)
- **You:** Controller + DTOs implemented; Sequence diagram in `/docs`.
- **Partner:** Class diagram; Service/Repository (in-memory now or DB later). Will replace GameServiceStub.
- Known: Using Java 11; simple stubbed data in service for demo; R2DBC autoconfiguration excluded if needed.
