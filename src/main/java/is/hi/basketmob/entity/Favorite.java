package is.hi.basketmob.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id","team_id"}))
public class Favorite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private User user;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private Team team;

    @Column(nullable=false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public Favorite() {}
    public Favorite(User user, Team team) { this.user = user; this.team = team; }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Team getTeam() { return team; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    public void setUser(User user) { this.user = user; }
    public void setTeam(Team team) { this.team = team; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
