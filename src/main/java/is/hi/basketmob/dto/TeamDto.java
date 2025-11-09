package is.hi.basketmob.dto;

public record TeamDto(
        Long id,
        String name,
        String shortName,
        String city,
        String logoUrl
) {
    public TeamDto(Long id, String name) {
        this(id, name, null, null, null);
    }
}
