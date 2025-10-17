package pakki.service.impl;

import pakki.dto.*;
import pakki.service.GameService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service // temporary impl so the app runs now
public class GameServiceStub implements GameService {

    @Override
    public GameDto getGame(Long id) {
        return new GameDto(
                id, "FINAL", "2025-10-01T19:00:00Z",
                new TeamDto(1L, "KR"), new TeamDto(2L, "Valur"),
                89, 82
        );
    }

    @Override
    public Page<GameListItemDto> listByDate(LocalDate date, int page, int size, String sort) {
        var items = List.of(
                new GameListItemDto(1L, date.atTime(19,0).toString(), "SCHEDULED", "KR", "Valur"),
                new GameListItemDto(2L, date.atTime(21,0).toString(), "SCHEDULED", "Njárðvík", "Keflavík")
        );
        return new PageImpl<>(items, PageRequest.of(page, size), items.size());
    }
}

