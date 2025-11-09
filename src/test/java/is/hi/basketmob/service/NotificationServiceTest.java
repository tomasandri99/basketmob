package is.hi.basketmob.service;

import is.hi.basketmob.entity.Favorite;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.League;
import is.hi.basketmob.entity.Notification;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.entity.User;
import is.hi.basketmob.notification.GameUpdatePublisher;
import is.hi.basketmob.repository.FavoriteRepository;
import is.hi.basketmob.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private FavoriteRepository favoriteRepository;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(notificationRepository, favoriteRepository, new GameUpdatePublisher());
    }

    @Test
    void createsNotificationForFollowersWhenGameEnds() {
        Team valur = team(10L, "Valur");
        Team kr = team(20L, "KR");
        Game finalGame = new Game();
        finalGame.setLeague(new League());
        finalGame.setHomeTeam(valur);
        finalGame.setAwayTeam(kr);
        finalGame.setTipoff(LocalDateTime.now());
        finalGame.setStatus(Game.Status.FINAL);
        finalGame.setHomeScore(90);
        finalGame.setAwayScore(82);

        Favorite adminFavorite = new Favorite(user(1L, "admin@basketmob.is"), valur);
        Favorite tomasFavorite = new Favorite(user(2L, "tomas@example.com"), kr);
        when(favoriteRepository.findByTeamIdIn(anyList())).thenReturn(List.of(adminFavorite, tomasFavorite));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.onGameFinal(finalGame);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(2)).save(captor.capture());
        List<Notification> saved = captor.getAllValues();
        assertThat(saved).hasSize(2);
        assertThat(saved.get(0).getMessage()).contains("Valur");
        assertThat(saved.get(0).isReadFlag()).isFalse();
    }

    private Team team(Long id, String name) {
        Team team = new Team();
        team.setName(name);
        ReflectionTestUtils.setField(team, "id", id);
        return team;
    }

    private User user(Long id, String email) {
        User user = new User();
        user.setEmail(email);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}
