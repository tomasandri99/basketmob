package is.hi.basketmob.controller;

import is.hi.basketmob.dto.UserSignupRequest;
import is.hi.basketmob.dto.LoginRequest;
import is.hi.basketmob.dto.AuthResponse;
import is.hi.basketmob.dto.UserResponse;
import is.hi.basketmob.mapper.UserMapper;
import is.hi.basketmob.service.AuthService;
import is.hi.basketmob.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** User registration endpoint: create a new account */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserSignupRequest request) {
        User createdUser = authService.register(request);
        // Map to DTO and return 201 Created
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserMapper.toResponse(createdUser));
    }

    /** User login endpoint: verify credentials and return auth token */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
