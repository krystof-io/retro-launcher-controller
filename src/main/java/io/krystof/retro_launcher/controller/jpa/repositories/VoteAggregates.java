package io.krystof.retro_launcher.controller.jpa.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

@Data
@AllArgsConstructor
public class VoteAggregates {
    double avgMusicScore;
    double avgGraphicsScore;
    double avgVibesScore;
    long totalVotes;
}
