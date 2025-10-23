package is.hi.basketmob.dto;

import java.time.OffsetDateTime;

public class AuthResponse {
    public String token;
    public OffsetDateTime expiresAt;
    public UserResponse user;

    public AuthResponse() {}

    public AuthResponse(String token, OffsetDateTime expiresAt, UserResponse user) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.user = user;
    }
}
