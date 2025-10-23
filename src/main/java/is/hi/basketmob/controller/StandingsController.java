package is.hi.basketmob.controller;

import is.hi.basketmob.dto.StandingDto;
import is.hi.basketmob.service.LeagueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leagues")
public class StandingsController {
    private final LeagueService leagueService;
    public StandingsController(LeagueService leagueService) {
        this.leagueService = leagueService;
    }

    @GetMapping("/{id}/standings")
    public ResponseEntity<List<StandingDto>> standings(@PathVariable("id") Long leagueId,
                                                       @RequestParam String season) {
        return ResponseEntity.ok(leagueService.getStandings(leagueId, season));
    }
}
