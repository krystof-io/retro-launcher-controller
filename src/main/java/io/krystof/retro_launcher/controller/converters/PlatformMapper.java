package io.krystof.retro_launcher.controller.converters;

import io.krystof.retro_launcher.controller.dto.PlatformDTO;
import io.krystof.retro_launcher.controller.jpa.entities.Platform;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {PlatformBinaryMapper.class})
public interface PlatformMapper {
    @Mapping(target = "binaries", source = "binaries")
    PlatformDTO toDto(Platform entity);

    @Mapping(target = "binaries", source = "binaries")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Platform toEntity(PlatformDTO dto);

    List<PlatformDTO> toDtoList(List<Platform> entities);
    Set<PlatformDTO> toDtoSet(Set<Platform> entities);
}
