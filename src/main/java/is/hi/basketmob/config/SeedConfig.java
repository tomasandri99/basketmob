package is.hi.basketmob.config;

import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.League;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.repository.GameRepository;
import is.hi.basketmob.repository.LeagueRepository;
import is.hi.basketmob.repository.TeamRepository;
import is.hi.basketmob.seed.GameSeed;
import is.hi.basketmob.seed.LeagueDataClient;
import is.hi.basketmob.seed.LeagueSeed;
import is.hi.basketmob.seed.TeamSeed;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SeedConfig {

    @Bean
    CommandLineRunner seedDatabase(LeagueDataClient dataClient,
                                   LeagueRepository leagues,
                                   TeamRepository teams,
                                   GameRepository games) {
        return args -> {
            if (leagues.count() > 0L) {
                return;
            }

            List<LeagueSeed> leagueSeeds = dataClient.loadLeagues();
            for (LeagueSeed leagueSeed : leagueSeeds) {
                League league = new League();
                league.setName(leagueSeed.getName());
                league.setSeason(leagueSeed.getSeason());
                league = leagues.save(league);

                Map<String, Team> teamLookup = new HashMap<>();
                for (TeamSeed teamSeed : leagueSeed.getTeams()) {
                    Team team = new Team();
                    team.setName(teamSeed.getName());
                    team.setShortName(teamSeed.getShortName());
                    team.setCity(teamSeed.getCity());
                    team.setLogoUrl(teamSeed.getLogo());
                    team.setLeague(league);
                    team = teams.save(team);
                    teamLookup.put(team.getName(), team);
                }

                for (GameSeed gameSeed : leagueSeed.getGames()) {
                    Team home = teamLookup.get(gameSeed.getHome());
                    Team away = teamLookup.get(gameSeed.getAway());
                    if (home == null || away == null) {
                        continue;
                    }
                    Game game = new Game();
                    game.setLeague(league);
                    game.setHomeTeam(home);
                    game.setAwayTeam(away);
                    game.setTipoff(LocalDateTime.parse(gameSeed.getTipoff()));
                    Game.Status status = gameSeed.getStatus() == null
                            ? Game.Status.SCHEDULED
                            : Game.Status.valueOf(gameSeed.getStatus());
                    game.setStatus(status);
                    game.setHomeScore(gameSeed.getHomeScore());
                    game.setAwayScore(gameSeed.getAwayScore());
                    games.save(game);
                }
            }
        };
    }
}
