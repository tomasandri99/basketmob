package is.hi.basketmob.dto;

public record StandingDto(
        Long teamId,
        String teamName,
        int wins,
        int losses,
        int gamesPlayed,
        int pointsFor,
        int pointsAgainst,
        double winPct
) {}
