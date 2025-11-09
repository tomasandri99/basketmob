package is.hi.basketmob.seed;

import java.util.List;

public class LeagueSeed {
    private String name;
    private String season;
    private List<TeamSeed> teams;
    private List<GameSeed> games;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public List<TeamSeed> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamSeed> teams) {
        this.teams = teams;
    }

    public List<GameSeed> getGames() {
        return games;
    }

    public void setGames(List<GameSeed> games) {
        this.games = games;
    }
}
