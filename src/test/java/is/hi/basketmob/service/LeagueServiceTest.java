package is.hi.basketmob.service;

import is.hi.basketmob.dto.StandingDto;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.League;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.repository.GameRepository;
import is.hi.basketmob.repository.LeagueRepository;
import is.hi.basketmob.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeagueServiceTest {

    @Mock
    private LeagueRepository leagueRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private GameRepository gameRepository;

    private LeagueService service;

    @BeforeEach
    void setUp() {
        service = new LeagueService(leagueRepository, teamRepository, gameRepository);
    }

    @Test
    void calculatesStandingsWithExtendedStats() {
        League league = new League();
        league.setSeason("2025");
        ReflectionTestUtils.setField(league, "id", 1L);

        Team valur = new Team();
        valur.setName("Valur");
        ReflectionTestUtils.setField(valur, "id", 100L);

        Team kr = new Team();
        kr.setName("KR");
        ReflectionTestUtils.setField(kr, "id", 200L);

        when(leagueRepository.findById(1L)).thenReturn(Optional.of(league));
        when(teamRepository.findByLeagueId(1L)).thenReturn(List.of(valur, kr));

        Game g1 = finalGame(league, valur, kr, 88, 80);
        Game g2 = finalGame(league, kr, valur, 70, 85);
        when(gameRepository.findByLeagueIdAndStatus(1L, Game.Status.FINAL)).thenReturn(List.of(g1, g2));

        List<StandingDto> standings = service.getStandings(1L, "2025");

        assertThat(standings).hasSize(2);
        StandingDto leader = standings.get(0);
        assertThat(leader.teamName()).isEqualTo("Valur");
        assertThat(leader.wins()).isEqualTo(2);
        assertThat(leader.losses()).isZero();
        assertThat(leader.gamesPlayed()).isEqualTo(2);
        assertThat(leader.pointsFor()).isEqualTo(173);
        assertThat(leader.pointsAgainst()).isEqualTo(150);
        assertThat(leader.winPct()).isEqualTo(1.0d);

        StandingDto runnerUp = standings.get(1);
        assertThat(runnerUp.teamName()).isEqualTo("KR");
        assertThat(runnerUp.wins()).isZero();
        assertThat(runnerUp.losses()).isEqualTo(2);
        assertThat(runnerUp.pointsFor()).isEqualTo(150);
    }

    @Test
    void rejectsSeasonMismatch() {
        League league = new League();
        league.setSeason("2025");
        ReflectionTestUtils.setField(league, "id", 1L);
        when(leagueRepository.findById(1L)).thenReturn(Optional.of(league));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.getStandings(1L, "2024"));
        assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private Game finalGame(League league, Team home, Team away, int homeScore, int awayScore) {
        Game game = new Game();
        game.setLeague(league);
        game.setHomeTeam(home);
        game.setAwayTeam(away);
        game.setTipoff(LocalDateTime.now());
        game.setStatus(Game.Status.FINAL);
        game.setHomeScore(homeScore);
        game.setAwayScore(awayScore);
        return game;
    }
}
