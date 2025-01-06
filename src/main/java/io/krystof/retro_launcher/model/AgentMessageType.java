package io.krystof.retro_launcher.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

public enum AgentMessageType {
    STATUS_UPDATE,
    ERROR,
    // future message types...
}