package is.hi.basketmob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import is.hi.basketmob.entity.League;

public interface LeagueRepository extends JpaRepository<League, Long> {}
