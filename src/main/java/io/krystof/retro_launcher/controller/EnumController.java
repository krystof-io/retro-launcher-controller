package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.model.ContentRating;
import io.krystof.retro_launcher.model.CurationStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class EnumController {

    @GetMapping("/content-ratings")
    public List<String> getContentRatings() {
        return Arrays.stream(ContentRating.values())
                .map(ContentRating::name)
                .collect(Collectors.toList());
    }

    @GetMapping("/curation-statuses")
    public List<String> getCurationStatuses() {
        return Arrays.stream(CurationStatus.values())
                .map(CurationStatus::name)
                .collect(Collectors.toList());
    }
}