package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.dto.ApiResponse;
import io.krystof.retro_launcher.controller.dto.VoteRequestDTO;
import io.krystof.retro_launcher.controller.dto.VoteResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/votes")
@Validated
@Slf4j
public class VoteController {
    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VoteResponseDTO>> submitVote(
            @RequestBody @Validated VoteRequestDTO request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Platform-Id") String platformId) {

        log.info("Received vote from user {} via {}: {}", userId, platformId, request);

        try {
            VoteResponseDTO response = voteService.submitVote(request, userId, platformId);
            return ResponseEntity.ok(ApiResponse.success(response, "Vote recorded successfully"));
        } catch (ResponseStatusException e) {
            throw e;  // Let @RestControllerAdvice handle it
        } catch (Exception e) {
            log.error("Error processing vote", e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to process vote"
            );
        }
    }

    // Could add additional endpoints here:
    // - GET /api/votes/program/{programId} - Get vote statistics
    // - GET /api/votes/user/{userId}/program/{programId} - Get user's vote
    // - DELETE /api/votes/{voteId} - Delete a vote (with proper authorization)
}
