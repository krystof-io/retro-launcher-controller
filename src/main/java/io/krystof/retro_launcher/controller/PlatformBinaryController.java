package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.converters.PlatformBinaryLaunchArgumentMapper;
import io.krystof.retro_launcher.controller.converters.PlatformBinaryMapper;
import io.krystof.retro_launcher.controller.dto.PlatformBinaryDTO;
import io.krystof.retro_launcher.controller.jpa.entities.Platform;
import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinary;
import io.krystof.retro_launcher.controller.jpa.repositories.PlatformBinaryRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.PlatformRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping("/api/platform-binaries")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class PlatformBinaryController {

    private final PlatformBinaryRepository binaryRepository;
    private final PlatformRepository platformRepository;
    private final PlatformBinaryMapper binaryMapper;
    private final PlatformBinaryLaunchArgumentMapper launchArgumentMapper;

    public PlatformBinaryController(
            PlatformBinaryRepository binaryRepository,
            PlatformRepository platformRepository,
            PlatformBinaryMapper binaryMapper,
            PlatformBinaryLaunchArgumentMapper launchArgumentMapper) {
        this.binaryRepository = binaryRepository;
        this.platformRepository = platformRepository;
        this.binaryMapper = binaryMapper;
        this.launchArgumentMapper = launchArgumentMapper;
    }

    @GetMapping("/platform/{platformId}")
    public List<PlatformBinaryDTO> getBinariesForPlatform(@PathVariable Long platformId) {
        return binaryMapper.toDtoList(binaryRepository.findByPlatformId(platformId));
    }

    @GetMapping("/{id}")
    public PlatformBinaryDTO getBinaryById(@PathVariable Long id) {
        return binaryRepository.findById(id)
                .map(binaryMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Binary not found"
                ));
    }

    @PostMapping("/platform/{platformId}")
    @Transactional
    public ResponseEntity<PlatformBinaryDTO> createBinary(
            @PathVariable Long platformId,
            @RequestBody PlatformBinaryDTO binaryDTO) {

        Platform platform = platformRepository.findById(platformId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Platform not found"
                ));

        PlatformBinary binary = binaryMapper.toEntity(binaryDTO);
        binary.setPlatform(platform);

        // If this is the first binary or marked as default, ensure it's the only default
        if (binary.isDefault() || binaryRepository.countByPlatformId(platformId) == 0) {
            binaryRepository.clearDefaultForPlatform(platformId);
        }

        PlatformBinary savedBinary = binaryRepository.save(binary);
        return new ResponseEntity<>(
                binaryMapper.toDto(savedBinary),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<PlatformBinaryDTO> updateBinary(
            @PathVariable Long id,
            @RequestBody PlatformBinaryDTO binaryDTO) {

        return binaryRepository.findById(id)
                .map(existingBinary -> {
                    // Update basic fields
                    existingBinary.setName(binaryDTO.getName());
                    existingBinary.setVariant(binaryDTO.getVariant());
                    existingBinary.setDescription(binaryDTO.getDescription());

                    // Handle default status
                    if (binaryDTO.isDefault() && !existingBinary.isDefault()) {
                        binaryRepository.clearDefaultForPlatform(existingBinary.getPlatform().getId());
                    }
                    existingBinary.setDefault(binaryDTO.isDefault());

//                    // Update launch arguments
                    existingBinary.getLaunchArguments().clear();
                    existingBinary.getLaunchArguments().addAll(launchArgumentMapper.toEntitySet(binaryDTO.getLaunchArguments()));
                    existingBinary.getLaunchArguments().forEach(arg -> arg.setPlatformBinary(existingBinary));

                    PlatformBinary updatedBinary = binaryRepository.save(existingBinary);
                    return ResponseEntity.ok(binaryMapper.toDto(updatedBinary));
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Binary not found"
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBinary(@PathVariable Long id) {
        return binaryRepository.findById(id)
                .map(binary -> {
                    // If this was the default binary, make another one default
                    if (binary.isDefault()) {
                        binaryRepository.findFirstByPlatformIdAndIdNot(binary.getPlatform().getId(), id)
                                .ifPresent(newDefault -> {
                                    newDefault.setDefault(true);
                                    binaryRepository.save(newDefault);
                                });
                    }
                    binaryRepository.delete(binary);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Binary not found"
                ));
    }
}