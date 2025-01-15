package io.krystof.retro_launcher.controller.converters;

import io.krystof.retro_launcher.controller.dto.PlatformBinaryDTO;
import io.krystof.retro_launcher.controller.dto.PlatformBinaryLaunchArgumentDTO;
import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinary;
import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinaryLaunchArgument;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {PlatformMapper.class, PlatformBinaryLaunchArgumentMapper.class})
public interface PlatformBinaryMapper {

    @Mapping(target = "launchArguments", source = "launchArguments")
    PlatformBinaryDTO toDto(PlatformBinary entity);

    //@Mapping(target = "launchArguments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PlatformBinary toEntity(PlatformBinaryDTO dto);

    List<PlatformBinaryDTO> toDtoList(List<PlatformBinary> entities);
    Set<PlatformBinaryDTO> toDtoSet(Set<PlatformBinary> entities);

    default Set<PlatformBinary> toEntitySet(List<PlatformBinaryDTO> dtos) {
        if (dtos == null) {
            return null;
        }

        Set<PlatformBinary> set = new HashSet<>();
        for (PlatformBinaryDTO dto : dtos) {
            set.add(toEntity(dto));
        }
        return set;
    }

}
