package io.krystof.retro_launcher.controller.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(force = true)
@Value
public class VoteRequestDTO {
    Long programId;

    int musicScore;

    int graphicsScore;

    int vibesScore;

    String comment;
}