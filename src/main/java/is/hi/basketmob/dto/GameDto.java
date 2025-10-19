package is.hi.basketmob.dto;

/**
 * Game details DTO used by GET /api/v1/games/{id}.
 * Dates/times are strings (ISO-8601) to keep it simple at the API edge.
 */
public record GameDto(
        Long id,
        String date,        // e.g., "2025-10-19"
        String tipoff,      // e.g., "19:15"
        TeamDto homeTeam,
        TeamDto awayTeam,
        Integer homeScore,  // null if not played
        Integer awayScore   // null if not played
) {}





