package io.krystof.retro_launcher.controller.converters;

import io.krystof.retro_launcher.controller.dto.PlatformBinaryLaunchArgumentDTO;
import io.krystof.retro_launcher.controller.dto.ProgramLaunchArgumentDTO;
import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinaryLaunchArgument;
import io.krystof.retro_launcher.controller.jpa.entities.ProgramLaunchArgument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PlatformBinaryLaunchArgumentMapper {

    PlatformBinaryLaunchArgumentDTO toDto(PlatformBinaryLaunchArgument entity);

    @Mapping(target = "platformBinary", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PlatformBinaryLaunchArgument toEntity(PlatformBinaryLaunchArgumentDTO dto);

    List<PlatformBinaryLaunchArgumentDTO> toDtoList(List<PlatformBinaryLaunchArgument> entities);
    Set<PlatformBinaryLaunchArgumentDTO> toDtoSet(Set<PlatformBinaryLaunchArgument> entities);

    // Convert List<DTO> to Set<Entity>
    default Set<PlatformBinaryLaunchArgument> toEntitySet(List<PlatformBinaryLaunchArgumentDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        Set<PlatformBinaryLaunchArgument> set = new HashSet<>();
        for (PlatformBinaryLaunchArgumentDTO dto : dtos) {
            set.add(toEntity(dto));
        }
        return set;
    }
}
