package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.controller.jpa.entities.ProgramDiskImage;
import io.krystof.retro_launcher.controller.jpa.images.DiskImageStorageDAO;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramDiskImageRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/disk-images")
@CrossOrigin(origins = "${frontend.allowed.origins}")
public class DiskImageController {


    private final ProgramDiskImageRepository programDiskImageRepository;
    private final DiskImageStorageDAO diskImageStorageDAO;

    public DiskImageController( ProgramDiskImageRepository programDiskImageRepository, DiskImageStorageDAO diskImageStorageDAO) {
        this.programDiskImageRepository = programDiskImageRepository;
        this.diskImageStorageDAO = diskImageStorageDAO;
    }


    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadDiskImage(@PathVariable Long id) throws UnsupportedEncodingException {

        ProgramDiskImage diskImage = programDiskImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Disk image not found"));

        Resource diskImageResource = diskImageStorageDAO.getDiskImageAsResource(diskImage.getStoragePath());
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + diskImage.getImageName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        return new ResponseEntity<>(diskImageResource, headers, HttpStatus.OK);

    }
}
