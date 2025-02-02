package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.dto.VoteRequestDTO;
import io.krystof.retro_launcher.controller.dto.VoteResponseDTO;
import io.krystof.retro_launcher.controller.jpa.entities.Program;
import io.krystof.retro_launcher.controller.jpa.entities.ProgramVote;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramVoteRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.VoteAggregates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@Transactional
@Slf4j
public class VoteService {
    private final ProgramVoteRepository voteRepository;
    private final ProgramRepository programRepository;

    public VoteService(ProgramVoteRepository voteRepository, ProgramRepository programRepository) {
        this.voteRepository = voteRepository;
        this.programRepository = programRepository;
    }

    public VoteResponseDTO submitVote(VoteRequestDTO request, String userId, String platformId) {
        Program program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found"));

        // Check for existing vote
        ProgramVote vote = voteRepository.findByProgramIdAndUserId(request.getProgramId(), userId)
                .orElse(ProgramVote.builder()
                        .programId(request.getProgramId())
                        .userId(userId)
                        .build());


        vote.setPlatformId(platformId); // Update platform ID in case user voted from different platform
        vote.setMusicScore(request.getMusicScore());
        vote.setGraphicsScore(request.getGraphicsScore());
        vote.setVibesScore(request.getVibesScore());
        vote.setComment(request.getComment());

        voteRepository.save(vote);

        // Calculate new averages
        VoteAggregates aggregates = voteRepository.getVoteAggregates(request.getProgramId());

        // Update program scores
        program.setAvgMusicScore(BigDecimal.valueOf(aggregates.getAvgMusicScore()));
        program.setAvgGraphicsScore(BigDecimal.valueOf(aggregates.getAvgGraphicsScore()));
        program.setAvgVibesScore(BigDecimal.valueOf(aggregates.getAvgVibesScore()));
        program.setTotalVotes((int) aggregates.getTotalVotes());

        // Calculate MGV index (this will also set the tier via @PreUpdate)
        calculateAndSetMGVIndex(program);

        program = programRepository.save(program);

        return VoteResponseDTO.builder()
                .programId(program.getId())
                .title(program.getTitle())
                .avgMusicScore(program.getAvgMusicScore())
                .avgGraphicsScore(program.getAvgGraphicsScore())
                .avgVibesScore(program.getAvgVibesScore())
                .mgvIndex(program.getMgvIndex())
                .tier(program.getTier())
                .totalVotes(program.getTotalVotes())
                .build();
    }

    private void calculateAndSetMGVIndex(Program program) {
        // Only calculate if we have all scores
        if (program.getAvgMusicScore() != null &&
                program.getAvgGraphicsScore() != null &&
                program.getAvgVibesScore() != null) {

            // Calculate base score (average of M, G, V)
            BigDecimal baseScore = program.getAvgMusicScore()
                    .add(program.getAvgGraphicsScore())
                    .add(program.getAvgVibesScore())
                    .divide(BigDecimal.valueOf(3), RoundingMode.HALF_UP);

            // Calculate confidence factor (0.5 to 1.0 based on vote count)
            BigDecimal confidenceFactor = BigDecimal.valueOf(0.5)
                    .add(BigDecimal.valueOf(program.getTotalVotes())
                            .divide(BigDecimal.valueOf(20), 2, RoundingMode.HALF_UP))
                    .min(BigDecimal.ONE);
            if (program.getTotalVotes() == 1) {
                //assume I am the first voter, so I am very confident
                confidenceFactor = BigDecimal.ONE;
            }

            // Calculate final index (scale to 0-100)
            BigDecimal mgvIndex = baseScore
                    .multiply(confidenceFactor)
                    .multiply(BigDecimal.valueOf(20))
                    .setScale(2, RoundingMode.HALF_UP);

            program.setMgvIndex(mgvIndex);
        }
    }
}
