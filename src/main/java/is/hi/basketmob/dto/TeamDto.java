package is.hi.basketmob.dto;

/** Simplified Team data for use in responses (e.g., favorites list, game details). */
public class TeamDto {
    public Long id;
    public String name;

    public TeamDto() {}
    public TeamDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    // Convenience constructor, in case we only have name
    public TeamDto(String name) {
        this.id = null;
        this.name = name;
    }
}
