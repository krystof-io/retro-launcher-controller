package io.krystof.retro_launcher.controller.converters;

import io.krystof.retro_launcher.controller.dto.ProgramDiskImageDTO;
import io.krystof.retro_launcher.controller.jpa.entities.ProgramDiskImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProgramDiskImageMapper {
    ProgramDiskImageDTO toDto(ProgramDiskImage entity);

    @Mapping(target = "program", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProgramDiskImage toEntity(ProgramDiskImageDTO dto);

    List<ProgramDiskImageDTO> toDtoList(List<ProgramDiskImage> entities);
    Set<ProgramDiskImageDTO> toDtoSet(Set<ProgramDiskImage> entities);
}