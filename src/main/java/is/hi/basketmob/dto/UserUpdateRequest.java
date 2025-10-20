package is.hi.basketmob.dto;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UserUpdateRequest {

    @Size(min = 2, max = 50)
    private String displayName;

    @Pattern(regexp = "^(https?://).+", message = "avatarUrl must start with http:// or https://")
    private String avatarUrl;

    @Pattern(regexp = "^(male|female|other)$", message = "gender must be one of: male, female, other")
    private String gender;

    @Size(min = 8, max = 200)
    private String newPassword;

    private String currentPassword;


    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
}

