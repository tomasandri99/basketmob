package is.hi.basketmob.controller;

import is.hi.basketmob.dto.StandingDto;
import is.hi.basketmob.service.StandingsProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leagues")
public class StandingsController {
    private final StandingsProvider standings;
    public StandingsController(StandingsProvider standings) {
        this.standings = standings;
    }

    @GetMapping("/{id}/standings")
    public ResponseEntity<List<StandingDto>> standings(@PathVariable("id") Long leagueId,
                                                       @RequestParam String season) {
        return ResponseEntity.ok(standings.getStandings(leagueId, season));
    }
}
