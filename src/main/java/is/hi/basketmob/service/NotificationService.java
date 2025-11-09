package is.hi.basketmob.service;

import is.hi.basketmob.entity.Favorite;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.Notification;
import is.hi.basketmob.notification.GameUpdateObserver;
import is.hi.basketmob.notification.GameUpdatePublisher;
import is.hi.basketmob.repository.FavoriteRepository;
import is.hi.basketmob.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService implements GameUpdateObserver {

    private final NotificationRepository notifications;
    private final FavoriteRepository favorites;

    public NotificationService(NotificationRepository notifications,
                               FavoriteRepository favorites,
                               GameUpdatePublisher publisher) {
        this.notifications = notifications;
        this.favorites = favorites;
        publisher.register(this);
    }

    @Override
    @Transactional
    public void onGameFinal(Game game) {
        List<Long> teamIds = List.of(
                game.getHomeTeam().getId(),
                game.getAwayTeam().getId()
        );

        List<Favorite> followers = favorites.findByTeamIdIn(teamIds);
        if (followers.isEmpty()) {
            return;
        }

        String message = buildMessage(game);
        for (Favorite follower : followers) {
            notifications.save(new Notification(follower.getUser(), game, message));
        }
    }

    private String buildMessage(Game game) {
        String vs = game.getHomeTeam().getName() + " vs " + game.getAwayTeam().getName();
        String score = (game.getHomeScore() != null && game.getAwayScore() != null)
                ? " (" + game.getHomeScore() + "-" + game.getAwayScore() + ")"
                : "";
        return "Final: " + vs + score;
    }
}
