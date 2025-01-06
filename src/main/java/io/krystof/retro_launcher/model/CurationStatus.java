package io.krystof.retro_launcher.model;

public enum CurationStatus {
    UNCURATED("Program has not been reviewed"),
    WORKING("Program has been verified as working"),
    BROKEN("Program has been verified as not working");

    private final String description;

    CurationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
