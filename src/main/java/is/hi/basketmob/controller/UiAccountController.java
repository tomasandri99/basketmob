package is.hi.basketmob.controller;

import is.hi.basketmob.security.AuthenticatedUser;
import is.hi.basketmob.service.FavoriteService;
import is.hi.basketmob.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiAccountController {

    private final FavoriteService favoriteService;
    private final NotificationService notificationService;

    public UiAccountController(FavoriteService favoriteService,
                               NotificationService notificationService) {
        this.favoriteService = favoriteService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AuthenticatedUser actor, Model model) {
        if (actor == null) {
            return "redirect:/login";
        }
        model.addAttribute("favorites", favoriteService.list(actor.getId()));
        model.addAttribute("notifications", notificationService.list(actor.getId()));
        return "dashboard";
    }
}
