package io.krystof.retro_launcher.controller.dataloading;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.krystof.retro_launcher.controller.dataloading.model.CSDbGroup;
import io.krystof.retro_launcher.controller.dataloading.model.CSDbReleaseDetail;
import io.krystof.retro_launcher.controller.dataloading.model.ImageFile;
import io.krystof.retro_launcher.controller.jpa.entities.*;
import io.krystof.retro_launcher.controller.jpa.repositories.AuthorRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.PlatformBinaryRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.PlatformRepository;
import io.krystof.retro_launcher.controller.jpa.repositories.ProgramRepository;
import io.krystof.retro_launcher.model.ContentRating;
import io.krystof.retro_launcher.model.CurationStatus;
import io.krystof.retro_launcher.model.PlaybackTimelineEventType;
import io.krystof.retro_launcher.model.ProgramType;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
@Profile("oneshot")
public class ManualDataLoaderTest {

    @Autowired
    ProgramRepository programRepository;
    @Autowired
    S3Client s3Client;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private AuthorRepository authorRepository;

    MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final Logger log = LoggerFactory.getLogger(ManualDataLoaderTest.class);

    @Autowired
    private PlatformBinaryRepository platformBinaryRepository;


    @Value("${retro.storage.bucket}")
    private String bucketNameToUse;

    public ManualDataLoaderTest() throws NoSuchAlgorithmException {}

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("local", "oneshot");
        context.register(ManualDataLoaderTestConfig.class);
        context.refresh();

