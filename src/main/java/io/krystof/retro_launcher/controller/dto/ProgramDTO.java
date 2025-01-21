package io.krystof.retro_launcher.controller.dto;

import io.krystof.retro_launcher.model.ContentRating;
import io.krystof.retro_launcher.model.CurationStatus;
import io.krystof.retro_launcher.model.ProgramType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class ProgramDTO {
    private Long id;
    private String title;
    private ProgramType type;
    private Integer releaseYear;
    private String description;
    private ContentRating contentRating;
    private CurationStatus curationStatus;
    private String curatorNotes;
    private OffsetDateTime lastRunAt;
    private Integer runCount;
    private String sourceUrl;
    private Double sourceRating;
    private String sourceId;
    private List<AuthorDTO> authors;
    private PlatformDTO platform;
    private PlatformBinaryDTO platformBinary;
    private List<ProgramDiskImageDTO> diskImages;
    private List<ProgramLaunchArgumentDTO> launchArguments;
    private List<PlaybackTimelineEventDTO> playbackTimelineEvents;

    public PlatformBinaryDTO getPlatformBinary() {
        return platformBinary;
    }

    public void setPlatformBinary(PlatformBinaryDTO platformBinary) {
        this.platformBinary = platformBinary;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ProgramType getType() { return type; }
    public void setType(ProgramType type) { this.type = type; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ContentRating getContentRating() { return contentRating; }
    public void setContentRating(ContentRating contentRating) { this.contentRating = contentRating; }

    public CurationStatus getCurationStatus() { return curationStatus; }
    public void setCurationStatus(CurationStatus curationStatus) { this.curationStatus = curationStatus; }

    public String getCuratorNotes() { return curatorNotes; }
    public void setCuratorNotes(String curatorNotes) { this.curatorNotes = curatorNotes; }

    public OffsetDateTime getLastRunAt() { return lastRunAt; }
    public void setLastRunAt(OffsetDateTime lastRunAt) { this.lastRunAt = lastRunAt; }

    public Integer getRunCount() { return runCount; }
    public void setRunCount(Integer runCount) { this.runCount = runCount; }

    public List<AuthorDTO> getAuthors() { return authors; }
    public void setAuthors(List<AuthorDTO> authors) { this.authors = authors; }

    public PlatformDTO getPlatform() { return platform; }
    public void setPlatform(PlatformDTO platform) { this.platform = platform; }

    public List<ProgramDiskImageDTO> getDiskImages() { return diskImages; }
    public void setDiskImages(List<ProgramDiskImageDTO> diskImages) { this.diskImages = diskImages; }

    public List<ProgramLaunchArgumentDTO> getLaunchArguments() { return launchArguments; }
    public void setLaunchArguments(List<ProgramLaunchArgumentDTO> launchArguments) { this.launchArguments = launchArguments; }

    public List<PlaybackTimelineEventDTO> getPlaybackTimelineEvents() {
        return playbackTimelineEvents;
    }

    public void setPlaybackTimelineEvents(List<PlaybackTimelineEventDTO> playbackTimelineEvents) {
        this.playbackTimelineEvents = playbackTimelineEvents;
    }

    @Override
    public String toString() {
        return "ProgramDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", releaseYear=" + releaseYear +
                ", description='" + description + '\'' +
                ", contentRating=" + contentRating +
                ", curationStatus=" + curationStatus +
                ", curatorNotes='" + curatorNotes + '\'' +
                ", lastRunAt=" + lastRunAt +
                ", runCount=" + runCount +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", sourceRating=" + sourceRating +
                ", sourceId='" + sourceId + '\'' +
                ", authors=" + authors +
                ", platform=" + platform +
                ", platformBinary=" + platformBinary +
                ", diskImages=" + diskImages +
                ", launchArguments=" + launchArguments +
                ", playbackTimelineEvents=" + playbackTimelineEvents +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProgramDTO that = (ProgramDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && type == that.type && Objects.equals(releaseYear, that.releaseYear) && Objects.equals(description, that.description) && contentRating == that.contentRating && curationStatus == that.curationStatus && Objects.equals(curatorNotes, that.curatorNotes) && Objects.equals(lastRunAt, that.lastRunAt) && Objects.equals(runCount, that.runCount) && Objects.equals(sourceUrl, that.sourceUrl) && Objects.equals(sourceRating, that.sourceRating) && Objects.equals(sourceId, that.sourceId) && Objects.equals(authors, that.authors) && Objects.equals(platform, that.platform) && Objects.equals(platformBinary, that.platformBinary) && Objects.equals(diskImages, that.diskImages) && Objects.equals(launchArguments, that.launchArguments) && Objects.equals(playbackTimelineEvents, that.playbackTimelineEvents);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(title);
        result = 31 * result + Objects.hashCode(type);
        result = 31 * result + Objects.hashCode(releaseYear);
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + Objects.hashCode(contentRating);
        result = 31 * result + Objects.hashCode(curationStatus);
        result = 31 * result + Objects.hashCode(curatorNotes);
        result = 31 * result + Objects.hashCode(lastRunAt);
        result = 31 * result + Objects.hashCode(runCount);
        result = 31 * result + Objects.hashCode(sourceUrl);
        result = 31 * result + Objects.hashCode(sourceRating);
        result = 31 * result + Objects.hashCode(sourceId);
        result = 31 * result + Objects.hashCode(authors);
        result = 31 * result + Objects.hashCode(platform);
        result = 31 * result + Objects.hashCode(platformBinary);
        result = 31 * result + Objects.hashCode(diskImages);
        result = 31 * result + Objects.hashCode(launchArguments);
        result = 31 * result + Objects.hashCode(playbackTimelineEvents);
        return result;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Double getSourceRating() {
        return sourceRating;
    }

    public void setSourceRating(Double sourceRating) {
        this.sourceRating = sourceRating;
    }

}