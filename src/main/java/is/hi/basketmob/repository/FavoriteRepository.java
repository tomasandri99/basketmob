package is.hi.basketmob.repository;

import is.hi.basketmob.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserIdAndTeamId(Long userId, Long teamId);
    List<Favorite> findByTeamIdIn(List<Long> teamIds);
}
