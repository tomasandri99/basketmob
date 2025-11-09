package is.hi.basketmob.dto;

import is.hi.basketmob.entity.Game;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class GameUpdateRequest {

    @NotNull
    private Game.Status status;

    @Min(0)
    private Integer homeScore;

    @Min(0)
    private Integer awayScore;

    public Game.Status getStatus() {
        return status;
    }

    public void setStatus(Game.Status status) {
        this.status = status;
    }

    public Integer getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(Integer homeScore) {
        this.homeScore = homeScore;
    }

    public Integer getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(Integer awayScore) {
        this.awayScore = awayScore;
    }
}
