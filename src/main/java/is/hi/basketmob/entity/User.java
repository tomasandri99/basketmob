package is.hi.basketmob.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Column(nullable = false, length = 200)
    private String password;

    @Column(length = 50)
    private String displayName;

    @Column(length = 500)
    private String avatarUrl;

    @Column(length = 20)
    private String gender; // e.g., male | female | other

    @Column(nullable = false, columnDefinition = "timestamp")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(nullable = false, columnDefinition = "timestamp")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

