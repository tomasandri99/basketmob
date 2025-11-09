package is.hi.basketmob.service;

import is.hi.basketmob.dto.StandingDto;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.repository.GameRepository;
import is.hi.basketmob.repository.LeagueRepository;
import is.hi.basketmob.repository.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class LeagueService implements StandingsProvider {
    private final LeagueRepository leagues;
    private final TeamRepository teams;
    private final GameRepository games;

    public LeagueService(LeagueRepository leagues, TeamRepository teams, GameRepository games) {
        this.leagues = leagues;
        this.teams = teams;
        this.games = games;
    }

    @Transactional(readOnly = true)
    public List<StandingDto> getStandings(Long leagueId, String season) {
        var league = leagues.findById(leagueId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "LEAGUE_NOT_FOUND"));

        if (season != null && league.getSeason() != null && !season.equals(league.getSeason())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SEASON_MISMATCH");
        }

        List<Team> leagueTeams = teams.findByLeagueId(leagueId);
        Map<Long, Integer> win = new HashMap<>();
        Map<Long, Integer> loss = new HashMap<>();
        for (Team t : leagueTeams) {
            win.put(t.getId(), 0);
            loss.put(t.getId(), 0);
        }

        List<Game> finals = games.findByLeagueIdAndStatus(leagueId, Game.Status.FINAL);
        for (Game g : finals) {
            Integer hs = g.getHomeScore(), as = g.getAwayScore();
            if (hs == null || as == null) continue;
            Long hid = g.getHomeTeam().getId(), aid = g.getAwayTeam().getId();
            if (hs > as) {
                win.put(hid, win.get(hid) + 1);
                loss.put(aid, loss.get(aid) + 1);
            } else if (as > hs) {
                win.put(aid, win.get(aid) + 1);
                loss.put(hid, loss.get(hid) + 1);
            }
        }

        List<StandingDto> rows = new ArrayList<>();
        for (Team t : leagueTeams) {
            rows.add(new StandingDto(t.getId(), t.getName(), win.get(t.getId()), loss.get(t.getId())));
        }

        rows.sort(Comparator.comparingInt((StandingDto s) -> s.wins).reversed()
                .thenComparingInt(s -> s.losses)
                .thenComparing(s -> s.teamName));

        return rows;
    }
}
