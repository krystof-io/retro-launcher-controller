package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.ProgramDiskImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramDiskImageRepository extends JpaRepository<ProgramDiskImage, Long> {
    List<ProgramDiskImage> findByProgramIdOrderByDiskNumber(Long programId);
    Optional<ProgramDiskImage> findByFileHash(String fileHash);
}