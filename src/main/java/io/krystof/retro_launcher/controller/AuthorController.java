package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.converters.AuthorMapper;
import io.krystof.retro_launcher.controller.dto.AuthorDTO;
import io.krystof.retro_launcher.controller.jpa.repositories.AuthorRepository;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class AuthorController {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    public AuthorController(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @GetMapping
    public List<AuthorDTO> searchAuthors(@RequestParam(required = false) String search) {
        if (search == null || search.trim().isEmpty()) {
            return authorMapper.toDtoList(
                    authorRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
            );
        }

        // We need to add a search method to the repository
        return authorMapper.toDtoList(
                authorRepository.findByNameContainingIgnoreCase(search.trim())
        );
    }
}