package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.model.EmulatorStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class EmulatorController {

    @GetMapping(path = "/status")
    public Mono<EmulatorStatus> getStatus() {
        EmulatorStatus es = new EmulatorStatus();
        es.setRunning(new Random().nextBoolean());
        return Mono.just(es);
    }
}
