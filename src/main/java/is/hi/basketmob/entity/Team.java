package is.hi.basketmob.entity;

import javax.persistence.*;

@Entity
public class Team {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(length = 50)
    private String shortName;
    @Column(length = 120)
    private String city;
    @Column(length = 500)
    private String logoUrl;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private League league;

    public Team() {}
    public Team(String name, League league) { this.name = name; this.league = league; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getShortName() { return shortName; }
    public String getCity() { return city; }
    public String getLogoUrl() { return logoUrl; }
    public League getLeague() { return league; }
    public void setName(String name) { this.name = name; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public void setCity(String city) { this.city = city; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public void setLeague(League league) { this.league = league; }
}
