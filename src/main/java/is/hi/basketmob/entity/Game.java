package is.hi.basketmob.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Game {
    public enum Status { SCHEDULED, LIVE, FINAL }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private League league;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Team homeTeam;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Team awayTeam;

    private LocalDateTime tipoff;

    @Enumerated(EnumType.STRING)
    private Status status = Status.SCHEDULED;

    private Integer homeScore; // nullable until FINAL/LIVE
    private Integer awayScore;

    public Game() {}
    public Game(League league, Team homeTeam, Team awayTeam, LocalDateTime tipoff, Status status, Integer homeScore, Integer awayScore) {
        this.league = league;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.tipoff = tipoff;
        this.status = status;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public Long getId() { return id; }
    public League getLeague() { return league; }
    public Team getHomeTeam() { return homeTeam; }
    public Team getAwayTeam() { return awayTeam; }
    public LocalDateTime getTipoff() { return tipoff; }
    public Status getStatus() { return status; }
    public Integer getHomeScore() { return homeScore; }
    public Integer getAwayScore() { return awayScore; }

    public void setLeague(League league) { this.league = league; }
    public void setHomeTeam(Team homeTeam) { this.homeTeam = homeTeam; }
    public void setAwayTeam(Team awayTeam) { this.awayTeam = awayTeam; }
    public void setTipoff(LocalDateTime tipoff) { this.tipoff = tipoff; }
    public void setStatus(Status status) { this.status = status; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
}
