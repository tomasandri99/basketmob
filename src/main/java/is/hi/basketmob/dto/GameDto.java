package is.hi.basketmob.dto;

public record GameDto(
        Long id,
        String date,
        String tipoff,
        String status,
        TeamDto homeTeam,
        TeamDto awayTeam,
        Integer homeScore,
        Integer awayScore,
        String leagueName,
        boolean stale
) {}





