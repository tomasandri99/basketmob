package is.hi.basketmob.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Game game;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false)
    private boolean readFlag = false;

    @Column(nullable = false, columnDefinition = "timestamp")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Notification() {
    }
 
    public Notification(User user, Game game, String message) {
        this.user = user;
        this.game = game;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Game getGame() {
        return game;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReadFlag() {
        return readFlag;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReadFlag(boolean readFlag) {
        this.readFlag = readFlag;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
