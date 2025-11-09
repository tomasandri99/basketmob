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
        Map<Long, StandingAccumulator> stats = new HashMap<>();
        for (Team team : leagueTeams) {
            stats.put(team.getId(), new StandingAccumulator());
        }

        List<Game> finals = games.findByLeagueIdAndStatus(leagueId, Game.Status.FINAL);
        for (Game game : finals) {
            Integer homeScore = game.getHomeScore();
            Integer awayScore = game.getAwayScore();
            if (homeScore == null || awayScore == null) {
                continue;
            }

            StandingAccumulator home = stats.get(game.getHomeTeam().getId());
            StandingAccumulator away = stats.get(game.getAwayTeam().getId());
            if (home == null || away == null) {
                continue;
            }

            home.recordGame(homeScore, awayScore, homeScore > awayScore);
            away.recordGame(awayScore, homeScore, awayScore > homeScore);
        }

        List<StandingDto> rows = new ArrayList<>();
        for (Team team : leagueTeams) {
            StandingAccumulator acc = stats.get(team.getId());
            rows.add(new StandingDto(
                    team.getId(),
                    team.getName(),
                    acc.wins,
                    acc.losses,
                    acc.gamesPlayed,
                    acc.pointsFor,
                    acc.pointsAgainst,
                    acc.winPct()
            ));
        }

        rows.sort(Comparator
                .comparingDouble(StandingDto::winPct).reversed()
                .thenComparingInt(dto -> dto.pointsFor() - dto.pointsAgainst()).reversed()
                .thenComparingInt(StandingDto::pointsFor).reversed()
                .thenComparing(StandingDto::teamName));

        return rows;
    }

    private static final class StandingAccumulator {
        int wins = 0;
        int losses = 0;
        int gamesPlayed = 0;
        int pointsFor = 0;
        int pointsAgainst = 0;

        void recordGame(int scored, int allowed, boolean win) {
            gamesPlayed++;
            pointsFor += scored;
            pointsAgainst += allowed;
            if (win) {
                wins++;
            } else {
                losses++;
            }
        }

        double winPct() {
            return gamesPlayed == 0 ? 0d : (double) wins / gamesPlayed;
        }
    }
}
