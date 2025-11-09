package is.hi.basketmob.dto;

public class UserResponse {
    public Long id;
    public String email;
    public String displayName;
    public String avatarUrl;
    public String gender;
    public boolean admin;

    public UserResponse() {}
    public UserResponse(Long id, String email, String displayName, String avatarUrl, String gender, boolean admin) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
        this.gender = gender;
        this.admin = admin;
    }
}

