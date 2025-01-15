package io.krystof.retro_launcher.controller.converters;


import io.krystof.retro_launcher.controller.dto.ProgramDTO;
import io.krystof.retro_launcher.controller.jpa.entities.Author;
import io.krystof.retro_launcher.controller.jpa.entities.Program;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {
        AuthorMapper.class,
        PlatformMapper.class,
        ProgramDiskImageMapper.class,
        ProgramLaunchArgumentMapper.class,
        PlatformBinaryMapper.class,
        PlatformBinaryLaunchArgumentMapper.class,
        PlaybackTimelineEventMapper.class
})
public interface ProgramMapper {
    @Mapping(target = "authors", source = "authors")
    @Mapping(target = "platform", source = "platform")
    @Mapping(target = "diskImages", source = "diskImages")
    @Mapping(target = "launchArguments", source = "launchArguments")
    @Mapping(target = "platformBinary", source = "platformBinary")
    @Mapping(target="playbackTimelineEvents", source="playbackTimelineEvents")
    ProgramDTO toDto(Program entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Program toEntity(ProgramDTO dto);

    List<ProgramDTO> toDtoList(List<Program> entities);
    Set<ProgramDTO> toDtoSet(Set<Program> entities);
}