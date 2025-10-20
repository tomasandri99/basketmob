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

        };
    }
}
