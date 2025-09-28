package pakki.service;

import pakki.dto.*;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;


public interface GameService {
    GameDto getGame(Long id);
    Page<GameListItemDto> listByDate(LocalDate date, int page, int size, String sort);
}

