package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.model.EmulatorStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class EmulatorController {

    private final EmulatorAgentService agentService;

    public EmulatorController(EmulatorAgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping("/status")
    public EmulatorStatus getStatus() {
        return agentService.getCurrentStatus();
    }

}
