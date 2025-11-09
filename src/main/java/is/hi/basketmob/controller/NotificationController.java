package is.hi.basketmob.controller;

import is.hi.basketmob.dto.NotificationDto;
import is.hi.basketmob.security.AuthenticatedUser;
import is.hi.basketmob.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me/notifications")
public class NotificationController {

    private final NotificationService notifications;

    public NotificationController(NotificationService notifications) {
        this.notifications = notifications;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> list(@AuthenticationPrincipal AuthenticatedUser actor) {
        return ResponseEntity.ok(notifications.list(actor.getId()));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@AuthenticationPrincipal AuthenticatedUser actor,
                                         @PathVariable Long id) {
        notifications.markRead(actor.getId(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clear(@AuthenticationPrincipal AuthenticatedUser actor) {
        notifications.clear(actor.getId());
        return ResponseEntity.noContent().build();
    }
}
