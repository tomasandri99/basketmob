package is.hi.basketmob;

public class TeamDto {
    private final Long id;
    private final String name;

    public TeamDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "TeamDto{id=" + id + ", name='" + name + "'}";
    }
}
