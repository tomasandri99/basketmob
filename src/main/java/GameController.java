package is.hi.basketmob;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {

    private final GameService service;

    public GameController(GameService service) { this.service = service; }

    @GetMapping("/{id}")
    public GameDto getGame(@PathVariable Long id) {
        return service.getGame(id);
    }

    @GetMapping
    public List<GameListItemDto> getGamesByDate(
            @RequestParam String date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "tipoff,asc") String sort) {

        return service.listByDate(java.time.LocalDate.parse(date), page, size, sort);
    }
}
