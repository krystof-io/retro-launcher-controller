package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.converters.ProgramMapper;
import io.krystof.retro_launcher.controller.dto.ProgramDTO;
import io.krystof.retro_launcher.controller.jpa.entities.Program;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/program")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class ProgramController {

    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;

    public ProgramController(ProgramRepository programRepository, ProgramMapper programMapper) {
        this.programRepository = programRepository;
        this.programMapper = programMapper;
    }

    @GetMapping("/{id}")
    public ProgramDTO getProgramById(@PathVariable Long id) {
        Program program = programRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found"));
        return programMapper.toDto(program);
    }
}
