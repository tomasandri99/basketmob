package is.hi.basketmob.entity;

import is.hi.basketmob.entities.User;
import is.hi.basketmob.entity.Team;

import javax.persistence.*;
import java.time.OffsetDateTime;

/** Entity representing a user following a team (a favorite team). */
@Entity
@Table(name = "favorites",
        uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "team_id" }))
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(nullable = false, columnDefinition = "timestamp")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Favorite() {}
    public Favorite(User user, Team team) {
        this.user = user;
        this.team = team;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Team getTeam() { return team; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setUser(User user) { this.user = user; }
    public void setTeam(Team team) { this.team = team; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
