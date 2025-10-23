package is.hi.basketmob.controller;

import is.hi.basketmob.dto.FavoriteRequest;
import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.service.AuthService;
import is.hi.basketmob.service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/favorites")
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final AuthService authService;

    public FavoriteController(FavoriteService favoriteService, AuthService authService) {
        this.favoriteService = favoriteService;
        this.authService = authService;
    }

    /** Follow a team (add to favorites). */
    @PostMapping
    public ResponseEntity<TeamDto> followTeam(@PathVariable("userId") Long userId,
                                              @Valid @RequestBody FavoriteRequest request,
                                              @RequestHeader("Authorization") String authHeader) {
        // Expect header "Authorization: Bearer <token>"
        String token = extractToken(authHeader);
        Long actorId = authService.validateToken(token);
        TeamDto result = favoriteService.follow(actorId, false, userId, request.teamId);
        return ResponseEntity.status(201).body(result);
    }

    /** Unfollow a team (remove from favorites). */
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> unfollowTeam(@PathVariable("userId") Long userId,
                                             @PathVariable Long teamId,
                                             @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        Long actorId = authService.validateToken(token);
        favoriteService.unfollow(actorId, false, userId, teamId);
        return ResponseEntity.noContent().build();
    }

    /** Get list of teams the user is following. */
    @GetMapping
    public ResponseEntity<List<TeamDto>> getFavorites(@PathVariable("userId") Long userId,
                                                      @RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        Long actorId = authService.validateToken(token);
        List<TeamDto> teams = favoriteService.listFavorites(actorId, false, userId);
        return ResponseEntity.ok(teams);
    }

    /** Utility: Extract the token string from the "Authorization" header value. */
    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If no token provided, treat as unauthorized
            throw new RuntimeException("Authorization token missing or malformed");
        }
        return authHeader.substring("Bearer ".length()).trim();
    }
}
