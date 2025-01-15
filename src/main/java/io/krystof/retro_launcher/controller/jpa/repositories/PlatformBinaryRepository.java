package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlatformBinaryRepository extends JpaRepository<PlatformBinary, Long> {
    // Find all binaries for a platform
    List<PlatformBinary> findByPlatformId(Long platformId);

    // Find default binary for a platform
    Optional<PlatformBinary> findByPlatformIdAndIsDefaultTrue(Long platformId);

    // Find first non-default binary for a platform (used when changing default)
    Optional<PlatformBinary> findFirstByPlatformIdAndIdNot(Long platformId, Long binaryId);

    // Count binaries for a platform
    long countByPlatformId(Long platformId);

    // Clear default flag for all binaries of a platform
    @Modifying
    @Query("UPDATE PlatformBinary b SET b.isDefault = false WHERE b.platform.id = :platformId")
    void clearDefaultForPlatform(Long platformId);

    // Find binary by name for a platform
    Optional<PlatformBinary> findByPlatformIdAndName(Long platformId, String name);

    // Get all binaries with their launch arguments
    @Query("SELECT DISTINCT b FROM PlatformBinary b LEFT JOIN FETCH b.launchArguments WHERE b.platform.id = :platformId")
    List<PlatformBinary> findByPlatformIdWithLaunchArguments(Long platformId);

    // Get a single binary with its launch arguments
    @Query("SELECT b FROM PlatformBinary b LEFT JOIN FETCH b.launchArguments WHERE b.id = :id")
    Optional<PlatformBinary> findByIdWithLaunchArguments(Long id);

    // Check if name is unique within platform (excluding current binary)
    boolean existsByPlatformIdAndNameAndIdNot(Long platformId, String name, Long binaryId);

    // Check if platform has any default binary
    boolean existsByPlatformIdAndIsDefaultTrue(Long platformId);

    // Delete all binaries for a platform
    void deleteByPlatformId(Long platformId);
}