package io.krystof.retro_launcher.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentMessage<T> {
    private AgentMessageType type;
    private String timestamp;
    private T payload;
    private String id;
    private Map<String, Object> error;

    // Constructors
    public AgentMessage() {}

    public AgentMessage(AgentMessageType type, T payload) {
        this.type = type;
        this.timestamp = Instant.now().toString();
        this.payload = payload;
    }

    // Getters and Setters
    public AgentMessageType getType() {
        return type;
    }

    public void setType(AgentMessageType type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getError() {
        return error;
    }

    public void setError(Map<String, Object> error) {
        this.error = error;
    }
}

