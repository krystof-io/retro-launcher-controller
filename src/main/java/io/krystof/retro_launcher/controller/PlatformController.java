package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.converters.PlatformMapper;
import io.krystof.retro_launcher.controller.dto.PlatformDTO;
import io.krystof.retro_launcher.controller.jpa.repositories.PlatformRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/platforms")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class PlatformController {

    private final PlatformRepository platformRepository;
    private final PlatformMapper platformMapper;

    public PlatformController(PlatformRepository platformRepository, PlatformMapper platformMapper) {
        this.platformRepository = platformRepository;
        this.platformMapper = platformMapper;
    }

    @GetMapping
    public List<PlatformDTO> getAllPlatforms() {
        return platformMapper.toDtoList(platformRepository.findAll());
    }
}
