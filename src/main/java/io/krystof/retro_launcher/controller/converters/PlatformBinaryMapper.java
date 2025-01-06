package io.krystof.retro_launcher.controller.converters;

import io.krystof.retro_launcher.controller.dto.PlatformBinaryDTO;
import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {PlatformMapper.class, PlatformBinaryLaunchArgumentMapper.class})
public interface PlatformBinaryMapper {
    @Mapping(target = "launchArguments", source = "launchArguments")
    PlatformBinaryDTO toDto(PlatformBinary entity);

    @Mapping(target = "launchArguments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PlatformBinary toEntity(PlatformBinaryDTO dto);

    List<PlatformBinaryDTO> toDtoList(List<PlatformBinary> entities);
    Set<PlatformBinaryDTO> toDtoSet(Set<PlatformBinary> entities);
}
