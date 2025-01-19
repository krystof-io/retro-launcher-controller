package io.krystof.retro_launcher.controller;


import io.krystof.retro_launcher.controller.jpa.entities.Author;
import io.krystof.retro_launcher.controller.jpa.entities.Program;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramRepository;
import io.krystof.retro_launcher.controller.resolvers.CommandLineArgumentResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/curate")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class CurateController {

    private static final Logger logger = LoggerFactory.getLogger(CurateController.class);

    private final ProgramRepository programRepository;
    private final EmulatorAgentService agentService;

    public CurateController(ProgramRepository programRepository, EmulatorAgentService agentService) {
        this.programRepository = programRepository;
        this.agentService = agentService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> curateProgram(@PathVariable Long id) {
        logger.info("Launch request received for program ID: {}", id);

        // Fetch program with all necessary details
        return programRepository.findById(id)
                .map(program -> {
                    // Build launch configuration
                    Map<String, Object> config = buildLaunchConfig(program);

                    // Send to agent
                    try {
                        return ResponseEntity.ok(curateProgramOnAgent(config));
                    } catch (Exception e) {
                        logger.error("Error launching program: ", e);
                        return ResponseEntity.internalServerError().body(Map.of(
                                "error", "Failed to launch program",
                                "details", e.getMessage()
                        ));
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> buildLaunchConfig(Program program) {
        Map<String, Object> config = new HashMap<>();

        // Get the platform binary details
        config.put("binary", program.getPlatformBinary().getName());

        config.put("command_line_args", CommandLineArgumentResolver.buildCommandLineArgsWithoutBinaryOrDiskImage(program));

        // Convert disk images to required format
        List<Map<String, Object>> images = new ArrayList<>();
        if (program.getDiskImages() != null) {
            program.getDiskImages().stream()
                    .sorted((d1, d2) -> d1.getDiskNumber().compareTo(d2.getDiskNumber()))
                    .forEach(disk -> {
                        Map<String, Object> imageConfig = new HashMap<>();
                        imageConfig.put("disk_number", disk.getDiskNumber());
                        imageConfig.put("file_hash", disk.getFileHash());
                        imageConfig.put("storage_path", disk.getStoragePath());
                        imageConfig.put("size", disk.getFileSize());
                        images.add(imageConfig);
                    });
        }
        config.put("images", images);

        config.put("platform_name", program.getPlatform().getName());
        config.put("program_title", program.getTitle());
        config.put("program_type", program.getType().toString());
        config.put("authors", program.getAuthors().stream().map(Author::getName).toArray());

        logger.info("Built launch configuration: {}", config);
        return config;
    }

    private Map<String, Object> curateProgramOnAgent(Map<String, Object> config) {
        ResponseEntity<Map<String, Object>> response = agentService.postToAgent("/program/curate", config);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Agent service returned error: " + response.getBody());
        }

        return response.getBody();
    }
}
