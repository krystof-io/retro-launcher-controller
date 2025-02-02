package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.ProgramVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramVoteRepository extends JpaRepository<ProgramVote, Long> {
    @Query("""
        SELECT new io.krystof.retro_launcher.controller.jpa.repositories.VoteAggregates(
            AVG(v.musicScore),
            AVG(v.graphicsScore),
            AVG(v.vibesScore),
            COUNT(v)
        )
        FROM ProgramVote v
        WHERE v.programId = :programId
        GROUP BY v.programId
    """)
    VoteAggregates getVoteAggregates(Long programId);

    Optional<ProgramVote> findByProgramIdAndUserId(Long programId, String userId);
}