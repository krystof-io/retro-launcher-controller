package io.krystof.retro_launcher.controller.resolvers;

import io.krystof.retro_launcher.controller.jpa.entities.ProgramDiskImage;

public class StoragePathResolver {
    public static String resolveStoragePath(ProgramDiskImage programDiskImage) {
        return String.format("%s/%s/%s/%s",
                programDiskImage.getProgram().getPlatform().getName().toLowerCase(),
                programDiskImage.getProgram().getType().toString().toLowerCase(),
                programDiskImage.getFileHash(),
                programDiskImage.getImageName());
    }
}
