package is.hi.basketmob.repository;

import is.hi.basketmob.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    Page<Game> findByTipoffBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    List<Game> findByLeagueIdAndStatus(Long leagueId, Game.Status status);
}
