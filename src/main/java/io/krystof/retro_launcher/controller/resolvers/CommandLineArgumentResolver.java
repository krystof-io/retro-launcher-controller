package io.krystof.retro_launcher.controller.resolvers;

import io.krystof.retro_launcher.controller.jpa.entities.PlatformBinaryLaunchArgument;
import io.krystof.retro_launcher.controller.jpa.entities.Program;

public class CommandLineArgumentResolver {
    public static  String buildCommandLineArgsWithoutBinaryOrDiskImage(Program program) {
        StringBuilder sb = new StringBuilder();
        program.getPlatformBinary().getLaunchArguments().stream().filter(PlatformBinaryLaunchArgument::isFileArgument).
                sorted((o1, o2) -> o1.getArgumentOrder()-o2.getArgumentOrder()).forEach(arg -> {
            if (!arg.isFileArgument()) {
                sb.append(arg.getArgumentTemplate());
                sb.append(" ");
            }
        });

        program.getLaunchArguments().stream().sorted((o1, o2) -> o1.getArgumentOrder()-o2.getArgumentOrder()).forEach(arg -> {
            sb.append(arg.getArgumentValue());
            sb.append(" ");
        });

        program.getPlatformBinary().getLaunchArguments().stream().filter(PlatformBinaryLaunchArgument::isFileArgument).forEach(arg -> {
            sb.append(arg.getArgumentTemplate());
        });

        return sb.toString();
    }
}