        try {
            ManualDataLoaderTest manualDataLoaderTestService = context.getBean(ManualDataLoaderTest.class);
            manualDataLoaderTestService.doTheThing();
        } catch (Exception e) {
            log.error("Error doing the thing", e);
        }
    }

    private void doTheThing() throws NoSuchAlgorithmException, IOException {
        log.info("Doing the thing, and our AWS bucket name is: {}", bucketNameToUse);


        Path imageRoots = Paths.get("C:\\dev\\git\\krystof.io\\retro-launcher-scraper\\csdb_data\\downloads");

        //Delete if we need to, otherwise soft create.
        //programRepository.deleteAll();
        //platformRepository.deleteAll();
        //authorRepository.deleteAll();
        //Clear our bucket!
        Bucket bucket = s3Client.listBuckets().buckets().stream().filter(b -> b.name().equals(bucketNameToUse)).findFirst().orElseThrow();
        //Delete all entries from the bucket?
//        s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketNameToUse).build()).contents().forEach(obj -> {
//            log.info("Deleting object: {}", obj.key());
//            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketNameToUse).key(obj.key()).build());
//        });


        File jsonSourceFile = Paths.get("C:\\dev\\git\\krystof.io\\retro-launcher-scraper\\csdb_data\\releases\\csdb_detailed_releases.json").toFile();

        Platform c64 = platformRepository.findByName("Commodore 64").orElseGet(this::create64Platform);
        PlatformBinary c64Binary = platformBinaryRepository.findByPlatformId(c64.getId()).stream().findFirst().orElseThrow();

        List<CSDbReleaseDetail> releases = objectMapper.readValue(jsonSourceFile, objectMapper.getTypeFactory().constructCollectionType(List.class, CSDbReleaseDetail.class));
        releases.stream().forEach(release -> {
            log.info("Release: {}:{}", release.getReleaseId(),release.getTitle());
            log.info("Groups: {}", release.getGroups());
            log.info("Downloads: {}", release.getDownloads());
            log.info("Images: {}", release.getImages());

            //Find the program if it exists, search by release id (source id)
            Program program = programRepository.findBySourceId(Integer.toString(release.getReleaseId())).orElseGet(() -> {
                log.info("Creating program: {}", release.getTitle());
                return createProgram(c64,c64Binary,release);
            });

            if (program.getId() != null) {
                log.info("Program {} already exists, skipping!",program.getTitle());
                return;
            }

            //Handle the disk images
            program.setDiskImages(new ArrayList<>());
            int diskNumber = 1;
            for (ImageFile imageFile : release.getImages()) {
                try {
                    handleDiskImage(bucketNameToUse, program, diskNumber, imageRoots.resolve(imageFile.getPath()).toFile());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                diskNumber++;
            }

            programRepository.save(program);

        });


    }

    private Program createProgram(Platform platform, PlatformBinary platformBinary ,CSDbReleaseDetail release) {
        Program myProgram  = new Program();
        myProgram.setTitle(release.getTitle());
        myProgram.setSourceId(Integer.toString(release.getReleaseId()));
        myProgram.setSourceUrl(release.getUrl());
        myProgram.setSourceRating(release.getRating());
        myProgram.setType(ProgramType.DEMO);
        myProgram.setAuthors(new HashSet<>(findOrCreateAuthors(release.getGroups())));
        myProgram.setPlatform(platform);
        myProgram.setPlatformBinary(platformBinary);
        myProgram.setCurationStatus(CurationStatus.UNCURATED);
        myProgram.setContentRating(ContentRating.UNRATED);
        myProgram.setReleaseYear(release.getReleaseDate().getYear());


        return myProgram;

    }

    private List<Author> findOrCreateAuthors(List<CSDbGroup> groups) {
        List<Author> authors = new ArrayList<>();
        groups.forEach(group -> {
            Author author = authorRepository.findByName(group.getName()).orElseGet(() -> createAuthor(group.getName(), group.getWebsiteUrl()));
            authors.add(author);
        });
        return authors;
    }

    private void handleDiskImage(String ourBucket, Program program, int diskNumber, File imageFile) throws IOException {

        String fileHash = Hex.encodeHexString(sha256Digest.digest(Files.readAllBytes(imageFile.toPath())));
        ProgramDiskImage programDiskImage = createProgramDiskImage(program, diskNumber, imageFile.getName(), fileHash, imageFile.length());
        program.getDiskImages().add(programDiskImage);
        uploadFile(ourBucket, programDiskImage.getStoragePath(), imageFile);
    }

    private void uploadFile(String ourBucket, String storagePath, File file) {
        log.info("Uploading file: {} to {}", file, storagePath);
        s3Client.putObject(builder -> builder.bucket(ourBucket).key(storagePath).build(), file.toPath());
    }

    private ProgramDiskImage createProgramDiskImage(Program program, int diskNumber, String filePath, String fileHash, long fileSize) {
        ProgramDiskImage programDiskImage = new ProgramDiskImage();
        programDiskImage.setProgram(program);
        programDiskImage.setDiskNumber(diskNumber);
        programDiskImage.setImageName(filePath);
        programDiskImage.setFileSize(fileSize);
        programDiskImage.setFileHash(fileHash);
        return programDiskImage;
    }

    private ProgramLaunchArgument createProgramLauchArgument(Program program, int argumentOrder, String argumentValue, String argumentGroup, String description) {
        ProgramLaunchArgument programLaunchArgument = new ProgramLaunchArgument();
        programLaunchArgument.setProgram(program);
        programLaunchArgument.setArgumentOrder(argumentOrder);
        programLaunchArgument.setArgumentValue(argumentValue);
        programLaunchArgument.setArgumentGroup(argumentGroup);
        programLaunchArgument.setDescription(description);
        return programLaunchArgument;
    }

    private Program createProgram(String title, Platform platform, PlatformBinary platformBinary, ProgramType programType, int year,
                                  String description, ContentRating contentRating, CurationStatus curationStatus, String curationNotes, Author author) {
        Program program = new Program();
        program.setTitle(title);
        program.setPlatform(platform);
        program.setPlatformBinary(platformBinary);
        program.setType(programType);
        program.setReleaseYear(year);
        program.setDescription(description);
        program.setContentRating(contentRating);
        program.setCurationStatus(curationStatus);
        program.setAuthors(new HashSet<>());
        program.getAuthors().add(author);
        program.setCuratorNotes(curationNotes);
        program.setSourceRating(6.1);
        program.setSourceUrl("https://someplaceouthere.com/release/1234324");
        program.setSourceId("1234324");
        return programRepository.save(program);
    }

    private Author createAuthor(String authorName, String desc) {
        log.info("Creating author: {}", authorName);
        Author author = new Author();
        author.setName(authorName);
        author.setDescription(desc);
        return authorRepository.save(author);
    }

    private Platform create64Platform() {
        Platform c64 = new Platform();
        c64.setName("Commodore 64");
        c64.setDescription("The classic demoscene machine!");
        Set<PlatformBinary> platformBinaries = new HashSet<>();
        c64.setBinaries(platformBinaries);
        PlatformBinary c64Binary = new PlatformBinary();
        c64Binary.setPlatform(c64);
        c64Binary.setName("x64");
        c64Binary.setVariant("Default");
        c64Binary.setDescription("Default X64 binary (non-SC) for hot PAL Demo action.");
        Set<PlatformBinaryLaunchArgument> c64BinaryLaunchArguments = new HashSet<>();
        c64Binary.setLaunchArguments(c64BinaryLaunchArguments);
        PlatformBinaryLaunchArgument c64BinaryLaunchArgument = new PlatformBinaryLaunchArgument();
//        c64BinaryLaunchArgument.setPlatformBinary(c64Binary);
//        c64BinaryLaunchArgument.setDescription("Arg 1");
//        c64BinaryLaunchArgument.setArgumentTemplate("arg1");
//        c64BinaryLaunchArgument.setArgumentOrder(1);
//        c64BinaryLaunchArgument.setFileArgument(false);
//        c64BinaryLaunchArgument.setRequired(false);
//        c64BinaryLaunchArguments.add(c64BinaryLaunchArgument);
//        c64BinaryLaunchArgument = new PlatformBinaryLaunchArgument();
//        c64BinaryLaunchArgument.setPlatformBinary(c64Binary);
//        c64BinaryLaunchArgument.setDescription("Arg 2");
//        c64BinaryLaunchArgument.setArgumentTemplate("arg2");
//        c64BinaryLaunchArgument.setArgumentOrder(2);
//        c64BinaryLaunchArgument.setFileArgument(false);
//        c64BinaryLaunchArgument.setRequired(false);
//        c64BinaryLaunchArguments.add(c64BinaryLaunchArgument);
        c64BinaryLaunchArgument = new PlatformBinaryLaunchArgument();
        c64BinaryLaunchArgument.setPlatformBinary(c64Binary);
        c64BinaryLaunchArgument.setDescription("The file to start");
        c64BinaryLaunchArgument.setArgumentTemplate("-autostart");
        c64BinaryLaunchArgument.setArgumentOrder(999);
        c64BinaryLaunchArgument.setFileArgument(true);
        c64BinaryLaunchArgument.setRequired(true);
        c64BinaryLaunchArguments.add(c64BinaryLaunchArgument);
        platformBinaries.add(c64Binary);
//        PlatformBinary binaryWithOtherOptions = new PlatformBinary();
//        binaryWithOtherOptions.setPlatform(c64);
//        binaryWithOtherOptions.setName("x64");
//        binaryWithOtherOptions.setVariant("VIC Dual SID");
//        binaryWithOtherOptions.setDescription("Vice-X64SC is a Commodore 64 emulator. This one is for special VIC Dual-SID mode.");
//        Set<PlatformBinaryLaunchArgument> dualSidLaunchArgs = new HashSet<>();
//        binaryWithOtherOptions.setLaunchArguments(dualSidLaunchArgs);
//        PlatformBinaryLaunchArgument dualSidLaunchArg = new PlatformBinaryLaunchArgument();
//        dualSidLaunchArg.setPlatformBinary(binaryWithOtherOptions);
//        dualSidLaunchArg.setDescription("Arg XDXDAS");
//        dualSidLaunchArg.setArgumentTemplate("arg1DUALSID");
//        dualSidLaunchArg.setArgumentOrder(1);
//        dualSidLaunchArg.setFileArgument(false);
//        dualSidLaunchArg.setRequired(false);
//        dualSidLaunchArgs.add(dualSidLaunchArg);
//
//        dualSidLaunchArg = new PlatformBinaryLaunchArgument();
//        dualSidLaunchArg.setPlatformBinary(binaryWithOtherOptions);
//        dualSidLaunchArg.setDescription("auto");
//        dualSidLaunchArg.setArgumentTemplate("assssss");
//        dualSidLaunchArg.setArgumentOrder(999);
//        dualSidLaunchArg.setFileArgument(true);
//        dualSidLaunchArg.setRequired(true);
//        dualSidLaunchArgs.add(dualSidLaunchArg);
//
//        platformBinaries.add(binaryWithOtherOptions);
        return platformRepository.save(c64);
    }


}
