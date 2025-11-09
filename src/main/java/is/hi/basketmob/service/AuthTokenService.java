package is.hi.basketmob.service;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthTokenService {

    public record AuthSession(Long userId, boolean admin, OffsetDateTime expiresAt) {}

    private final Map<String, AuthSession> tokens = new ConcurrentHashMap<>();

    public String issueToken(Long userId, boolean admin) {
        String token = UUID.randomUUID().toString();
        OffsetDateTime expires = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);
        tokens.put(token, new AuthSession(userId, admin, expires));
        return token;
    }

    public Optional<AuthSession> resolve(String token) {
        if (token == null) return Optional.empty();
        AuthSession session = tokens.get(token);
        if (session == null) return Optional.empty();
        if (session.expiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            tokens.remove(token);
            return Optional.empty();
        }
        return Optional.of(session);
    }

    public void revoke(String token) {
        if (token != null) {
            tokens.remove(token);
        }
    }
}
