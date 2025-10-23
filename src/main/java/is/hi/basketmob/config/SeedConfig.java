// src/main/java/is/hi/basketmob/config/SeedConfig.java
package is.hi.basketmob.config;

import is.hi.basketmob.entity.*;
import is.hi.basketmob.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.Game.Status;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class SeedConfig {
    @Bean
    CommandLineRunner seed(GameRepository games, TeamRepository teams, LeagueRepository leagues) {
        return args -> {
            if (leagues.count() > 0) return;

            League l = new League();
            l.setName("Dominos League");
            l.setSeason("2025");
            leagues.save(l);

            Team t1 = new Team(); t1.setName("Keflavík"); t1.setLeague(l);
            Team t2 = new Team(); t2.setName("Valur");    t2.setLeague(l);
            Team t3 = new Team(); t3.setName("KR");       t3.setLeague(l);
            Team t4 = new Team(); t4.setName("Haukar");   t4.setLeague(l);
            teams.saveAll(List.of(t1,t2,t3,t4));


            Game g1 = new Game(
                    l,         // League
                    t1,        // homeTeam
                    t2,        // awayTeam
                    LocalDateTime.now().minusDays(1).withHour(19).withMinute(15),
                    Status.FINAL,
                    88,        // homeScore (Integer)
                    81         // awayScore (Integer)
            );


            Game g2 = new Game(
                    l,
                    t3,
                    t4,
                    LocalDateTime.now().withHour(20).withMinute(0),
                    Status.SCHEDULED,
                    null,      // homeScore
                    null       // awayScore
            );

            games.saveAll(List.of(g1, g2));
        };
    }
}

