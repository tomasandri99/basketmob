package is.hi.basketmob.service.impl;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameListItemDto;
import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.repository.GameRepository;
import is.hi.basketmob.service.GameService;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
@Profile("!stub") // use this one unless you run with spring.profiles.active=stub
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public GameDto getGame(Long id) {
        Game g = gameRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GAME_NOT_FOUND"));
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

    @Override
    @Transactional(readOnly = true)
    public Page<GameListItemDto> listByDate(LocalDate date, int page, int size, String sort) {
        Pageable pageable;
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",", 2);
            String field = parts[0].trim();
            Sort.Direction dir = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1])) ? Sort.Direction.DESC : Sort.Direction.ASC;
            pageable = PageRequest.of(page, size, Sort.by(dir, field));
        } else {
            pageable = PageRequest.of(page, size, Sort.by("tipoff").ascending());
        }

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to   = date.plusDays(1).atStartOfDay();

        Page<Game> pageGames = gameRepository.findByTipoffBetween(from, to, pageable);

        return pageGames.map(g -> {
            String time = g.getTipoff().toLocalTime()
                    .truncatedTo(ChronoUnit.MINUTES)
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
            return new GameListItemDto(
                    g.getId(),
                    time,
                    g.getStatus().name(),
                    g.getHomeTeam().getName(),
                    g.getAwayTeam().getName()
            );
        });
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
