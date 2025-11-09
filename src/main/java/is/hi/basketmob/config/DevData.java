package is.hi.basketmob.config;

import is.hi.basketmob.entity.User;
import is.hi.basketmob.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevData {

    @Bean
    CommandLineRunner seedUser(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByEmail("admin@basketmob.is").isEmpty()) {
                User admin = new User();
                admin.setEmail("admin@basketmob.is");
                admin.setPassword(encoder.encode("Admin123!"));
                admin.setDisplayName("BasketMob Admin");
                admin.setAdmin(true);
                repo.save(admin);
            }
        };
    }
}
