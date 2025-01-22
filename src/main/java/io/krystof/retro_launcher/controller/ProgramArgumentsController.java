package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.jpa.repositories.ProgramLaunchArgumentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/program-arguments")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class ProgramArgumentsController {

    private final ProgramLaunchArgumentRepository launchArgumentRepository;

    public ProgramArgumentsController(ProgramLaunchArgumentRepository launchArgumentRepository) {
        this.launchArgumentRepository = launchArgumentRepository;
    }

    @GetMapping("/distinct")
    public ResponseEntity<Map<String, List<String>>> getDistinctArguments() {
        try {
            // Get distinct values and their groups
            List<Object[]> results =  launchArgumentRepository.findDistinctArguments();

            // Group results by argument group
            Map<String, List<String>> groupedArguments = new HashMap<>();

            for (Object[] result : results) {
                String argGroup = (String) result[0];
                String argValue = (String) result[1];

                groupedArguments.computeIfAbsent(
                        argGroup != null ? argGroup : "other",
                        k -> new ArrayList<>()
                ).add(argValue);
            }

            return ResponseEntity.ok(groupedArguments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
