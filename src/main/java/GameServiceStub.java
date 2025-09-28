package is.hi.basketmob;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GameServiceStub implements GameService {

    @Override
    public GameDto getGame(Long id) {
        if (Objects.equals(id, 1L)) {
            TeamDto home = new TeamDto(10L, "Valur");
            TeamDto away = new TeamDto(11L, "KR");
            return new GameDto(
                    1L, "FINAL",
                    OffsetDateTime.now().minusDays(1).toString(),
                    home, away,
                    89, 82
            );
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "GAME_NOT_FOUND");
    }

    @Override
    public List<GameListItemDto> listByDate(LocalDate date, int page, int size, String sort) {
        List<GameListItemDto> list = new ArrayList<>();
        list.add(new GameListItemDto(
                1L,
                date.atTime(19,30).atOffset(OffsetDateTime.now().getOffset()).toString(),
                "SCHEDULED", "Valur", "KR"));
        list.add(new GameListItemDto(
                2L,
                date.atTime(20,0).atOffset(OffsetDateTime.now().getOffset()).toString(),
                "SCHEDULED", "Keflavík", "FH"));
        return list;
    }
}
