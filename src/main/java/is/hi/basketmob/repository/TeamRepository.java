package is.hi.basketmob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import is.hi.basketmob.entity.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByLeagueId(Long leagueId);
}
