package io.krystof.retro_launcher.controller.dto;


import io.krystof.retro_launcher.model.PlaybackTimelineEventType;

import java.util.Map;

public class PlaybackTimelineEventDTO {
    private Long id;
    private Integer sequenceNumber;
    private PlaybackTimelineEventType eventType;
    private Integer timeOffsetSeconds;
    private Map<String, Object> eventData;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getSequenceNumber() { return sequenceNumber; }
    public void setSequenceNumber(Integer sequenceNumber) { this.sequenceNumber = sequenceNumber; }

    public PlaybackTimelineEventType getEventType() { return eventType; }
    public void setEventType(PlaybackTimelineEventType eventType) { this.eventType = eventType; }

    public Integer getTimeOffsetSeconds() { return timeOffsetSeconds; }
    public void setTimeOffsetSeconds(Integer timeOffsetSeconds) { this.timeOffsetSeconds = timeOffsetSeconds; }

    public Map<String, Object> getEventData() { return eventData; }
    public void setEventData(Map<String, Object> eventData) { this.eventData = eventData; }

}