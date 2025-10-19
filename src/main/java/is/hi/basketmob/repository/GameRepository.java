package is.hi.basketmob.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import is.hi.basketmob.entity.Game;

import java.time.LocalDateTime;

public interface GameRepository extends JpaRepository<Game, Long> {
    Page<Game> findByTipoffBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<Game> findByTipoffBetweenAndLeagueId(LocalDateTime start, LocalDateTime end, Long leagueId, Pageable pageable);
}
