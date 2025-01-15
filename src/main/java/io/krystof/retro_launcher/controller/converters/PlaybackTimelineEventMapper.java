package io.krystof.retro_launcher.controller.converters;

import io.krystof.retro_launcher.controller.dto.PlaybackTimelineEventDTO;
import io.krystof.retro_launcher.controller.jpa.entities.PlaybackTimelineEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlaybackTimelineEventMapper {
    PlaybackTimelineEventDTO toDto(PlaybackTimelineEvent entity);

    @Mapping(target = "program", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PlaybackTimelineEvent toEntity(PlaybackTimelineEventDTO dto);
    List<PlaybackTimelineEventDTO> toDtoList(List<PlaybackTimelineEvent> entities);
}