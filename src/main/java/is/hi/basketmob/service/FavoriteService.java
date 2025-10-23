package is.hi.basketmob.service;

import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.entity.Favorite;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.entity.User;
import is.hi.basketmob.repository.FavoriteRepository;
import is.hi.basketmob.repository.TeamRepository;
import is.hi.basketmob.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final FavoriteRepository favorites;
    private final UserRepository users;
    private final TeamRepository teams;

    public FavoriteService(FavoriteRepository favorites, UserRepository users, TeamRepository teams) {
        this.favorites = favorites;
        this.users = users;
        this.teams = teams;
    }

    @Transactional
    public TeamDto follow(Long userId, Long teamId) {
        User u = users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));
        Team t = teams.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TEAM_NOT_FOUND"));

        if (favorites.findByUserIdAndTeamId(userId, teamId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ALREADY_FAVORITED");
        }
        favorites.save(new Favorite(u, t));
        return new TeamDto(t.getId(), t.getName());
    }

    @Transactional
    public void unfollow(Long userId, Long teamId) {
        Favorite f = favorites.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FAVORITE_NOT_FOUND"));
        favorites.delete(f);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> list(Long userId) {
        // optional: verify user exists
        users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND"));

        return favorites.findByUserId(userId).stream()
                .map(f -> new TeamDto(f.getTeam().getId(), f.getTeam().getName()))
                .collect(Collectors.toList());
    }
}
