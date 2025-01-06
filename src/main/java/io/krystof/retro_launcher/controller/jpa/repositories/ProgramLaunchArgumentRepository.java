package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.ProgramLaunchArgument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramLaunchArgumentRepository extends JpaRepository<ProgramLaunchArgument, Long> {
    List<ProgramLaunchArgument> findByProgramIdOrderByArgumentOrder(Long programId);
}
