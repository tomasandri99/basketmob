package is.hi.basketmob.controller;

import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class FavoriteController {
    private final FavoriteService favorites;
    public FavoriteController(FavoriteService favorites) {
        this.favorites = favorites;
    }

    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<TeamDto>> list(@PathVariable Long userId) {
        return ResponseEntity.ok(favorites.list(userId));
    }

    // Use query param so you can do /api/v1/users/{id}/favorites?teamId=1
    @PostMapping("/{userId}/favorites")
    public ResponseEntity<TeamDto> follow(@PathVariable Long userId,
                                          @RequestParam Long teamId) {
        return ResponseEntity.ok(favorites.follow(userId, teamId));
    }

    @DeleteMapping("/{userId}/favorites/{teamId}")
    public ResponseEntity<Void> unfollow(@PathVariable Long userId,
                                         @PathVariable Long teamId) {
        favorites.unfollow(userId, teamId);
        return ResponseEntity.noContent().build();
    }
}
