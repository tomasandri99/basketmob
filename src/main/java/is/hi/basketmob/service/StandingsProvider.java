package is.hi.basketmob.service;

import is.hi.basketmob.dto.StandingDto;

import java.util.List;

public interface StandingsProvider {
    List<StandingDto> getStandings(Long leagueId, String season);
}
