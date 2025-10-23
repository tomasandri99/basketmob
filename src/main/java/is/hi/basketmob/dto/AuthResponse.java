package is.hi.basketmob.dto;

import java.time.Instant;

/** Response body for successful authentication, containing token and user info. */
public class AuthResponse {
    public String token;
    public Instant expiresAt;
    public UserResponse user;  // Reuse existing UserResponse DTO for profile info

    public AuthResponse() {}
    public AuthResponse(String token, Instant expiresAt, UserResponse user) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.user = user;
    }
}
