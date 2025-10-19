package is.hi.basketmob.controller;

import is.hi.basketmob.dto.GameDto;
import is.hi.basketmob.dto.GameListItemDto;
import is.hi.basketmob.service.GameService;

import java.time.LocalDate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
@Validated
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Get a single game by id.
     * Returns 200 with the game DTO, or lets a 404 bubble up from the service
     * (e.g., EntityNotFoundException handled by your @ControllerAdvice).
     */
    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }

    /**
     * List games for a specific date with paging and sorting.
     * Example: GET /api/v1/games?date=2025-10-19&page=0&size=20&sort=tipoff,asc
     */
    @GetMapping
    public ResponseEntity<Page<GameListItemDto>> listByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(defaultValue = "0")
            @Min(0) int page,

            @RequestParam(defaultValue = "20")
            @Min(1) @Max(100) int size,

            // e.g. "tipoff,asc" or "id,desc"
            @RequestParam(defaultValue = "tipoff,asc")
            String sort
    ) {
        return ResponseEntity.ok(gameService.listByDate(date, page, size, sort));
    }
}
