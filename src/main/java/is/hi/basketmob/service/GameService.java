
package is.hi.basketmob.service;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameListItemDto;
import java.time.LocalDate;
import org.springframework.data.domain.Page;

public interface GameService {
    GameDto getGame(Long id);
    Page<GameListItemDto> listByDate(LocalDate date, int page, int size, String sort);
}



