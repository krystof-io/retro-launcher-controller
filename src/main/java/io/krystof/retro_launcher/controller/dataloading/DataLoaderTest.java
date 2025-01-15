package io.krystof.retro_launcher.controller.dataloading;

import io.krystof.retro_launcher.controller.jpa.entities.*;
import io.krystof.retro_launcher.controller.jpa.repositories.AuthorRepository;
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
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
@Profile("oneshot")
public class DataLoaderTest {

    @Autowired
    ProgramRepository programRepository;
    @Autowired
    S3Client s3Client;
    @Autowired
    private PlatformRepository platformRepository;
    @Autowired
    private AuthorRepository authorRepository;
    MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");

    private static final Logger log = LoggerFactory.getLogger(DataLoaderTest.class);

    public DataLoaderTest() throws NoSuchAlgorithmException {
    }


    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setActiveProfiles("local", "oneshot");
        context.register(DataLoaderTestConfig.class);
        context.refresh();

        try {
            DataLoaderTest dataLoaderTestService = context.getBean(DataLoaderTest.class);
            dataLoaderTestService.doTheThing();
        } catch (Exception e) {
            log.error("Error doing the thing", e);
        }
    }

    private void doTheThing() throws NoSuchAlgorithmException, IOException {
        log.info("Doing the thing");


        String ourBucket = "retro-storage-dev";


        programRepository.deleteAll();
        platformRepository.deleteAll();
        authorRepository.deleteAll();

        //Clear our bucket!
        Bucket bucket = s3Client.listBuckets().buckets().stream().filter(b -> b.name().equals(ourBucket)).findFirst().orElseThrow();
        //Delete all entries from the bucket?
        s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(ourBucket).build()).contents().forEach(obj -> {
            log.info("Deleting object: {}", obj.key());
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(ourBucket).key(obj.key()).build());
        });

        Platform c64 = create64Platform();

        Author booze = createAuthor("Booze Design", "Booze Design is a group of people who make demos for the Commodore 64.");
        for (int x = 0; x < 100; x++ ){
            createAuthor("Gooze #"+x, "Gooze #"+x+" is a group of people who make demos for the Commodore 64.");
        }
        Author performers = createAuthor("Performers", "Performers is a group of people who make demos for the Commodore 64.");

        Program edgeOfDisgrace = createProgram("Edge of Disgrace", c64, c64.getBinaries().iterator().next(), ProgramType.DEMO, 2011,
                "Edge of Disgrace is a demo for the Commodore 64.", ContentRating.SAFE, CurationStatus.UNCURATED,
                "This is a demo that is very good.", booze);
        edgeOfDisgrace.setLaunchArguments(new ArrayList<>());
        edgeOfDisgrace.getLaunchArguments().add(createProgramLauchArgument(edgeOfDisgrace, 1, "-sid 8580", "sound", "SID model 8580"));
        edgeOfDisgrace.getLaunchArguments().add(createProgramLauchArgument(edgeOfDisgrace, 2, "-drive 8,9", "disk", "Use Disks 8,9"));
        edgeOfDisgrace = programRepository.save(edgeOfDisgrace);

        edgeOfDisgrace.setDiskImages(new ArrayList<>());
        handleDiskImage(ourBucket, edgeOfDisgrace, 1,
                Paths.get("src/test/resources/edge_of_disgrace/EdgeOfDisgrace_0.d64").toFile());
        handleDiskImage(ourBucket, edgeOfDisgrace, 2,
                Paths.get("src/test/resources/edge_of_disgrace/EdgeOfDisgrace_1a.d64").toFile());
        handleDiskImage(ourBucket, edgeOfDisgrace, 3,
                Paths.get("src/test/resources/edge_of_disgrace/EdgeOfDisgrace_1b.d64").toFile());

        edgeOfDisgrace = programRepository.save(edgeOfDisgrace);

        edgeOfDisgrace.setPlaybackTimelineEvents(new ArrayList<>());
        PlaybackTimelineEvent myPlaybackTimelineEvent = new PlaybackTimelineEvent();
        myPlaybackTimelineEvent.setEventType(PlaybackTimelineEventType.MOUNT_NEXT_DISK);
        myPlaybackTimelineEvent.setTimeOffsetSeconds(45);
        myPlaybackTimelineEvent.setProgram(edgeOfDisgrace);
        myPlaybackTimelineEvent.setSequenceNumber(1);
        edgeOfDisgrace.getPlaybackTimelineEvents().add(myPlaybackTimelineEvent);

        myPlaybackTimelineEvent = new PlaybackTimelineEvent();
        myPlaybackTimelineEvent.setEventType(PlaybackTimelineEventType.MOUNT_NEXT_DISK);
        myPlaybackTimelineEvent.setTimeOffsetSeconds(567);
        myPlaybackTimelineEvent.setProgram(edgeOfDisgrace);
        myPlaybackTimelineEvent.setSequenceNumber(2);
        edgeOfDisgrace.getPlaybackTimelineEvents().add(myPlaybackTimelineEvent);

        myPlaybackTimelineEvent = new PlaybackTimelineEvent();
        myPlaybackTimelineEvent.setEventType(PlaybackTimelineEventType.END_PLAYBACK);
        myPlaybackTimelineEvent.setTimeOffsetSeconds(323);
        myPlaybackTimelineEvent.setProgram(edgeOfDisgrace);
        myPlaybackTimelineEvent.setSequenceNumber(3);
        edgeOfDisgrace.getPlaybackTimelineEvents().add(myPlaybackTimelineEvent);

        myPlaybackTimelineEvent = new PlaybackTimelineEvent();
        myPlaybackTimelineEvent.setEventType(PlaybackTimelineEventType.PRESS_KEYS);
        myPlaybackTimelineEvent.setTimeOffsetSeconds(323);
        myPlaybackTimelineEvent.setProgram(edgeOfDisgrace);
        myPlaybackTimelineEvent.setEventData(new HashMap<>());
        myPlaybackTimelineEvent.getEventData().put("keys", "<F1>");
        myPlaybackTimelineEvent.setSequenceNumber(4);
        edgeOfDisgrace.getPlaybackTimelineEvents().add(myPlaybackTimelineEvent);


        edgeOfDisgrace = programRepository.save(edgeOfDisgrace);
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
        return programRepository.save(program);
    }

    private Author createAuthor(String performers, String s) {
        Author author = new Author();
        author.setName(performers);
        author.setDescription(s);
        return authorRepository.save(author);
    }

    private Platform create64Platform() {
        Platform c64 = new Platform();
        c64.setName("Commodore 64");
        c64.setDescription("The Commodore 64 is an 8-bit home computer introduced in January 1982 by Commodore International.");
        Set<PlatformBinary> platformBinaries = new HashSet<>();
        c64.setBinaries(platformBinaries);
        PlatformBinary c64Binary = new PlatformBinary();
        c64Binary.setPlatform(c64);
        c64Binary.setName("x64");
        c64Binary.setVariant("Default");
        c64Binary.setDescription("Vice-X64SC is a Commodore 64 emulator.");
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
        PlatformBinary binaryWithOtherOptions = new PlatformBinary();
        binaryWithOtherOptions.setPlatform(c64);
        binaryWithOtherOptions.setName("x64");
        binaryWithOtherOptions.setVariant("VIC Dual SID");
        binaryWithOtherOptions.setDescription("Vice-X64SC is a Commodore 64 emulator. This one is for special VIC Dual-SID mode.");
        Set<PlatformBinaryLaunchArgument> dualSidLaunchArgs = new HashSet<>();
        binaryWithOtherOptions.setLaunchArguments(dualSidLaunchArgs);
        PlatformBinaryLaunchArgument dualSidLaunchArg = new PlatformBinaryLaunchArgument();
        dualSidLaunchArg.setPlatformBinary(binaryWithOtherOptions);
        dualSidLaunchArg.setDescription("Arg XDXDAS");
        dualSidLaunchArg.setArgumentTemplate("arg1DUALSID");
        dualSidLaunchArg.setArgumentOrder(1);
        dualSidLaunchArg.setFileArgument(false);
        dualSidLaunchArg.setRequired(false);
        dualSidLaunchArgs.add(dualSidLaunchArg);

        dualSidLaunchArg = new PlatformBinaryLaunchArgument();
        dualSidLaunchArg.setPlatformBinary(binaryWithOtherOptions);
        dualSidLaunchArg.setDescription("auto");
        dualSidLaunchArg.setArgumentTemplate("assssss");
        dualSidLaunchArg.setArgumentOrder(999);
        dualSidLaunchArg.setFileArgument(true);
        dualSidLaunchArg.setRequired(true);
        dualSidLaunchArgs.add(dualSidLaunchArg);

        platformBinaries.add(binaryWithOtherOptions);
        return platformRepository.save(c64);
    }


}
