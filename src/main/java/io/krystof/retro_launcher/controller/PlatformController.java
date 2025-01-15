package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.converters.PlatformBinaryMapper;
import io.krystof.retro_launcher.controller.converters.PlatformMapper;
import io.krystof.retro_launcher.controller.dto.PlatformDTO;
import io.krystof.retro_launcher.controller.jpa.entities.Platform;
import io.krystof.retro_launcher.controller.jpa.repositories.PlatformRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController

@CrossOrigin(origins = "${frontend.allowed.origins}")
public class PlatformController {

    private final PlatformRepository platformRepository;
    private final PlatformMapper platformMapper;
    private final PlatformBinaryMapper platformBinaryMapper;

    public PlatformController(PlatformRepository platformRepository, PlatformMapper platformMapper,
                              PlatformBinaryMapper platformBinaryMapper) {
        this.platformRepository = platformRepository;
        this.platformMapper = platformMapper;
        this.platformBinaryMapper = platformBinaryMapper;
    }

    @GetMapping("/api/platforms")
    public List<PlatformDTO> getAllPlatforms() {
        return platformMapper.toDtoList(platformRepository.findAll());
    }

    @GetMapping("/api/platform/{id}")
    public PlatformDTO getPlatformById(@PathVariable Long id) {
        return platformRepository.findById(id)
                .map(platformMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Platform not found"
                ));
    }

    @PostMapping("/api/platform")
    public ResponseEntity<PlatformDTO> createPlatform(@RequestBody PlatformDTO platformDTO) {
        // Ensure we're creating a new platform
        if (platformDTO.getId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "New platform cannot have an ID"
            );
        }

        // Convert DTO to entity, save, and convert back to DTO
        Platform platform = platformMapper.toEntity(platformDTO);
        Platform savedPlatform = platformRepository.save(platform);
        return new ResponseEntity<>(
                platformMapper.toDto(savedPlatform),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/api/platform/{id}")
    @Transactional(propagation = Propagation.REQUIRED)
    public ResponseEntity<PlatformDTO> updatePlatform(
            @PathVariable Long id,
            @RequestBody PlatformDTO platformDTO) {

        // Verify ID matches
        if (!id.equals(platformDTO.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Path ID must match platform ID in body"
            );
        }

        return platformRepository.findById(id)
                .map(existingPlatform -> {
                    // Update fields from DTO
                    existingPlatform.setName(platformDTO.getName());
                    existingPlatform.setDescription(platformDTO.getDescription());

                    // Handle platform binaries update
                    if (platformDTO.getBinaries() != null) {
                        existingPlatform.getBinaries().clear();
                        existingPlatform.getBinaries().addAll(
                                platformBinaryMapper.toEntitySet(platformDTO.getBinaries())
                        );
                        existingPlatform.getBinaries().forEach(binary-> binary.getLaunchArguments().forEach(launchArgument -> launchArgument.setPlatformBinary(binary)));
                        existingPlatform.getBinaries().forEach(binary -> binary.setPlatform(existingPlatform));
                    }

                    // Save and return updated platform
                    Platform updatedPlatform = platformRepository.save(existingPlatform);
                    return ResponseEntity.ok(platformMapper.toDto(updatedPlatform));
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Platform not found"
                ));
    }

    @DeleteMapping("/api/platform/{id}")
    public ResponseEntity<Void> deletePlatform(@PathVariable Long id) {
        return platformRepository.findById(id)
                .map(platform -> {
                    platformRepository.delete(platform);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Platform not found"
                ));
    }

    // Optional: Add validation method if needed
    private void validatePlatform(PlatformDTO platformDTO) {
        if (platformDTO.getName() == null || platformDTO.getName().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Platform name is required"
            );
        }

        // Add more validation as needed
        // e.g., check for duplicate names, validate binary configurations, etc.
    }
}
