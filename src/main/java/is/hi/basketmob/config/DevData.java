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
            if (repo.findByEmail("tomas@example.com").isEmpty()) {
                User u = new User();
                u.setEmail("tomas@example.com");
                u.setPassword(encoder.encode("Password123!"));
                u.setDisplayName("Tomas");
                repo.save(u);
            }
        };
    }
}

