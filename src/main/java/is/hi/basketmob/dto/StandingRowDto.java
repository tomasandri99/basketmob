package is.hi.basketmob.dto;

public record StandingRowDto(
        Long teamId, String teamName, Integer wins, Integer losses, boolean stale
) {}
