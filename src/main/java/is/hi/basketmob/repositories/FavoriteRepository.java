package is.hi.basketmob.repository;

import is.hi.basketmob.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Repository for Favorite entities. */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser_Id(Long userId);
    Optional<Favorite> findByUser_IdAndTeam_Id(Long userId, Long teamId);
}
