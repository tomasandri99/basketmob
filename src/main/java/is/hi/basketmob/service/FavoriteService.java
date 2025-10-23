package is.hi.basketmob.service;

import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.entity.Favorite;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.entities.User;
import is.hi.basketmob.repository.FavoriteRepository;
import is.hi.basketmob.repositories.UserRepository;
import is.hi.basketmob.repository.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

/** Service for managing user favorite teams (follow/unfollow functionality). */
@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepo;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public FavoriteService(FavoriteRepository favoriteRepo,
                           UserRepository userRepository,
                           TeamRepository teamRepository) {
        this.favoriteRepo = favoriteRepo;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Follow a team on behalf of a user.
     * @param actorUserId ID of the logged-in user performing the action
     * @param actorIsAdmin whether the actor has admin rights
     * @param targetUserId the ID of the user who will follow the team (should match actorUserId unless admin)
     * @param teamId the ID of the team to follow
     * @return TeamDto of the team that was followed
     */
    @Transactional
    public TeamDto follow(Long actorUserId, boolean actorIsAdmin, Long targetUserId, Long teamId) {
        // Authorization check: only the user themselves (or an admin) can add a favorite for that user
        if (!(actorIsAdmin || actorUserId.equals(targetUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot follow teams for this user");
        }
        // Verify user exists
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        // Verify team exists
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
        // Prevent duplicate favorite
        favoriteRepo.findByUser_IdAndTeam_Id(targetUserId, teamId).ifPresent(fav -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Team is already followed");
        });
        // Create and save new Favorite
        Favorite fav = new Favorite(user, team);
        favoriteRepo.save(fav);
        // Return the followed team info as DTO
        return new TeamDto(team.getId(), team.getName());
    }

    /**
     * Unfollow a team for a user.
     * @param actorUserId ID of logged-in user performing the action
     * @param actorIsAdmin whether the actor has admin rights
     * @param targetUserId the ID of the user who will unfollow the team
     * @param teamId the ID of the team to unfollow
     */
    @Transactional
    public void unfollow(Long actorUserId, boolean actorIsAdmin, Long targetUserId, Long teamId) {
        if (!(actorIsAdmin || actorUserId.equals(targetUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot unfollow teams for this user");
        }
        Favorite fav = favoriteRepo.findByUser_IdAndTeam_Id(targetUserId, teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found (user is not following this team)"));
        favoriteRepo.delete(fav);
    }

    /**
     * List all teams followed by the given user.
     * @param actorUserId ID of logged-in user performing the request
     * @param actorIsAdmin admin flag
     * @param targetUserId the user whose favorites to list
     * @return List of TeamDto representing teams the user follows
     */
    @Transactional(readOnly = true)
    public List<TeamDto> listFavorites(Long actorUserId, boolean actorIsAdmin, Long targetUserId) {
        if (!(actorIsAdmin || actorUserId.equals(targetUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view favorites for this user");
        }
        // Ensure user exists (optional check)
        userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        // Fetch favorites and map to TeamDto list
        List<Favorite> favs = favoriteRepo.findByUser_Id(targetUserId);
        return favs.stream()
                .map(fav -> {
                    Team team = fav.getTeam();
                    return new TeamDto(team.getId(), team.getName());
                })
                .collect(Collectors.toList());
    }
}
