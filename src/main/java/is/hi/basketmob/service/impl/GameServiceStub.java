package is.hi.basketmob.service.impl;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameListItemDto;
import is.hi.basketmob.dto.TeamDto;
import is.hi.basketmob.service.GameService;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@Service
@Primary
public class GameServiceStub implements GameService {


    private static final class Game {
        final Long id;
        final LocalDate date;
        final LocalTime tipoff;
        final String homeTeam;
        final String awayTeam;
        final Integer homeScore;
        final Integer awayScore;
        final String status;

        Game(Long id, LocalDate date, LocalTime tipoff,
             String homeTeam, String awayTeam,
             Integer homeScore, Integer awayScore,
             String status) {
            this.id = id;
            this.date = date;
            this.tipoff = tipoff;
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.homeScore = homeScore;
            this.awayScore = awayScore;
            this.status = status;
        }
    }

    private final List<Game> games = new ArrayList<>();

    public GameServiceStub() { seed(); }

    private void seed() {
        LocalDate today = LocalDate.now();
        games.add(new Game(1L, today,              LocalTime.of(19, 15), "Valur",      "KR",         null, null, "SCHEDULED"));
        games.add(new Game(2L, today,              LocalTime.of(20,  0), "FH",         "ÍR",         null, null, "SCHEDULED"));
        games.add(new Game(3L, today.minusDays(1), LocalTime.of(18, 30), "Keflavík",   "Grindavík",  86,   82,   "FINAL"));
        games.add(new Game(4L, today.plusDays(1),  LocalTime.of(19, 45), "Njarðvík",   "Tindastóll", null, null, "SCHEDULED"));
        games.add(new Game(5L, today,              LocalTime.of(18,  0), "Breiðablik", "Haukar",     null, null, "SCHEDULED"));
    }

    // --------------- GameService API ----------------

    @Override
    public GameDto getGame(Long id) {
        Game g = games.stream()
                .filter(x -> Objects.equals(x.id, id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("GAME_NOT_FOUND"));
        return toGameDto(g);
    }

    @Override
    public Page<GameListItemDto> listByDate(LocalDate date, int page, int size, String sort) {

        String sortField = "tipoff";
        boolean asc = true;
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",", 2);
            sortField = parts[0].trim().toLowerCase(Locale.ROOT);
            if (parts.length > 1) asc = !"desc".equalsIgnoreCase(parts[1]);
        }

        Comparator<Game> cmp = comparatorFor(sortField);
        if (!asc) cmp = cmp.reversed();

        List<Game> sameDay = games.stream()
                .filter(g -> g.date.equals(date))
                .sorted(cmp)
                .toList();


        page = Math.max(0, page);
        size = Math.max(1, size);
        int from = page * size;
        int to = Math.min(from + size, sameDay.size());

        List<Game> slice = (from >= sameDay.size()) ? List.of() : sameDay.subList(from, to);
        List<GameListItemDto> items = slice.stream().map(this::toGameListItemDto).toList();

        return new PageImpl<>(items, PageRequest.of(page, size), sameDay.size());
    }



    private Comparator<Game> comparatorFor(String sortField) {
        return switch (sortField) {
            case "id"     -> Comparator.comparing((Game g) -> g.id);
            case "status" -> Comparator.comparing((Game g) -> g.status)
                    .thenComparing(g -> g.tipoff);
            case "tipoff" -> Comparator.comparing((Game g) -> g.tipoff)
                    .thenComparing(g -> g.id);
            default       -> Comparator.comparing((Game g) -> g.tipoff)
                    .thenComparing(g -> g.id);
        };
    }


    private GameListItemDto toGameListItemDto(Game g) {

        return new GameListItemDto(
                g.id,
                g.homeTeam,
                g.awayTeam,
                g.status,
                g.tipoff.toString()
        );
    }


    private GameDto toGameDto(Game g) {
        TeamDto home = new TeamDto(g.homeTeam);
        TeamDto away = new TeamDto(g.awayTeam);

        return new GameDto(
                g.id,
                g.date.toString(),
                g.tipoff.toString(),
                home,
                away,
                g.homeScore,
                g.awayScore
        );
    }
}




