package is.hi.basketmob.controller;

import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.security.AuthenticatedUser;
import is.hi.basketmob.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v1/me/favorites")
@Validated
public class FavoriteController {
    private final FavoriteService favorites;

    public FavoriteController(FavoriteService favorites) {
        this.favorites = favorites;
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> list(@AuthenticationPrincipal AuthenticatedUser actor) {
        return ResponseEntity.ok(favorites.list(actor.getId()));
    }

    @PostMapping
    public ResponseEntity<TeamDto> follow(@AuthenticationPrincipal AuthenticatedUser actor,
                                          @RequestParam @Positive Long teamId) {
        return ResponseEntity.ok(favorites.follow(actor.getId(), teamId));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> unfollow(@AuthenticationPrincipal AuthenticatedUser actor,
                                         @PathVariable @Positive Long teamId) {
        favorites.unfollow(actor.getId(), teamId);
        return ResponseEntity.noContent().build();
    }
}
