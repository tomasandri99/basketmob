package is.hi.basketmob.service;

import is.hi.basketmob.dto.StandingRowDto;
import java.util.List;

public interface StandingsService {
    List<StandingRowDto> standingsForLeague(Long leagueId, String season);
}
