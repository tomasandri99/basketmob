package is.hi.basketmob.service;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameUpdateRequest;
import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.notification.GameUpdatePublisher;
import is.hi.basketmob.repository.GameRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@Profile("!stub")
public class GameCommandService {

    private final GameRepository games;
    private final GameUpdatePublisher publisher;

    public GameCommandService(GameRepository games, GameUpdatePublisher publisher) {
        this.games = games;
        this.publisher = publisher;
    }

    @Transactional
    public GameDto updateGame(Long id, GameUpdateRequest request) {
        Game game = games.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GAME_NOT_FOUND"));

        if (request.getStatus() == Game.Status.FINAL &&
                (request.getHomeScore() == null || request.getAwayScore() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Scores required when marking game as FINAL");
        }

        if (request.getHomeScore() != null) {
            game.setHomeScore(request.getHomeScore());
        }
        if (request.getAwayScore() != null) {
            game.setAwayScore(request.getAwayScore());
        }
        if (request.getStatus() != null) {
            game.setStatus(request.getStatus());
        }

        Game saved = games.save(game);

        if (saved.getStatus() == Game.Status.FINAL) {
            publisher.notifyFinal(saved);
        }

        return toDto(saved);
    }

    private GameDto toDto(Game g) {
        TeamDto homeDto = toTeamDto(g.getHomeTeam());
        TeamDto awayDto = toTeamDto(g.getAwayTeam());

        var tip = g.getTipoff();
        String date = tip.toLocalDate().toString();
        String time = tip.toLocalTime()
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        return new GameDto(
                g.getId(), date, time,
                homeDto, awayDto,
                g.getHomeScore(), g.getAwayScore()
        );
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
}
