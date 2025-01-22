package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.ProgramLaunchArgument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramLaunchArgumentRepository extends JpaRepository<ProgramLaunchArgument, Long> {
    List<ProgramLaunchArgument> findByProgramIdOrderByArgumentOrder(Long programId);

    @Query("SELECT DISTINCT pla.argumentGroup, pla.argumentValue FROM ProgramLaunchArgument pla ORDER BY pla.argumentGroup, pla.argumentValue")
    List<Object[]> findDistinctArguments();


}
