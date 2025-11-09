package is.hi.basketmob.service;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameListItemDto;
import is.hi.basketmob.entity.Game;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface GameService {
    GameDto getGame(Long id);
    Page<GameListItemDto> listGames(LocalDate date,
                                    LocalDate dateFrom,
                                    LocalDate dateTo,
                                    Long leagueId,
                                    Game.Status status,
                                    int page,
                                    int size,
                                    String sort);
}
