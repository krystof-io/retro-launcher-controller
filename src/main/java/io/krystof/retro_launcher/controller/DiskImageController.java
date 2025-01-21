package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.converters.ProgramDiskImageMapper;
import io.krystof.retro_launcher.controller.dto.ProgramDiskImageDTO;
import io.krystof.retro_launcher.controller.jpa.entities.Program;
import io.krystof.retro_launcher.controller.jpa.entities.ProgramDiskImage;
import io.krystof.retro_launcher.controller.jpa.images.DiskImageStorageDAO;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramDiskImageRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramRepository;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@RestController
@RequestMapping("/api/disk-images")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class DiskImageController {
    private static final Logger logger = LoggerFactory.getLogger(DiskImageController.class);

    private final ProgramDiskImageRepository programDiskImageRepository;
    private final ProgramRepository programRepository;
    private final DiskImageStorageDAO diskImageStorageDAO;
    private final ProgramDiskImageMapper diskImageMapper;
    private final MessageDigest sha256;

    public DiskImageController(
            ProgramDiskImageRepository programDiskImageRepository,
            ProgramRepository programRepository,
            DiskImageStorageDAO diskImageStorageDAO,
            ProgramDiskImageMapper diskImageMapper) throws NoSuchAlgorithmException {
        this.programDiskImageRepository = programDiskImageRepository;
        this.programRepository = programRepository;
        this.diskImageStorageDAO = diskImageStorageDAO;
        this.diskImageMapper = diskImageMapper;
        this.sha256 = MessageDigest.getInstance("SHA-256");
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProgramDiskImageDTO> uploadDiskImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("programId") Long programId,
            @RequestParam("diskNumber") Integer diskNumber) throws IOException {

        logger.info("Uploading disk image for program {} with disk number {}", programId, diskNumber);

        // Get program
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Program not found"));

        // Calculate file hash
        String fileHash = Hex.encodeHexString(sha256.digest(file.getBytes()));

        // Check if file already exists
        Optional<ProgramDiskImage> existingImage = programDiskImageRepository.findByFileHash(fileHash);
        if (existingImage.isPresent()) {
            logger.info("File with hash {} already exists", fileHash);
            ProgramDiskImage diskImage = existingImage.get();
            diskImage.setProgram(program);
            diskImage.setDiskNumber(diskNumber);
            diskImage = programDiskImageRepository.save(diskImage);
            return ResponseEntity.ok(diskImageMapper.toDto(diskImage));
        }

        // Create new disk image record
        ProgramDiskImage diskImage = new ProgramDiskImage();
        diskImage.setProgram(program);
        diskImage.setDiskNumber(diskNumber);
        diskImage.setImageName(file.getOriginalFilename());
        diskImage.setFileHash(fileHash);
        diskImage.setFileSize(file.getSize());

        // Save to database to get ID
        diskImage = programDiskImageRepository.save(diskImage);

        // Upload to storage
        try {
            diskImageStorageDAO.storeDiskImage(diskImage.getStoragePath(), file);
        } catch (Exception e) {
            // If storage fails, delete database record
            programDiskImageRepository.delete(diskImage);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to store disk image: " + e.getMessage());
        }

        return ResponseEntity.ok(diskImageMapper.toDto(diskImage));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDiskImage(@PathVariable Long id) throws IOException {
        ProgramDiskImage diskImage = programDiskImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk image not found"));

        Resource diskImageResource = diskImageStorageDAO.getDiskImageAsResource(diskImage.getStoragePath());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + diskImage.getImageName() + "\"")
                .body(diskImageResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiskImage(@PathVariable Long id) {
        ProgramDiskImage diskImage = programDiskImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk image not found"));

        try {
            // Delete from storage first
            diskImageStorageDAO.deleteDiskImage(diskImage.getStoragePath());

            // Then delete database record
            programDiskImageRepository.delete(diskImage);

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete disk image: " + e.getMessage());
        }
    }
}