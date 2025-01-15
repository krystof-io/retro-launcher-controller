package io.krystof.retro_launcher.controller.config;

import io.krystof.retro_launcher.controller.converters.AuthorMapper;
import io.krystof.retro_launcher.controller.converters.ProgramMapper;
import io.krystof.retro_launcher.controller.jpa.entities.*;
import io.krystof.retro_launcher.controller.jpa.repositories.*;
import io.krystof.retro_launcher.model.ContentRating;
import io.krystof.retro_launcher.model.CurationStatus;
import io.krystof.retro_launcher.model.ProgramType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("localxxx") // Only runs in local profile
public class LocalDataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(LocalDataLoader.class);

    private final AuthorRepository authorRepo;
    private final PlatformRepository platformRepo;
    private final ProgramRepository programRepo;
    private final PlatformBinaryRepository binaryRepo;
    private final BinaryArgumentTemplateRepository binaryArgumentTemplateRepo;
    private final ProgramDiskImageRepository diskImageRepo;
    private final ProgramLaunchArgumentRepository launchArgRepo;

    private final ProgramMapper programMapper;
    private final AuthorMapper authorMapper;

    public LocalDataLoader(
            AuthorRepository authorRepo,
            PlatformRepository platformRepo,
            ProgramRepository programRepo,
            PlatformBinaryRepository binaryRepo,
            BinaryArgumentTemplateRepository binaryArgumentTemplateRepo,
            ProgramDiskImageRepository diskImageRepo,
            ProgramLaunchArgumentRepository launchArgRepo,
            AuthorMapper authorMapper,
            ProgramMapper programMapper) {
        this.authorRepo = authorRepo;
        this.platformRepo = platformRepo;
        this.programRepo = programRepo;
        this.binaryRepo = binaryRepo;
        this.binaryArgumentTemplateRepo = binaryArgumentTemplateRepo;
        this.diskImageRepo = diskImageRepo;
        this.launchArgRepo = launchArgRepo;
        this.authorMapper = authorMapper;
        this.programMapper = programMapper;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Loading development data...");

        // Create authors
        Author fairlight = createAuthor("Fairlight", "Legendary C64 demo group from Sweden");
        Author horizon = createAuthor("Horizon", "Prolific C64 demo group");

        // Get platform (should exist from Flyway migration)
        Platform c64 = platformRepo.findByName("C64")
                .orElseThrow(() -> new RuntimeException("C64 platform not found"));

        // Get default binary (should exist from Flyway migration)
        PlatformBinary x64sc = binaryRepo.findByPlatformIdAndIsDefaultTrue(c64.getId())
                .orElseThrow(() -> new RuntimeException("Default C64 binary not found"));

        // Create demo programs
        createDemo(
                "Edge of Disgrace",
                fairlight,
                c64,
                x64sc,
                2008,
                "Iconic C64 demo featuring amazing visual effects",
                "demos/eod/eod.d64",
                "sha256:abc123",
                174848
        );

        createDemo(
                "Desert Dream",
                horizon,
                c64,
                x64sc,
                1993,
                "Classic desert-themed demo",
                "demos/desert/dream.d64",
                "sha256:def456",
                174848
        );

        logger.info("Development data loading complete");

        printPlatformReport();
        printProgramReport();
        printAuthorReport();
        printProgramsByStatus();

    }

    private Author createAuthor(String name, String description) {
        return authorRepo.findByName(name).orElseGet(() -> {
            Author author = new Author();
            author.setName(name);
            author.setDescription(description);
            return authorRepo.save(author);
        });
    }

    private void createDemo(
            String title,
            Author author,
            Platform platform,
            PlatformBinary binary,
            int releaseYear,
            String description,
            String filePath,
            String fileHash,
            long fileSize) {

        Program program = new Program();
        program.setTitle(title);
        program.getAuthors().add(author);
        program.setPlatform(platform);
        program.setPlatformBinary(binary);
        program.setReleaseYear(releaseYear);
        program.setDescription(description);
        program.setType(ProgramType.DEMO);
        program.setContentRating(ContentRating.UNRATED);
        program.setCurationStatus(CurationStatus.UNCURATED);


        program = programRepo.save(program);

        // Create disk image
        ProgramDiskImage diskImage = createDiskImage(
                program,
                1,
                filePath,
                fileHash,
                fileSize,
                title + " Demo Disk"
        );

        // Set as boot disk

        programRepo.save(program);

        // Add some standard launch arguments
        ProgramLaunchArgument arg1 = new ProgramLaunchArgument();
        arg1.setProgram(program);
        arg1.setArgumentOrder(1);
        arg1.setArgumentValue("-sid 8580");
        arg1.setArgumentGroup("sound");
        arg1.setDescription("Use 8580 SID chip for better sound compatibility");
        launchArgRepo.save(arg1);

        ProgramLaunchArgument arg2 = new ProgramLaunchArgument();
        arg2.setProgram(program);
        arg2.setArgumentOrder(2);
        arg2.setArgumentValue("-model c64c");
        arg2.setArgumentGroup("hardware");
        arg2.setDescription("Use C64C model for compatibility");
        launchArgRepo.save(arg2);
    }

    private ProgramDiskImage createDiskImage(
            Program program,
            int diskNumber,
            String filePath,
            String fileHash,
            long fileSize,
            String displayName) {

        return diskImageRepo.findByFileHash(fileHash).orElseGet(() -> {
            ProgramDiskImage diskImage = new ProgramDiskImage();
            diskImage.setProgram(program);
            diskImage.setDiskNumber(diskNumber);
            diskImage.setImageName(filePath);
            diskImage.setFileHash(fileHash);
            diskImage.setFileSize(fileSize);
            //diskImage.setDisplayName(displayName);
//            diskImage.setStoragePath("c64/demos/" + fileHash + "/disk1.d64");
            return diskImageRepo.save(diskImage);
        });
    }

    private void printPlatformReport() {
        logger.info("\n=== Platforms ===");
        logger.info(String.format("%-10s | %-20s | %-15s | %s", "ID", "Name", "Default Binary", "Description"));
        logger.info("-".repeat(80));

        platformRepo.findAll().forEach(platform -> {
            PlatformBinary defaultBinary = binaryRepo.findByPlatformIdAndIsDefaultTrue(platform.getId()).orElse(null);
            logger.info(String.format("%-10d | %-20s | %-15s | %s",
                    platform.getId(),
                    platform.getName(),
                    defaultBinary != null ? defaultBinary.getName() : "None",
                    platform.getDescription()
            ));

            // Show binary argument templates
            binaryRepo.findByPlatformId(platform.getId()).forEach(binary -> {
                logger.info("   Binary: " + binary.getName());
                binaryArgumentTemplateRepo.findByPlatformBinaryIdOrderByArgumentOrder(binary.getId())
                        .forEach(template -> {
                            logger.info(String.format("      Arg[%d]: %s %s",
                                    template.getArgumentOrder(),
                                    template.getArgumentTemplate(),
                                    template.isFileArgument() ? "(file)" : ""
                            ));
                        });
            });
        });
    }

    private void printProgramReport() {
        logger.info("\n=== Programs ===");
        logger.info(String.format("%-30s | %-10s | %-10s | %-10s | %s",
                "Title", "Platform", "Type", "Status", "Author"));
        logger.info("-".repeat(100));

        programRepo.findAll().forEach(program -> {
            logger.info(String.format("%-30s | %-10s | %-10s | %-10s | %s",
                    program.getTitle(),
                    program.getPlatform().getName(),
                    program.getType(),
                    program.getCurationStatus(),
                    program.getAuthors() != null ? program.getAuthors().toString() : "Unknown"
            ));

            // Show disk images
            diskImageRepo.findByProgramIdOrderByDiskNumber(program.getId())
                    .forEach(disk -> {
                        logger.info(String.format("   Disk[%d]: %s (Hash: %s)",
                                disk.getDiskNumber(),
                                disk.getImageName(),
                                disk.getFileHash()
                        ));
                    });

            // Show launch arguments
            launchArgRepo.findByProgramIdOrderByArgumentOrder(program.getId())
                    .forEach(arg -> {
                        logger.info(String.format("   Arg[%d]: %s (%s)",
                                arg.getArgumentOrder(),
                                arg.getArgumentValue(),
                                arg.getArgumentGroup()
                        ));
                    });
        });
    }

    private void printAuthorReport() {
        logger.info("\n=== Authors ===");
        logger.info(String.format("%-20s | %-5s | %s",
                "Name", "Count", "Description"));
        logger.info("-".repeat(80));

        authorRepo.findAll().forEach(author -> {
            long programCount = programRepo.findByAuthorId(author.getId()).size();
            logger.info(String.format("%-20s | %-5d | %s",
                    author.getName(),
                    programCount,
                    author.getDescription()
            ));
        });
    }

    private void printProgramsByStatus() {
        logger.info("\n=== Programs By Status ===");
        programRepo.findByCurationStatus(CurationStatus.UNCURATED).forEach(program -> {
            logger.info("{}:{}",program.getCurationStatus(), program.getTitle());
        });

        Platform c64plat = platformRepo.findByName("C64").get();

        Sort.Direction sortDir = Sort.Direction.ASC;
        String sortField ="id";

        Specification<Program> spec = Specification.where(io.krystof.retro_launcher.controller.jpa.specifications.ProgramSpecifications.withEagerLoading())
                .and(io.krystof.retro_launcher.controller.jpa.specifications.ProgramSpecifications.withType(ProgramType.DEMO))
                .and(io.krystof.retro_launcher.controller.jpa.specifications.ProgramSpecifications.withPlatform(c64plat.getId()));

        Sort sorting = Sort.by(sortDir, sortField);
        Page<Program> programPage = programRepo.findAll(spec, PageRequest.of(0, 6, sorting));

        for (Program program : programPage) {
            logger.info("PROGRAM SPEC RESULT: {} {}",program.getTitle(),program.getId());
            logger.info("PROG DTO:{}",programMapper.toDto(program));
            logger.info("AUTHOR DTO:{}",authorMapper.toDtoSet(program.getAuthors()));
        }

    }
}