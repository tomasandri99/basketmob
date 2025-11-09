package is.hi.basketmob.service;

import is.hi.basketmob.dto.StandingDto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy that caches standings results to avoid recomputing expensive aggregates.
 */
@Service
@Primary
public class CachingStandingsProxy implements StandingsProvider {

    private static final Duration TTL = Duration.ofMinutes(5);

    private final StandingsProvider delegate;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public CachingStandingsProxy(LeagueService delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<StandingDto> getStandings(Long leagueId, String season) {
        String key = cacheKey(leagueId, season);
        CacheEntry entry = cache.get(key);
        Instant now = Instant.now();
        if (entry != null && entry.expiresAt.isAfter(now)) {
            return entry.value;
        }
        List<StandingDto> result = delegate.getStandings(leagueId, season);
        List<StandingDto> snapshot = List.copyOf(result);
        cache.put(key, new CacheEntry(snapshot, now.plus(TTL)));
        return snapshot;
    }

    private String cacheKey(Long leagueId, String season) {
        return leagueId + ":" + (season == null ? "" : season);
    }

    private record CacheEntry(List<StandingDto> value, Instant expiresAt) {}
}
