package is.hi.basketmob.service.impl;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameListItemDto;
import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.entity.Game;
import is.hi.basketmob.entity.Team;
import is.hi.basketmob.repository.GameRepository;
import is.hi.basketmob.service.GameService;
import org.springframework.context.annotation.Profile;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

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
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GAME_NOT_FOUND"));
        TeamDto home = toTeamDto(game.getHomeTeam());
        TeamDto away = toTeamDto(game.getAwayTeam());

        var tip = game.getTipoff();
        String date = tip.toLocalDate().toString();
        String time = tip.toLocalTime()
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        return new GameDto(
                game.getId(),
                date,
                time,
                game.getStatus().name(),
                home,
                away,
                game.getHomeScore(),
                game.getAwayScore(),
                game.getLeague().getName(),
                false
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "games",
            key = "T(java.util.Objects).hash(#date,#dateFrom,#dateTo,#leagueId,#status,#page,#size,#sort)")
    public Page<GameListItemDto> listGames(LocalDate date,
                                           LocalDate dateFrom,
                                           LocalDate dateTo,
                                           Long leagueId,
                                           Game.Status status,
                                           int page,
                                           int size,
                                           String sort) {
        Specification<Game> spec = Specification.where(alwaysTrue());

        if (date != null) {
            LocalDateTime from = date.atStartOfDay();
            LocalDateTime to = date.plusDays(1).atStartOfDay();
            spec = spec.and(tipoffBetween(from, to));
        } else {
            if (dateFrom != null) {
                spec = spec.and(tipoffGreaterOrEqual(dateFrom.atStartOfDay()));
            }
            if (dateTo != null) {
                spec = spec.and(tipoffLessThan(dateTo.plusDays(1).atStartOfDay()));
            }
        }

        if (date == null && dateFrom == null && dateTo == null) {
            LocalDate today = LocalDate.now();
            spec = spec.and(tipoffBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay()));
        }

        if (leagueId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("league").get("id"), leagueId));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<Game> pageGames = gameRepository.findAll(spec, pageable);

        return pageGames.map(this::toListItem);
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
        String time = game.getTipoff().toLocalTime()
                .truncatedTo(ChronoUnit.MINUTES)
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        return new GameListItemDto(
                game.getId(),
                time,
                game.getStatus().name(),
                game.getHomeTeam().getName(),
                game.getAwayTeam().getName()
        );
    }

    private Specification<Game> tipoffBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> cb.between(root.get("tipoff"), from, to);
    }

    private Specification<Game> tipoffGreaterOrEqual(LocalDateTime from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("tipoff"), from);
    }

    private Specification<Game> tipoffLessThan(LocalDateTime to) {
        return (root, query, cb) -> cb.lessThan(root.get("tipoff"), to);
    }

    private Specification<Game> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("tipoff").ascending();
        }
        String[] parts = sort.split(",", 2);
        String field = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1]))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }
}
