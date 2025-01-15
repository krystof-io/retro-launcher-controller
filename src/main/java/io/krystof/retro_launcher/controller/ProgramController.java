package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.converters.ProgramMapper;
import io.krystof.retro_launcher.controller.dto.PlaybackTimelineEventDTO;
import io.krystof.retro_launcher.controller.dto.ProgramDTO;
import io.krystof.retro_launcher.controller.dto.ProgramDiskImageDTO;
import io.krystof.retro_launcher.controller.dto.ProgramLaunchArgumentDTO;
import io.krystof.retro_launcher.controller.jpa.entities.*;
import io.krystof.retro_launcher.controller.jpa.images.DiskImageStorageDAO;
import io.krystof.retro_launcher.controller.jpa.repositories.AuthorRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.PlatformBinaryRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramDiskImageRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/program")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class ProgramController {

    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;
    private final PlatformBinaryRepository platformBinaryRepository;
    private final AuthorRepository authorRepository;


    public ProgramController(ProgramRepository programRepository, ProgramMapper programMapper, PlatformBinaryRepository platformBinaryRepository, AuthorRepository authorRepository) {
        this.programRepository = programRepository;
        this.programMapper = programMapper;
        this.platformBinaryRepository = platformBinaryRepository;
        this.authorRepository = authorRepository;
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<ProgramDTO> updateProgram(
            @PathVariable Long id,
            @RequestBody ProgramDTO updates) {

        return programRepository.findById(id)
                .map(program -> {
                    // Update basic fields
                    program.setTitle(updates.getTitle());
                    program.setDescription(updates.getDescription());
                    program.setReleaseYear(updates.getReleaseYear());
                    program.setContentRating(updates.getContentRating());
                    program.setCurationStatus(updates.getCurationStatus());
                    program.setCuratorNotes(updates.getCuratorNotes());

                    // Update platform binary if changed
                    if (updates.getPlatformBinary() != null) {
                        PlatformBinary binary = platformBinaryRepository.findById(
                                updates.getPlatformBinary().getId()
                        ).orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "Invalid platform binary ID"
                        ));
                        program.setPlatformBinary(binary);
                    }

                    // Update authors if changed
                    if (updates.getAuthors() != null) {
                        Set<Author> authors = updates.getAuthors().stream()
                                .map(authorDto -> authorRepository.findById(authorDto.getId())
                                        .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST, "Invalid author ID"
                                        )))
                                .collect(Collectors.toSet());
                        program.setAuthors(authors);
                    }

                    //handle disk images - pain in the ass with unique constraints, having to set -diskNumbers
                    //first since we have a constraint on disk number.  Probably could use some love down the
                    //road but it works for now, and we don't typically edit all of a program that often.
                    if (updates.getDiskImages() != null) {
                        // Get map of existing images by file hash for lookup
                        Map<String, ProgramDiskImage> existingImagesByHash = program.getDiskImages().stream()
                                .collect(Collectors.toMap(ProgramDiskImage::getFileHash, img -> img));

                        // Remove images that are not in the update
                        Set<String> updateHashes = updates.getDiskImages().stream()
                                .map(ProgramDiskImageDTO::getFileHash)
                                .collect(Collectors.toSet());

                        program.getDiskImages().forEach(img -> {
                            if (!updateHashes.contains(img.getFileHash())) {
                                img.setProgram(null);
                            }
                        });
                        program.getDiskImages().removeIf(img -> !updateHashes.contains(img.getFileHash()));

                        program = programRepository.save(program);

                        // First pass: set to temporary negative disk numbers
                        List<ProgramDiskImage> diskImages = updates.getDiskImages().stream()
                                .map(diskImageDto -> {
                                    // Try to find existing image with this hash
                                    ProgramDiskImage diskImage = existingImagesByHash.get(diskImageDto.getFileHash());
                                    if (diskImage != null) {
                                        // Use a temporary negative disk number to avoid unique constraints
                                        diskImage.setDiskNumber(-Math.abs(diskImageDto.getDiskNumber()));
                                    } else {
                                        // Only create new if it doesn't exist
                                        diskImage = new ProgramDiskImage();
                                        diskImage.setDiskNumber(-Math.abs(diskImageDto.getDiskNumber()));
                                        diskImage.setImageName(diskImageDto.getImageName());
                                        diskImage.setFileHash(diskImageDto.getFileHash());
                                        diskImage.setFileSize(diskImageDto.getFileSize());
                                    }

                                    return diskImage;
                                })
                                .collect(Collectors.toList());

                        program = programRepository.save(program);

                        // Second pass: set to actual positive disk numbers
                        for (int i = 0; i < diskImages.size(); i++) {
                            ProgramDiskImage diskImage = diskImages.get(i);
                            diskImage.setProgram(program);
                            diskImage.setDiskNumber(Math.abs(updates.getDiskImages().get(i).getDiskNumber()));
                        }

                        program.getDiskImages().clear();
                        program.getDiskImages().addAll(diskImages);
                    }

                    //handle launch arguments - pain in the ass with unique constraints, having to set -order
                    //first since we have a constraint on disk number.  Probably could use some love down the
                    //road but it works for now, and we don't typically edit all of a program that often.
                    if (updates.getLaunchArguments() != null) {


                        // Get map of existing images by file hash for lookup
                        Map<String, ProgramLaunchArgument> existingArgumentsByToString = program.getLaunchArguments().stream()
                                .collect(Collectors.toMap(ProgramLaunchArgument::toStringForRoughCompare, arg -> arg));

                        // Remove images that are not in the update
                        Set<String> updateToStrings = updates.getLaunchArguments().stream()
                                .map(ProgramLaunchArgumentDTO::toStringForRoughCompare)
                                .collect(Collectors.toSet());

                        program.getLaunchArguments().forEach(arg -> {
                            if (!updateToStrings.contains(arg.toStringForRoughCompare())) {
                                arg.setProgram(null);
                            }
                        });
                        program.getLaunchArguments().removeIf(arg -> !updateToStrings.contains(arg.toStringForRoughCompare()));

                        program = programRepository.save(program);

                        // First pass: set to temporary negative disk numbers
                        List<ProgramLaunchArgument> launchArgs = updates.getLaunchArguments().stream()
                                .map(programLaunchArgumentDTO -> {
                                    // Try to find existing image with this hash
                                    ProgramLaunchArgument programLaunchArgument = existingArgumentsByToString.get(programLaunchArgumentDTO.toStringForRoughCompare());
                                    if (programLaunchArgument != null) {
                                        // Use a temporary negative disk number to avoid unique constraints
                                        programLaunchArgument.setArgumentOrder(-Math.abs(programLaunchArgumentDTO.getArgumentOrder()));
                                    } else {
                                        // Only create new if it doesn't exist
                                        programLaunchArgument = new ProgramLaunchArgument();
                                        programLaunchArgument.setArgumentOrder(-Math.abs(programLaunchArgumentDTO.getArgumentOrder()));
                                        programLaunchArgument.setArgumentGroup(programLaunchArgumentDTO.getArgumentGroup());
                                        programLaunchArgument.setArgumentValue(programLaunchArgumentDTO.getArgumentValue());
                                        programLaunchArgument.setDescription(programLaunchArgumentDTO.getDescription());
                                    }

                                    return programLaunchArgument;
                                })
                                .collect(Collectors.toList());

                        program = programRepository.save(program);

                        // Second pass: set to actual positive disk numbers
                        for (int i = 0; i < launchArgs.size(); i++) {
                            ProgramLaunchArgument launchArgument = launchArgs.get(i);
                            launchArgument.setProgram(program);
                            launchArgument.setArgumentOrder(Math.abs(updates.getLaunchArguments().get(i).getArgumentOrder()));
                        }

                        program.getLaunchArguments().clear();
                        program.getLaunchArguments().addAll(launchArgs);
                    }

                    //handle playback timeline events - pain in the ass with unique constraints, having to set -order
                    //first since we have a constraint on sequence number.  Probably could use some love down the
                    //road but it works for now, and we don't typically edit all of a program that often.
                    if (updates.getPlaybackTimelineEvents() != null) {

                        // FOr these we'll just overlay any existing ones with new ones coming in, delete those no longer referenced, and save new unknowns.
                        // Get map of existing images by file hash for lookup
                        Map<Integer, PlaybackTimelineEvent> existingEventsBySequenceNumber = program.getPlaybackTimelineEvents().stream()
                                .collect(Collectors.toMap(PlaybackTimelineEvent::getSequenceNumber, arg -> arg));

                        Map<Integer, PlaybackTimelineEventDTO> updatedEventsBySequenceNumber =  updates.getPlaybackTimelineEvents().stream()
                                .collect(Collectors.toMap(PlaybackTimelineEventDTO::getSequenceNumber, arg -> arg));

                        // remove items that exist but aren't in the update
                        program.getPlaybackTimelineEvents().forEach(event -> {
                            if (!updatedEventsBySequenceNumber.containsKey(event.getSequenceNumber())) {
                                event.setProgram(null);
                            }
                        });

                        program.getPlaybackTimelineEvents().removeIf(arg -> !updatedEventsBySequenceNumber.containsKey(arg.getSequenceNumber()));

                        program = programRepository.save(program);

                        // First pass: set to temporary negative sequence numbers
                        List<PlaybackTimelineEvent> items = updates.getPlaybackTimelineEvents().stream()
                                .map(playbackTimelineEventDTO -> {
                                    // Try to find existing image with this hash
                                    PlaybackTimelineEvent playbackTimelineEvent = existingEventsBySequenceNumber.get(playbackTimelineEventDTO.getSequenceNumber());
                                    if (playbackTimelineEvent == null) {
                                        playbackTimelineEvent = new PlaybackTimelineEvent();
                                    }
                                    playbackTimelineEvent.setSequenceNumber(-Math.abs(playbackTimelineEventDTO.getSequenceNumber()));
                                    playbackTimelineEvent.setEventType(playbackTimelineEventDTO.getEventType());
                                    playbackTimelineEvent.setTimeOffsetSeconds(playbackTimelineEventDTO.getTimeOffsetSeconds());
                                    playbackTimelineEvent.setEventData(playbackTimelineEventDTO.getEventData());
                                    return playbackTimelineEvent;
                                })
                                .collect(Collectors.toList());

                        program = programRepository.save(program);

                        // Second pass: set to actual positive disk numbers
                        for (int i = 0; i < items.size(); i++) {
                            PlaybackTimelineEvent playbackTimelineEvent = items.get(i);
                            playbackTimelineEvent.setProgram(program);
                            playbackTimelineEvent.setSequenceNumber(Math.abs(updates.getPlaybackTimelineEvents().get(i).getSequenceNumber()));
                        }
                        program.getPlaybackTimelineEvents().clear();
                        program.getPlaybackTimelineEvents().addAll(items);

                    }

                    // Save updates
                    Program savedProgram = programRepository.save(program);

                    return ResponseEntity.ok(programMapper.toDto(savedProgram));
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Program not found"
                ));
    }

    @GetMapping("/{id}")
    public ProgramDTO getProgramById(@PathVariable Long id) {
        Program program = programRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found"));
        return programMapper.toDto(program);
    }

}
