package is.hi.basketmob.entity;

import javax.persistence.*;

@Entity
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private League league;

    public Team() {}
    public Team(String name, League league) { this.name = name; this.league = league; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public League getLeague() { return league; }
    public void setName(String name) { this.name = name; }
    public void setLeague(League league) { this.league = league; }
}
