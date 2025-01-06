package io.krystof.retro_launcher.controller.converters;

import io.krystof.retro_launcher.controller.dto.AuthorDTO;
import io.krystof.retro_launcher.controller.jpa.entities.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    AuthorDTO toDto(Author entity);

    @Mapping(target = "programs", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Author toEntity(AuthorDTO dto);

    List<AuthorDTO> toDtoList(List<Author> entities);
    Set<AuthorDTO> toDtoSet(Set<Author> entities);
}
