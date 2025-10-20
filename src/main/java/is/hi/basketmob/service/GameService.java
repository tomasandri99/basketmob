package is.hi.basketmob.service;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameListItemDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface GameService {
    GameDto getGame(Long id);
    Page<GameListItemDto> listByDate(LocalDate date, int page, int size, String sort);
}
