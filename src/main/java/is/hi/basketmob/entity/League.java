package is.hi.basketmob.entity;

import javax.persistence.*;

@Entity
public class League {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;   // e.g., "NBA"
    private String season; // e.g., "2025"


    public League() {}
    public League(String name, String season) { this.name = name; this.season = season; }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSeason() { return season; }
    public void setName(String name) { this.name = name; }
    public void setSeason(String season) { this.season = season; }
}
