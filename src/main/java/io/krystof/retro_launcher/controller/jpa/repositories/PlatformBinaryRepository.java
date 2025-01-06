package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformBinaryRepository extends JpaRepository<PlatformBinary, Long> {
    List<PlatformBinary> findByPlatformId(Long platformId);
    Optional<PlatformBinary> findByPlatformIdAndIsDefaultTrue(Long platformId);
}