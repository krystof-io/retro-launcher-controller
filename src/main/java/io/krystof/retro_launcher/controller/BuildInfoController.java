package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.model.EmulatorStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class BuildInfoController {

    public BuildInfoController(BuildProperties buildProperties, EmulatorAgentService agentService) {
        this.agentService = agentService;
        this.buildProperties = buildProperties;
    }

    private final EmulatorAgentService agentService;
    private final BuildProperties buildProperties;

    @GetMapping("/build-info")
    public Map<String, Object> getBuildInfo() {
        EmulatorStatus agentStatus = agentService.getCurrentStatus();

        return Map.of(
                "controllerVersion", buildProperties.getVersion()+"-"+buildProperties.getTime(),
                "agentVersion", agentStatus.getVersion()
        );
    }
}