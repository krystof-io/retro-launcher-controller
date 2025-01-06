package io.krystof.retro_launcher.controller.converters;

import io.krystof.retro_launcher.controller.dto.ProgramLaunchArgumentDTO;
import io.krystof.retro_launcher.controller.jpa.entities.ProgramLaunchArgument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProgramLaunchArgumentMapper {

    ProgramLaunchArgumentDTO toDto(ProgramLaunchArgument entity);

    @Mapping(target = "program", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProgramLaunchArgument toEntity(ProgramLaunchArgumentDTO dto);

    List<ProgramLaunchArgumentDTO> toDtoList(List<ProgramLaunchArgument> entities);
    Set<ProgramLaunchArgumentDTO> toDtoSet(Set<ProgramLaunchArgument> entities);
}
