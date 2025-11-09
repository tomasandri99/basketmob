package is.hi.basketmob.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Component
public class LocalJsonLeagueDataClient implements LeagueDataClient {

    private final ObjectMapper objectMapper;

    public LocalJsonLeagueDataClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public List<LeagueSeed> loadLeagues() {
        try (InputStream is = new ClassPathResource("data/dominos-2025.json").getInputStream()) {
            LeagueSeedData data = objectMapper.readValue(is, LeagueSeedData.class);
            return data.getLeagues() == null ? Collections.emptyList() : data.getLeagues();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load league seed data", e);
        }
    }
}
