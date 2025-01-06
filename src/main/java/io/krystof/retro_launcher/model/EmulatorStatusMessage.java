package io.krystof.retro_launcher.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.web.socket.WebSocketMessage;

// Specific message type for EmulatorStatus
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmulatorStatusMessage extends AgentMessage<EmulatorStatus> {
    public EmulatorStatusMessage() {
        super(AgentMessageType.STATUS_UPDATE, null);
    }

    public EmulatorStatusMessage(EmulatorStatus status) {
        super(AgentMessageType.STATUS_UPDATE, status);
    }
}
