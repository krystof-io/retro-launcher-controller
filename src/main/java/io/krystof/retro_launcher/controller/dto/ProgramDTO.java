package io.krystof.retro_launcher.controller.dto;

import io.krystof.retro_launcher.model.ContentRating;
import io.krystof.retro_launcher.model.CurationStatus;
import io.krystof.retro_launcher.model.ProgramType;
import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Data
public class ProgramDTO {
    private Long id;
    private String title;
    private ProgramType type;
    private Integer releaseYear;
    private String description;
    private ContentRating contentRating;
    private CurationStatus curationStatus;
    private String curatorNotes;
    private OffsetDateTime lastRunAt;
    private Integer runCount;
    private String sourceUrl;
    private Double sourceRating;
    private String sourceId;
    private List<AuthorDTO> authors;
    private PlatformDTO platform;
    private PlatformBinaryDTO platformBinary;
    private List<ProgramDiskImageDTO> diskImages;
    private List<ProgramLaunchArgumentDTO> launchArguments;
    private List<PlaybackTimelineEventDTO> playbackTimelineEvents;
    private BigDecimal avgMusicScore;
    private BigDecimal avgGraphicsScore;
    private BigDecimal avgVibesScore;
    private Integer totalVotes;
    private BigDecimal mgvIndex;
    private String tier;
}