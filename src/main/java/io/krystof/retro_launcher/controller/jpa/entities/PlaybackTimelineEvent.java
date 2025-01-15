package io.krystof.retro_launcher.controller.jpa.entities;


import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.krystof.retro_launcher.model.PlaybackTimelineEventType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "playback_timeline_event")
public class PlaybackTimelineEvent {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id", nullable = false)
    private Program program;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private PlaybackTimelineEventType eventType;

    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    @Column(name = "time_offset_seconds", nullable = false)
    private Integer timeOffsetSeconds;

    @Type(JsonBinaryType.class)
    @Column(name = "event_data", columnDefinition = "jsonb")
    private Map<String, Object> eventData;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public PlaybackTimelineEventType getEventType() {
        return eventType;
    }

    public void setEventType(PlaybackTimelineEventType eventType) {
        this.eventType = eventType;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer executionOrder) {
        this.sequenceNumber = executionOrder;
    }

    public Integer getTimeOffsetSeconds() {
        return timeOffsetSeconds;
    }

    public void setTimeOffsetSeconds(Integer delaySeconds) {
        this.timeOffsetSeconds = delaySeconds;
    }

    public Map<String, Object> getEventData() {
        return eventData;
    }

    public void setEventData(Map<String, Object> commandData) {
        this.eventData = commandData;
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
}