package is.hi.basketmob.dto;


public record GameDto(
        Long id,
        String date,
        String tipoff,
        TeamDto homeTeam,
        TeamDto awayTeam,
        Integer homeScore,
        Integer awayScore
) {}





