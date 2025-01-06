package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {
    Optional<Platform> findByName(String name);
}
