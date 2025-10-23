package is.hi.basketmob.dto;

public class StandingDto {
    public Long teamId;
    public String teamName;
    public int wins;
    public int losses;

    public StandingDto() {}
    public StandingDto(Long teamId, String teamName, int wins, int losses) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.wins = wins;
        this.losses = losses;
    }
}
