package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    Optional<Platform> findByName(String name);

    // Add if you need to check for duplicate names
    boolean existsByNameAndIdNot(String name, Long id);

    // Add if you need platforms with binaries
    @Query("SELECT p FROM Platform p LEFT JOIN FETCH p.binaries WHERE p.id = :id")
    Optional<Platform> findByIdWithBinaries(@Param("id") Long id);
}
