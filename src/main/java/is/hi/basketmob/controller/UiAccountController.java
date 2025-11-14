package is.hi.basketmob.controller;

import is.hi.basketmob.dto.UserUpdateRequest;
import is.hi.basketmob.security.AuthenticatedUser;
import is.hi.basketmob.service.FavoriteService;
import is.hi.basketmob.service.NotificationService;
import is.hi.basketmob.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
@Controller
@RequestMapping("/dashboard")
public class UiAccountController {

    private final UserService userService;
    private final FavoriteService favoriteService;
    private final NotificationService notificationService;

    public UiAccountController(UserService userService,
                               FavoriteService favoriteService,
                               NotificationService notificationService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
        this.notificationService = notificationService;
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal AuthenticatedUser actor,
                                @Valid @ModelAttribute("profileForm") UserUpdateRequest form,
                                BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (actor == null) {
            return "redirect:/login";
        }
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("profileForm", form);
            redirectAttributes.addFlashAttribute("error", "Inntak þarf að vera gilt áður en hægt er að vista prófíl.");
            return "redirect:/dashboard";
        }
        try {
            userService.updateUser(actor.getId(), form, actor.getId(), actor.isAdmin(), false);
            redirectAttributes.addFlashAttribute("toast", "Prófíl uppfærður.");
        } catch (ResponseStatusException ex) {
            redirectAttributes.addFlashAttribute("profileForm", form);
            redirectAttributes.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/favorites")
    public String followTeam(@AuthenticationPrincipal AuthenticatedUser actor,
                             @RequestParam Long teamId,
                             RedirectAttributes redirectAttributes) {
        if (actor == null) {
            return "redirect:/login";
        }
        try {
            favoriteService.follow(actor.getId(), teamId);
            redirectAttributes.addFlashAttribute("toast", "Lið bætt við uppáhaldslista.");
        } catch (ResponseStatusException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/favorites/{teamId}/delete")
    public String unfollow(@AuthenticationPrincipal AuthenticatedUser actor,
                           @PathVariable Long teamId,
                           RedirectAttributes redirectAttributes) {
        if (actor == null) {
            return "redirect:/login";
        }
        try {
            favoriteService.unfollow(actor.getId(), teamId);
            redirectAttributes.addFlashAttribute("toast", "Lið fjarlægt af uppáhaldslista.");
        } catch (ResponseStatusException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getReason());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/notifications/clear")
    public String clearNotifications(@AuthenticationPrincipal AuthenticatedUser actor,
                                     RedirectAttributes redirectAttributes) {
        if (actor == null) {
            return "redirect:/login";
        }
        notificationService.clear(actor.getId());
        redirectAttributes.addFlashAttribute("toast", "Tilkynningar hreinsaðar.");
        return "redirect:/dashboard";
    }

}
