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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SeedConfig {

    @Bean
    CommandLineRunner seedLeagues(LeagueRepository leagues,
                                 TeamRepository teams,
                                 GameRepository games,
                                 LeagueDataClient dataClient) {
        return args -> {
            if (leagues.count() > 0L) {
                return;
            }

            List<LeagueSeed> seedData = dataClient.loadLeagues();
            for (LeagueSeed leagueSeed : seedData) {
                League league = new League();
                league.setName(leagueSeed.getName());
                league.setSeason(leagueSeed.getSeason());
                league = leagues.save(league);

                Map<String, Team> teamLookup = new HashMap<>();
                List<Team> teamEntities = new ArrayList<>();
                for (TeamSeed teamSeed : leagueSeed.getTeams()) {
                    Team team = new Team();
                    team.setName(teamSeed.getName());
                    team.setShortName(teamSeed.getShortName());
                    team.setCity(teamSeed.getCity());
                    team.setLogoUrl(teamSeed.getLogo());
                    team.setLeague(league);
                    teamEntities.add(team);
                    teamLookup.put(team.getName(), team);
                }
                teams.saveAll(teamEntities);

                List<Game> gameEntities = new ArrayList<>();
                for (GameSeed gameSeed : leagueSeed.getGames()) {
                    Game game = new Game();
                    game.setLeague(league);
                    game.setHomeTeam(requireTeam(teamLookup, gameSeed.getHome()));
                    game.setAwayTeam(requireTeam(teamLookup, gameSeed.getAway()));
                    game.setTipoff(LocalDateTime.parse(gameSeed.getTipoff()));
                    game.setStatus(Game.Status.valueOf(gameSeed.getStatus()));
                    game.setHomeScore(gameSeed.getHomeScore());
                    game.setAwayScore(gameSeed.getAwayScore());
                    gameEntities.add(game);
                }
                games.saveAll(gameEntities);
            }
        };
    }

    private Team requireTeam(Map<String, Team> teams, String name) {
        Team team = teams.get(name);
        if (team == null) {
            throw new IllegalStateException("Unknown team referenced in seed data: " + name);
        }
        return team;
    }
}
