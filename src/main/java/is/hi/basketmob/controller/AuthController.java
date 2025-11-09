package is.hi.basketmob.controller;

import is.hi.basketmob.dto.UserResponse;
import is.hi.basketmob.dto.UserSignupRequest;
import is.hi.basketmob.dto.LoginRequest;
import is.hi.basketmob.dto.AuthResponse;
import is.hi.basketmob.mapper.UserMapper;
import is.hi.basketmob.service.AuthTokenService;
import is.hi.basketmob.service.UserService;
import is.hi.basketmob.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService tokenService;
    private final UserService userService;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          AuthTokenService tokenService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserSignupRequest req) {
        User user = userService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toResponse(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        if (req == null || req.email == null || req.password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing credentials");
        }

        User u = userService.findOptionalByEmail(req.email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(req.password, u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = tokenService.issueToken(u.getId(), u.isAdmin());
        OffsetDateTime expires = OffsetDateTime.now(ZoneOffset.UTC).plusDays(1);

        return new AuthResponse(token, expires, UserMapper.toResponse(u));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
                                       @RequestHeader(value = "X-Auth-Token", required = false) String headerToken,
                                       @CookieValue(value = "BM_TOKEN", required = false) String cookieToken,
                                       HttpServletResponse response) {
        resolveToken(authorization, headerToken, cookieToken)
                .ifPresent(tokenService::revoke);
        ResponseCookie cleared = ResponseCookie.from("BM_TOKEN", "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cleared.toString());
        return ResponseEntity.noContent().build();
    }

    private Optional<String> resolveToken(String header,
                                          String fallbackHeader,
                                          String cookieValue) {
        if (header != null && header.startsWith("Bearer ")) {
            return Optional.of(header.substring(7).trim());
        }
        if (fallbackHeader != null && !fallbackHeader.isBlank()) {
            return Optional.of(fallbackHeader.trim());
        }
        if (cookieValue != null && !cookieValue.isBlank()) {
            return Optional.of(cookieValue.trim());
        }
        return Optional.empty();
    }
}
