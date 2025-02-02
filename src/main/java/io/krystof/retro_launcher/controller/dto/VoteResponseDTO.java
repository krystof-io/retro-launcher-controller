package io.krystof.retro_launcher.controller.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class VoteResponseDTO {
    Long programId;
    String title;
    BigDecimal avgMusicScore;
    BigDecimal avgGraphicsScore;
    BigDecimal avgVibesScore;
    BigDecimal mgvIndex;
    String tier;
    int totalVotes;
}
