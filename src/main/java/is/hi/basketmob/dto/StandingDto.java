package is.hi.basketmob.dto;

public class StandingDto {

    private final Long teamId;
    private final String teamName;
    private final int wins;
    private final int losses;
    private final int gamesPlayed;
    private final int pointsFor;
    private final int pointsAgainst;
    private final double winPct;

    public StandingDto(Long teamId,
                       String teamName,
                       int wins,
                       int losses,
                       int gamesPlayed,
                       int pointsFor,
                       int pointsAgainst,
                       double winPct) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.wins = wins;
        this.losses = losses;
        this.gamesPlayed = gamesPlayed;
        this.pointsFor = pointsFor;
        this.pointsAgainst = pointsAgainst;
        this.winPct = winPct;
    }

    public Long teamId() {
        return teamId;
    }

    public String teamName() {
        return teamName;
    }

    public int wins() {
        return wins;
    }

    public int losses() {
        return losses;
    }

    public int gamesPlayed() {
        return gamesPlayed;
    }

    public int pointsFor() {
        return pointsFor;
    }

    public int pointsAgainst() {
        return pointsAgainst;
    }

    public double winPct() {
        return winPct;
    }
}
