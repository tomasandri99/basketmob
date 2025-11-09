package is.hi.basketmob.service;

import is.hi.basketmob.dto.NotificationDto;
import is.hi.basketmob.entity.Favorite;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.Notification;
import is.hi.basketmob.notification.GameUpdateObserver;
import is.hi.basketmob.notification.GameUpdatePublisher;
import is.hi.basketmob.repository.FavoriteRepository;
import is.hi.basketmob.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

        String message = formatMessage(game);
        Set<Long> notifiedUsers = new HashSet<>();
        for (Favorite favorite : followers) {
            Long userId = favorite.getUser().getId();
            if (notifiedUsers.add(userId)) {
                Notification notification = new Notification(favorite.getUser(), game, message);
                notifications.save(notification);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> list(Long userId) {
        return notifications.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markRead(Long userId, Long notificationId) {
        Notification notification = notifications.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND"));
        notification.setReadFlag(true);
        notifications.save(notification);
    }

    @Transactional
    public void clear(Long userId) {
        notifications.deleteByUserId(userId);
    }

    private NotificationDto toDto(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getGame().getId(),
                notification.getMessage(),
                notification.isReadFlag(),
                notification.getCreatedAt()
        );
    }

    private String formatMessage(Game game) {
        Integer homeScore = game.getHomeScore() == null ? 0 : game.getHomeScore();
        Integer awayScore = game.getAwayScore() == null ? 0 : game.getAwayScore();
        return String.format(
                "%s %d - %d %s",
                game.getHomeTeam().getName(),
                homeScore,
                awayScore,
                game.getAwayTeam().getName()
        );
    }
}
