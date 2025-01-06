package io.krystof.retro_launcher.model;

public enum ProgramType {
    DEMO("Demo programs showing platform capabilities"),
    GAME("Interactive entertainment software"),
    MUSIC("Music playback programs");

    private final String description;

    ProgramType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}