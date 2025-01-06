package io.krystof.retro_launcher.model;

public enum ContentRating {
    UNRATED("Content has not been reviewed"),
    SAFE("Content verified as safe for all audiences"),
    NSFW("Content not suitable for all audiences");

    private final String description;

    ContentRating(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}