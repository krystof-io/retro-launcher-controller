package io.krystof.retro_launcher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.krystof.retro_launcher.model.EmulatorStatus;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EmulatorAgentService implements WebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(EmulatorAgentService.class);

    private final WebSocketClient wsClient;
    private final String agentWsUrl;
    private final ObjectMapper objectMapper;
    private final EmulatorAgentWebSocketMediator mediator;

    EmulatorStatus currentStatus;

    private WebSocketSession session;
    private final ScheduledExecutorService reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    private volatile boolean reconnecting = false;

    private AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private static final long baseDelay = 250;
    private static final long maxReconnectDelay = 5000;


    public EmulatorAgentService(
            WebSocketClient wsClient,
            ObjectMapper objectMapper,
            EmulatorAgentWebSocketMediator mediator,
            @Value("${agent.api.url}") String agentBaseUrl) {
        this.wsClient = wsClient;
        this.objectMapper = objectMapper;
        this.mediator = mediator;
        this.agentWsUrl = agentBaseUrl.replace("http", "ws") + "/ws";
    }

    @PostConstruct
    public void initialConstruction() {
        logger.info("Bootstrapping websocket connection.");
        scheduleReconnect();
    }

    public void connect() {
        try {
            logger.info("Attempting to connect to WebSocket at {}", agentWsUrl);
            logger.info("Current session object: {}",session);
            if (session != null) {
                logger.info("session stats: {}", session.getAttributes());
                logger.info("session open: {}", session.isOpen());
                logger.info("session id: {}", session.getId());
            }
            CompletableFuture<WebSocketSession> sessionCompletable = wsClient.execute(this, this.agentWsUrl);
            try {
                sessionCompletable.get(5, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                logger.error("Timeout connecting to WebSocket, scheduling reconnect", e);
                scheduleReconnect();
            }

        } catch (Exception e) {
            logger.error("Error connecting to WebSocket", e);
            scheduleReconnect();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        reconnecting = false; // Connection is successful
        reconnectAttempts.set(0);
        logger.info("Connected to agent WebSocket at {}", agentWsUrl);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        logger.error("Transport error, scheduling reconnect", exception);
        closeSession();
        scheduleReconnect();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        logger.info("Connection closed: {}, scheduling reconnect", closeStatus);
        closeSession();
        scheduleReconnect();
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {
        logger.info("Inbound message from agent. Payload: {}", message.getPayload());
        if (message instanceof TextMessage textMessage) {
            EmulatorStatus status = objectMapper.readValue(textMessage.getPayload(), EmulatorStatus.class);
            currentStatus = status;
            mediator.updateStatus(status);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private void closeSession() {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                logger.error("Error closing WebSocket session", e);
            }
        }
        session = null;
    }

    private void scheduleReconnect() {
        logger.info("Reconnecting flag currently: {}", reconnecting);
        if (!reconnecting || (session == null || !session.isOpen())) {
            logger.info("Going into reconnect mode.");
            reconnecting = true;
            long delay = Math.min(
                    baseDelay * (long)Math.pow(2, reconnectAttempts.getAndIncrement()),
                    maxReconnectDelay
            );
            reconnectExecutor.schedule(this::connect, delay, TimeUnit.MILLISECONDS);
            logger.info("Scheduled WebSocket reconnection in {} ms", delay);
        }
    }

    public EmulatorStatus getCurrentStatus() {
        return currentStatus;
    }
}
