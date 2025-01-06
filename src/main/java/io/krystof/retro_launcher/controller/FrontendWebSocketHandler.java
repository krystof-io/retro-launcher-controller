package io.krystof.retro_launcher.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.krystof.retro_launcher.model.EmulatorError;

import io.krystof.retro_launcher.model.EmulatorStatus;
import io.krystof.retro_launcher.model.EmulatorStatusMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FrontendWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(FrontendWebSocketHandler.class);
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;
    private final EmulatorAgentService agentService;

    public FrontendWebSocketHandler(ObjectMapper objectMapper, EmulatorAgentService agentService, EmulatorAgentWebSocketMediator mediator) {
        this.objectMapper = objectMapper;
        this.agentService = agentService;
        mediator.setStatusListener(this::broadcastStatus);
        mediator.setErrorListener(this::broadcastError);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        // Send the current status to the client upon connection
        EmulatorStatus currentStatus = agentService.getCurrentStatus();
        if (currentStatus != null) {
            sendStatus(session, currentStatus);
        } else {
            logger.warn("No current status available to send on connect");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    public void broadcastStatus(EmulatorStatus status) {
        EmulatorStatusMessage message = new EmulatorStatusMessage(status);
        String messageJson = toJson(message);
        logger.info("Broadcasting status to {} sessions: {}", sessions.size(), messageJson);
        sessions.forEach(session -> sendMessage(session, messageJson));
    }

    private void broadcastError(EmulatorError error) {
        Map<String, Object> message = Map.of(
                "type", "ERROR",
                "timestamp", Instant.now().toString(),
                "payload", Map.of(
                        "code", error.getCode(),
                        "message", error.getMessage(),
                        "details", error.getDetails()
                )
        );

        String messageJson = toJson(message);
        logger.info("Broadcasting error to {} sessions: {}", sessions.size(), messageJson);
        sessions.forEach(session -> sendMessage(session, messageJson));
    }

    private void sendStatus(WebSocketSession session, EmulatorStatus status) {
        EmulatorStatusMessage message = new EmulatorStatusMessage(status);
        sendMessage(session, toJson(message));
    }

    private void sendMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            logger.error("Error sending message", e);
        }
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}