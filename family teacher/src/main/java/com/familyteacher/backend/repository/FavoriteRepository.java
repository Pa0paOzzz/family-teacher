package com.familyteacher.backend.repository;

import com.familyteacher.backend.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);
    List<Favorite> findByUserIdAndResourceType(Long userId, String resourceType);
    Optional<Favorite> findByUserIdAndResourceIdAndResourceType(Long userId, Long resourceId, String resourceType);
    void deleteByUserIdAndResourceIdAndResourceType(Long userId, Long resourceId, String resourceType);
}
