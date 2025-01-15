package io.krystof.retro_launcher.controller.jpa.repositories;

import io.krystof.retro_launcher.controller.jpa.entities.PlaybackTimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaybackTimelineEventRepository extends JpaRepository<PlaybackTimelineEvent, Long> {

    // Find all commands for a program ordered by execution order
    @Query("SELECT pc FROM PlaybackTimelineEvent pc " +
            "WHERE pc.program.id = :programId " +
            "ORDER BY pc.sequenceNumber")
    List<PlaybackTimelineEvent> findByProgramIdOrdered(Long programId);

    // Find next command after a given order
    @Query("SELECT pc FROM PlaybackTimelineEvent pc " +
            "WHERE pc.program.id = :programId " +
            "AND pc.sequenceNumber > :currentOrder " +
            "ORDER BY pc.sequenceNumber ASC " +
            "LIMIT 1")
    PlaybackTimelineEvent findNextCommand(Long programId, Integer currentOrder);
}
