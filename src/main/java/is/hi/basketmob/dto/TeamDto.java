package is.hi.basketmob.dto;

/** Simplified Team data for use in responses (e.g., favorites list, game details). */
public class TeamDto {
    public Long id;
    public String name;

public record TeamDto(Long id, String name) {}
