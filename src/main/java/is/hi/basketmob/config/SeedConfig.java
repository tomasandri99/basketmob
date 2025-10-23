package is.hi.basketmob.config;

import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.League;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.repository.GameRepository;
import is.hi.basketmob.repository.LeagueRepository;
import is.hi.basketmob.repository.TeamRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class SeedConfig {
    @Bean
    CommandLineRunner seed(LeagueRepository leagues, TeamRepository teams, GameRepository games) {
        return args -> {
            if (leagues.count() > 0L) return; // already seeded

            League l = new League();
            l.setName("Dominos League");
            l.setSeason("2025");
            l = leagues.save(l);

            Team kef = new Team(); kef.setName("Keflavík"); kef.setLeague(l);
            Team val = new Team(); val.setName("Valur");    val.setLeague(l);
            Team kr  = new Team(); kr.setName("KR");        kr.setLeague(l);
            Team hau = new Team(); hau.setName("Haukar");   hau.setLeague(l);
            teams.saveAll(List.of(kef, val, kr, hau));

            Game g1 = new Game();
            g1.setLeague(l);
            g1.setHomeTeam(kef);
            g1.setAwayTeam(val);
            g1.setTipoff(LocalDateTime.now().minusDays(1).withHour(19).withMinute(15));
            g1.setStatus(Game.Status.FINAL);
            g1.setHomeScore(88);
            g1.setAwayScore(81);

            Game g2 = new Game();
            g2.setLeague(l);
            g2.setHomeTeam(kr);
            g2.setAwayTeam(hau);
            g2.setTipoff(LocalDateTime.now().withHour(20).withMinute(0));
            g2.setStatus(Game.Status.SCHEDULED);

            games.saveAll(List.of(g1, g2));
        };
    }
}
