package io.krystof.retro_launcher.controller.util;


import io.krystof.retro_launcher.controller.jpa.entities.PlaybackTimelineEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlaybackTimelineConverter {
    /**
     * Converts a list of events with absolute timestamps to relative delays for the agent
     *
     * @param events List of events with absolute timestamps
     * @return List of events with relative delays
     */
    public static List<Map<String, Object>> convertToRelativeDelays(List<PlaybackTimelineEvent> events) {
        // First, sort events by absolute time
        List<PlaybackTimelineEvent> sortedEvents = events.stream()
                .sorted(Comparator.comparing(PlaybackTimelineEvent::getTimeOffsetSeconds))
                .collect(Collectors.toList());

        List<Map<String, Object>> agentEvents = new ArrayList<>();
        int previousTime = 0;

        for (PlaybackTimelineEvent event : sortedEvents) {
            // Calculate relative delay from previous event
            int relativeDelay = event.getTimeOffsetSeconds() - previousTime;

            // Create agent event format
            Map<String, Object> agentEvent = Map.of(
                    "event_type", event.getEventType().toString(),
                    "time_offset_seconds", relativeDelay,
                    "event_data", event.getEventData() != null ? event.getEventData() : Map.of()
            );

            agentEvents.add(agentEvent);
            previousTime = event.getTimeOffsetSeconds();
        }

        return agentEvents;
    }

    /**
     * Validates that a sequence of events has valid absolute timestamps
     *
     * @param events List of events to validate
     * @return true if valid, false if any timestamps are out of order
     */
    public static boolean validateAbsoluteTimeline(List<PlaybackTimelineEvent> events) {
        int previousTime = -1;
        for (PlaybackTimelineEvent event : events.stream()
                .sorted(Comparator.comparing(PlaybackTimelineEvent::getSequenceNumber))
                .collect(Collectors.toList())) {

            if (event.getTimeOffsetSeconds() < previousTime) {
                return false;
            }
            previousTime = event.getTimeOffsetSeconds();
        }
        return true;
    }
}