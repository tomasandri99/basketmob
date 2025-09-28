package is.hi.basketmob;

public class GameListItemDto {
    private final Long id;
    private final String tipoff;
    private final String status;
    private final String homeName;
    private final String awayName;

    public GameListItemDto(Long id, String tipoff, String status,
                           String homeName, String awayName) {
        this.id = id;
        this.tipoff = tipoff;
        this.status = status;
        this.homeName = homeName;
        this.awayName = awayName;
    }

    public Long getId() { return id; }
    public String getTipoff() { return tipoff; }
    public String getStatus() { return status; }
    public String getHomeName() { return homeName; }
    public String getAwayName() { return awayName; }

    @Override
    public String toString() {
        return "GameListItemDto{id=" + id + ", tipoff='" + tipoff + "', status='" + status + "'}";
    }
}
