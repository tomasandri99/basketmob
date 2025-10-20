package is.hi.basketmob.controller;

import is.hi.basketmob.dto.StandingRowDto;
import is.hi.basketmob.service.StandingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

@RestController
@RequestMapping("/api/v1/leagues")
public class StandingsController {

    private final StandingsService standings;

    public StandingsController(StandingsService standings) {
        this.standings = standings;
    }

    @GetMapping("/{id}/standings")
    public ResponseEntity<List<StandingRowDto>> standings(
            @PathVariable("id") @Min(1) Long leagueId,
            @RequestParam(defaultValue = "2025")
            @Pattern(regexp="\\d{4}") String season
    ) {
        return ResponseEntity.ok(standings.standingsForLeague(leagueId, season));
    }
}
