package io.krystof.retro_launcher.controller.jpa.entities;

import io.krystof.retro_launcher.model.ContentRating;
import io.krystof.retro_launcher.model.CurationStatus;
import io.krystof.retro_launcher.model.ProgramType;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "program")
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    private Platform platform;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ProgramType type;

    @Column(name = "release_year")
    private Integer releaseYear;

    @ManyToMany
    @JoinTable(
            name = "program_author",
            joinColumns = @JoinColumn(name = "program_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<Author> authors = new HashSet<>();

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_rating")
    private ContentRating contentRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "curation_status", nullable = false)
    private CurationStatus curationStatus;

    @Column(name = "curator_notes")
    private String curatorNotes;

    @Column(name = "last_run_at")
    private OffsetDateTime lastRunAt;

    @Column(name = "run_count")
    private Integer runCount = 0;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "source_rating")
    private Double sourceRating;

    @Column(name = "source_id")
    private String sourceId;

    @ManyToOne
    @JoinColumn(name = "platform_binary_id")
    private PlatformBinary platformBinary;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("argumentOrder ASC")
    private List<ProgramLaunchArgument> launchArguments;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequenceNumber ASC")
    private List<PlaybackTimelineEvent> playbackTimelineEvents;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "program",cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("diskNumber ASC")
    private List<ProgramDiskImage> diskImages;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public ProgramType getType() {
        return type;
    }

    public void setType(ProgramType type) {
        this.type = type;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Set<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<Author> authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContentRating getContentRating() {
        return contentRating;
    }

    public void setContentRating(ContentRating contentRating) {
        this.contentRating = contentRating;
    }

    public CurationStatus getCurationStatus() {
        return curationStatus;
    }

    public void setCurationStatus(CurationStatus curationStatus) {
        this.curationStatus = curationStatus;
    }

    public String getCuratorNotes() {
        return curatorNotes;
    }

    public void setCuratorNotes(String curatorNotes) {
        this.curatorNotes = curatorNotes;
    }

    public List<PlaybackTimelineEvent> getPlaybackTimelineEvents() {
        return playbackTimelineEvents;
    }

    public void setPlaybackTimelineEvents(List<PlaybackTimelineEvent> playbackTimelineEvents) {
        this.playbackTimelineEvents = playbackTimelineEvents;
    }
    public OffsetDateTime getLastRunAt() {
        return lastRunAt;
    }

    public void setLastRunAt(OffsetDateTime lastRunAt) {
        this.lastRunAt = lastRunAt;
    }

    public Integer getRunCount() {
        return runCount;
    }

    public void setRunCount(Integer runCount) {
        this.runCount = runCount;
    }

    public PlatformBinary getPlatformBinary() {
        return platformBinary;
    }

    public void setPlatformBinary(PlatformBinary platformBinary) {
        this.platformBinary = platformBinary;
    }

    public List<ProgramLaunchArgument> getLaunchArguments() {
        return launchArguments;
    }

    public void setLaunchArguments(List<ProgramLaunchArgument> launchArguments) {
        this.launchArguments = launchArguments;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ProgramDiskImage> getDiskImages() {
        return diskImages;
    }

    public void setDiskImages(List<ProgramDiskImage> diskImages) {
        this.diskImages = diskImages;
    }
}