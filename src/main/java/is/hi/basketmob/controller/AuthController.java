package is.hi.basketmob.controller;

import is.hi.basketmob.dto.UserResponse;
import is.hi.basketmob.dto.UserSignupRequest;
import is.hi.basketmob.dto.LoginRequest;
import is.hi.basketmob.dto.AuthResponse;
import is.hi.basketmob.entity.User;
import is.hi.basketmob.mapper.UserMapper;
import is.hi.basketmob.repository.UserRepository;
import is.hi.basketmob.service.AuthTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService tokenService;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuthTokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserSignupRequest req) {
        if (req == null || req.email == null || req.email.isBlank()
                || req.password == null || req.password.isBlank()
                || req.displayName == null || req.displayName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }

        userRepository.findByEmail(req.email).ifPresent(u -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        });

        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        boolean firstUser = userRepository.count() == 0;

        User u = new User();
        u.setEmail(req.email);
        u.setPassword(passwordEncoder.encode(req.password));
        u.setDisplayName(req.displayName);
        u.setAdmin(firstUser);
        u.setCreatedAt(nowUtc);
        u.setUpdatedAt(nowUtc);
        u = userRepository.save(u);

        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponse(u));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        if (req == null || req.email == null || req.password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing credentials");
        }

        User u = userRepository.findByEmail(req.email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.password, u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = tokenService.issueToken(u.getId(), u.isAdmin());
        OffsetDateTime expires = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);

        return new AuthResponse(token, expires, UserMapper.toResponse(u));
    }
}
