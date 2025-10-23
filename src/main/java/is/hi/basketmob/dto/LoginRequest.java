package is.hi.basketmob.dto;

import javax.validation.constraints.NotBlank;

/** Request body for user login. */
public class LoginRequest {
    @NotBlank(message = "Email is required")
    public String email;

    @NotBlank(message = "Password is required")
    public String password;

    public LoginRequest() {}
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
