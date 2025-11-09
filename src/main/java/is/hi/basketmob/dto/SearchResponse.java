package is.hi.basketmob.dto;

import java.util.List;

public record SearchResponse(
        List<TeamDto> teams,
        List<GameListItemDto> games
) {}
