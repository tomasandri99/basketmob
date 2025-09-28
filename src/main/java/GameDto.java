package is.hi.basketmob;

public class GameDto {
    private final Long id;
    private final String status;
    private final String tipoff;
    private final TeamDto home;
    private final TeamDto away;
    private final Integer homeScore;
    private final Integer awayScore;

    public GameDto(Long id, String status, String tipoff,
                   TeamDto home, TeamDto away,
                   Integer homeScore, Integer awayScore) {
        this.id = id;
        this.status = status;
        this.tipoff = tipoff;
        this.home = home;
        this.away = away;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public String getTipoff() { return tipoff; }
    public TeamDto getHome() { return home; }
    public TeamDto getAway() { return away; }
    public Integer getHomeScore() { return homeScore; }
    public Integer getAwayScore() { return awayScore; }

    @Override
    public String toString() {
        return "GameDto{id=" + id + ", status='" + status + "', tipoff='" + tipoff + "'}";
    }
}
