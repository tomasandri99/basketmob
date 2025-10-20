package is.hi.basketmob.service.impl;

import is.hi.basketmob.dto.StandingRowDto;
import is.hi.basketmob.service.StandingsService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StandingsServiceStub implements StandingsService {
    @Override
    public List<StandingRowDto> standingsForLeague(Long leagueId, String season) {
        // simple, deterministic sample
        var rows = new ArrayList<StandingRowDto>(List.of(
                new StandingRowDto(1L, "Valur", 3, 1, true),
                new StandingRowDto(2L, "KR",    3, 2, true),
                new StandingRowDto(3L, "FH",    2, 2, true),
                new StandingRowDto(4L, "ÍR",    1, 3, true)
        ));
        rows.sort(Comparator.<StandingRowDto>comparingInt(StandingRowDto::wins).reversed()
                .thenComparingInt(StandingRowDto::losses));
        return rows;
    }
}
