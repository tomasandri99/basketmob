package is.hi.basketmob.controller;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameListItemDto;
import is.hi.basketmob.service.GameService;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    private final GameService gameService;
    public GameController(GameService gameService) { this.gameService = gameService; }

    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }

    @GetMapping
    public ResponseEntity<Page<GameListItemDto>> listByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,
            @RequestParam(defaultValue="tipoff,asc") String sort) {
        return ResponseEntity.ok(gameService.listByDate(date, page, size, sort));
    }
}

