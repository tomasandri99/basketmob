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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import is.hi.basketmob.entity.Game;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/games")
@Validated
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<GameDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }


    @GetMapping
    public ResponseEntity<Page<GameListItemDto>> list(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateTo,

            @RequestParam(required = false)
            Long leagueId,

            @RequestParam(required = false)
            Game.Status status,

            @RequestParam(defaultValue = "0")
            @Min(0) int page,

            @RequestParam(defaultValue = "20")
            @Min(1) @Max(100) int size,


            @RequestParam(defaultValue = "tipoff,asc")
            String sort
    ) {
        validateDateRange(dateFrom, dateTo);
        return ResponseEntity.ok(
                gameService.listGames(
                        date,
                        dateFrom,
                        dateTo,
                        leagueId,
                        status,
                        page,
                        size,
                        sort
                ));
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dateFrom must be before dateTo");
        }
    }
}
