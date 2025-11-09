package is.hi.basketmob.service;

import is.hi.basketmob.dto.StandingDto;

import java.util.List;

/**
 * Abstraction for retrieving league standings (allows caching/proxy layers).
 */
public interface StandingsProvider {
    List<StandingDto> getStandings(Long leagueId, String season);
}
