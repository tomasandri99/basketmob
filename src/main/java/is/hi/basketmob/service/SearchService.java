package is.hi.basketmob.service;

import is.hi.basketmob.dto.GameListItemDto;
import is.hi.basketmob.dto.SearchResponse;
import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.repository.GameRepository;
import is.hi.basketmob.repository.TeamRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final TeamRepository teams;
    private final GameRepository games;

    public SearchService(TeamRepository teams, GameRepository games) {
        this.teams = teams;
        this.games = games;
    }

    public SearchResponse search(String query, int limit) {
        String q = query.trim();
        int capped = Math.min(Math.max(limit, 1), 25);

        List<TeamDto> teamHits = teams.findTop10ByNameContainingIgnoreCase(q).stream()
                .limit(capped)
                .map(this::toTeamDto)
                .collect(Collectors.toList());

        List<GameListItemDto> gameHits = games
                .findTop10ByHomeTeam_NameContainingIgnoreCaseOrAwayTeam_NameContainingIgnoreCase(q, q).stream()
                .limit(capped)
                .map(this::toListItem)
                .collect(Collectors.toList());

        return new SearchResponse(teamHits, gameHits);
    }

    private TeamDto toTeamDto(Team team) {
        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getShortName(),
                team.getCity(),
                team.getLogoUrl()
        );
    }

    private GameListItemDto toListItem(Game game) {
        return new GameListItemDto(
                game.getId(),
                game.getTipoff().toLocalTime().format(TIME_FORMAT),
                game.getStatus().name(),
                game.getHomeTeam().getName(),
                game.getAwayTeam().getName()
        );
    }
}
