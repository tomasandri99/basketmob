package is.hi.basketmob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class BasketmobApplication {
    public static void main(String[] args) {
        SpringApplication.run(BasketmobApplication.class, args);
    }
}
