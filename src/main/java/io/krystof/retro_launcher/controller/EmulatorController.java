package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.model.EmulatorStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${frontend.allowed.origins}")
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
