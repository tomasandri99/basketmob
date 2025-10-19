package is.hi.basketmob.dto;


public record GameDto(
        Long id,
        String status,
        String tipoff,   // ISO-8601 string, e.g. 2025-10-01T19:00:00Z
        TeamDto home,
        TeamDto away,
        Integer homeScore,
        Integer awayScore
        ) {}


