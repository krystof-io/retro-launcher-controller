package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.converters.ProgramMapper;
import io.krystof.retro_launcher.controller.dto.ProgramDTO;
import io.krystof.retro_launcher.controller.jpa.entities.Program;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramRepository;
import io.krystof.retro_launcher.controller.jpa.specifications.ProgramSpecifications;
import io.krystof.retro_launcher.model.ContentRating;
import io.krystof.retro_launcher.model.CurationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/programs")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class ProgramSearchController {

    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;

    public ProgramSearchController(ProgramRepository programRepository, ProgramMapper programMapper) {
        this.programRepository = programRepository;
        this.programMapper = programMapper;
    }


    @GetMapping("/search")
    public Page<ProgramDTO> searchPrograms(
            @RequestParam(required = false) String titleSearch,
            @RequestParam(required = false) Long platformId,
            @RequestParam(required = false) String curationStatus,
            @RequestParam(required = false) String contentRating,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortField,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        // Create base specification
        Specification<Program> spec = Specification.where(ProgramSpecifications.withEagerLoading());

        // Add search criteria
        if (titleSearch != null && !titleSearch.trim().isEmpty()) {
            spec = spec.and(ProgramSpecifications.titleContains(titleSearch.trim()));
        }

        if (platformId != null) {
            spec = spec.and(ProgramSpecifications.withPlatform(platformId));
        }

        if (curationStatus != null && !curationStatus.isEmpty()) {
            try {
                CurationStatus status = CurationStatus.valueOf(curationStatus);
                spec = spec.and(ProgramSpecifications.withCurationStatus(status));
            } catch (IllegalArgumentException ignored) {}
        }

        if (contentRating != null && !contentRating.isEmpty()) {
            try {
                ContentRating rating = ContentRating.valueOf(contentRating);
                spec = spec.and(ProgramSpecifications.withContentRating(rating));
            } catch (IllegalArgumentException ignored) {}
        }

        if (authorId != null) {
            spec = spec.and(ProgramSpecifications.byAuthorId(authorId));
        }

        if (yearFrom != null) {
            spec = spec.and(ProgramSpecifications.releaseYearAfter(yearFrom));
        }

        if (yearTo != null) {
            spec = spec.and(ProgramSpecifications.releaseYearBefore(yearTo));
        }

        // Create sort
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(direction, sortField);

        // Execute search with pagination
        Page<Program> results = programRepository.findAll(spec, PageRequest.of(page, size, sort));

        // Map to DTOs
        return results.map(programMapper::toDto);
    }
}