package pakki.dto;

public record GameListItemDto(
        Long id,
        String tipoff,
        String status,
        String homeName,
        String awayName
) {}
