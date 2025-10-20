package is.hi.basketmob.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// ✅ use the repositories from the correct package:
import is.hi.basketmob.repository.GameRepository;
import is.hi.basketmob.repository.TeamRepository;

@Configuration
public class SeedConfig {

    @Bean
    CommandLineRunner seed(GameRepository games, TeamRepository teams) {
        return args -> {
            // No-op seeding for now (compile-only). Add data later.
            // Example (when your entities are ready):
            // if (teams.count() == 0) { ... teams.saveAll(...); }
            // if (games.count() == 0) { ... games.saveAll(...); }
        };
    }
}
