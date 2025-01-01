package io.krystof.retro_launcher.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.krystof.retro_launcher.controller.EmulatorAgentService;
import io.krystof.retro_launcher.model.EmulatorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FrontendWebSocketHandler extends TextWebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(FrontendWebSocketHandler.class);
    private final Set<WebSocketSession> sessions =  ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper;
    private final EmulatorAgentService agentService;

    public FrontendWebSocketHandler(ObjectMapper objectMapper,  EmulatorAgentService agentService,EmulatorAgentWebSocketMediator mediator) {
        this.objectMapper = objectMapper;
        this.agentService = agentService;
        mediator.setStatusListener(this::broadcastStatus);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        // Send the current status to the client upon connection.
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
        String message = toJson(status);
        logger.info("Broadcasting status to {} sessions: {}", sessions.size(), message);
        sessions.forEach(session -> sendStatus(session, status));
    }

    private void sendStatus(WebSocketSession session, EmulatorStatus status) {
        try {
            session.sendMessage(new TextMessage(toJson(status)));
        } catch (IOException e) {
            logger.error("Error sending message", e);
        }
    }

    private String toJson(EmulatorStatus status) {
        try {
            return objectMapper.writeValueAsString(status);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}