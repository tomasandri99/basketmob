package is.hi.basketmob.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/** Request body for user registration (sign-up). */
public class UserSignupRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    public String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 200, message = "Password must be 8–200 characters")
    public String password;

    @NotBlank(message = "Display name is required")
    @Size(min = 2, max = 50, message = "Display name must be 2–50 characters")
    public String displayName;

    public UserSignupRequest() {}
    public UserSignupRequest(String email, String password, String displayName) {
        this.email = email;
        this.password = password;
        this.displayName = displayName;
    }
}
