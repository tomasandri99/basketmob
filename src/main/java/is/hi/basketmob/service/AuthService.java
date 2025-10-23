package is.hi.basketmob.service;

import is.hi.basketmob.dto.UserSignupRequest;
import is.hi.basketmob.dto.LoginRequest;
import is.hi.basketmob.dto.AuthResponse;
import is.hi.basketmob.dto.UserResponse;
import is.hi.basketmob.entities.User;
import is.hi.basketmob.mapper.UserMapper;
import is.hi.basketmob.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Service for authentication and user account creation/login. */
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // In-memory token store: token -> (userId and expiry)
    private final ConcurrentMap<String, SessionInfo> tokenStore = new ConcurrentHashMap<>();

    // Small inner class to hold session info
    private static class SessionInfo {
        Long userId;
        Instant expiresAt;
        SessionInfo(Long userId, Instant expiresAt) {
            this.userId = userId;
            this.expiresAt = expiresAt;
        }
    }

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Register a new user account (throws 409 if email is already taken). */
    @Transactional
    public User register(UserSignupRequest req) {
        // Check if email is already in use
        if (userRepository.findByEmail(req.email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        // Create new User entity
        User user = new User();
        user.setEmail(req.email.trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(req.password));
        user.setDisplayName(req.displayName.trim());
        // (Optional: set default avatar or gender if needed)
        // Save to database
        User saved = userRepository.save(user);
        return saved;
    }

    /** Authenticate user credentials and issue a token (throws 401 if invalid). */
    public AuthResponse login(LoginRequest req) {
        // Find user by email
        User user = userRepository.findByEmail(req.email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        // Check password match
        if (!passwordEncoder.matches(req.password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        // Generate a new token and expiration (e.g., 24 hours from now)
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(24, ChronoUnit.HOURS);
        tokenStore.put(token, new SessionInfo(user.getId(), expiry));

        // Build response with token and user profile info
        UserResponse profile = UserMapper.toResponse(user);
        return new AuthResponse(token, expiry, profile);
    }

    /** Validate a token and return the associated user ID (throws 401 if invalid or expired). */
    public Long validateToken(String token) {
        if (token == null || !tokenStore.containsKey(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid authentication token");
        }
        SessionInfo session = tokenStore.get(token);
        // Check expiration
        if (session.expiresAt.isBefore(Instant.now())) {
            tokenStore.remove(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session token has expired, please log in again");
        }
        return session.userId;
    }

    /** (Optional) Invalidate a token, e.g., for logout */
    public void logout(String token) {
        if (token != null) {
            tokenStore.remove(token);
        }
    }
}
