package is.hi.basketmob;

import java.time.LocalDate;
import java.util.List;

public interface GameService {
    GameDto getGame(Long id);
    List<GameListItemDto> listByDate(LocalDate date, int page, int size, String sort);
}
