package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.model.EmulatorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class EmulatorController {

    private final EmulatorAgentService agentService;
    private static final Logger logger = LoggerFactory.getLogger(EmulatorController.class);

    public EmulatorController(EmulatorAgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("/status")
    public EmulatorStatus getStatus() {
        return agentService.getCurrentStatus();
    }

    @PostMapping("/program/stop")
    public ResponseEntity<?> stop() {
        ResponseEntity<Map<String, Object>> response = agentService.postToAgent("/program/stop", "{\"force\": true}");

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Agent service returned error: " + response.getBody());
        }

        return ResponseEntity.ok(response.getBody());
    }

    @PostMapping("/program/command")
    public ResponseEntity<Map<String, Object>> executeCommand(@RequestBody Map<String, Object> command) {
        logger.info("Executing command: {}", command);

        try {
            return agentService.postToAgent("/program/command", command);
        } catch (Exception e) {
            logger.error("Failed to execute command: ", e);
            throw new RuntimeException("Failed to execute command", e);
        }
    }

}
