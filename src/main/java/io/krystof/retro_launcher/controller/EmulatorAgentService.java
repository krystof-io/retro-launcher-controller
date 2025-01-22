package io.krystof.retro_launcher.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.krystof.retro_launcher.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.WebSocketClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EmulatorAgentService implements WebSocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(EmulatorAgentService.class);

    // WebSocket configuration
    private static final long HEARTBEAT_INTERVAL = 30000; // 30 seconds
    private static final long HEARTBEAT_TIMEOUT = 90000;  // 90 seconds
    private static final long BASE_DELAY = 250;          // 250ms initial delay
    private static final long MAX_RECONNECT_DELAY = 30000; // 30 seconds max delay

    private final WebSocketClient wsClient;
    private final String agentWsUrl;
    private final String agentRestUrl;
    private final ObjectMapper objectMapper;
    private final EmulatorAgentWebSocketMediator mediator;
    private final RestTemplate restTemplate;

    private WebSocketSession session;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private ScheduledFuture<?> heartbeatFuture;
    private volatile long lastHeartbeatResponse;
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
    private volatile boolean reconnecting = false;

    public EmulatorAgentService(
            WebSocketClient wsClient,
            ObjectMapper objectMapper,
            EmulatorAgentWebSocketMediator mediator,
            @Value("${agent.api.url}") String agentBaseUrl,
            RestTemplate restTemplate) {
        this.wsClient = wsClient;
        this.objectMapper = objectMapper;
        this.mediator = mediator;
        this.agentWsUrl = agentBaseUrl.replace("http", "ws") + "/ws";
        this.agentRestUrl = agentBaseUrl;
        this.restTemplate = restTemplate;
        this.lastHeartbeatResponse = System.currentTimeMillis();
    }

    @PostConstruct
    public void initialize() {
        logger.info("Initializing EmulatorAgentService");
        connect();
        startHeartbeatMonitor();
    }

    private void startHeartbeatMonitor() {
        // Schedule heartbeat sender
        heartbeatFuture = scheduler.scheduleAtFixedRate(() -> {
            try {
                if (session != null && session.isOpen()) {
                    // Send heartbeat message
                    AgentMessage<Void> heartbeat = new AgentMessage<>(AgentMessageType.HEARTBEAT, null);
                    String heartbeatMsg = objectMapper.writeValueAsString(heartbeat);
                    logger.info("Sending heartbeat message to agent: {}", heartbeatMsg);
                    session.sendMessage(new TextMessage(heartbeatMsg));

                    // Check for heartbeat timeout
                    if (System.currentTimeMillis() - lastHeartbeatResponse > HEARTBEAT_TIMEOUT) {
                        logger.warn("Heartbeat timeout detected, reconnecting...");
                        closeSession();
                        scheduleReconnect();
                    }
                }
            } catch (Exception e) {
                logger.error("Error sending heartbeat", e);
                scheduleReconnect();
            }
        }, 0, HEARTBEAT_INTERVAL, TimeUnit.MILLISECONDS);
    }

    public void connect() {
        try {
            logger.info("Attempting to connect to WebSocket at {}", agentWsUrl);
            logger.debug("Current session object: {}", session);
            if (session != null) {
                logger.debug("Session stats: {}", session.getAttributes());
                logger.debug("Session open: {}", session.isOpen());
                logger.debug("Session id: {}", session.getId());
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

    private void scheduleReconnect() {
        logger.info("Reconnecting flag currently: {}", reconnecting);
        if (!reconnecting || (session == null || !session.isOpen())) {
            logger.info("Going into reconnect mode.");
            reconnecting = true;
            long delay = Math.min(
                    BASE_DELAY * (long)Math.pow(2, reconnectAttempts.getAndIncrement()),
                    MAX_RECONNECT_DELAY
            );
            scheduler.schedule(this::connect, delay, TimeUnit.MILLISECONDS);
            logger.info("Scheduled WebSocket reconnection in {} ms", delay);
        }
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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        this.session = session;
        reconnecting = false;
        reconnectAttempts.set(0);
        lastHeartbeatResponse = System.currentTimeMillis();
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
        logger.debug("Inbound message from agent. Payload: {}", message.getPayload());
        if (message instanceof TextMessage textMessage) {
            try {
                JsonNode messageNode = objectMapper.readTree(textMessage.getPayload());
                AgentMessageType messageType = AgentMessageType.valueOf(messageNode.get("type").asText());

                // Update heartbeat timestamp for any received message
                lastHeartbeatResponse = System.currentTimeMillis();
                reconnectAttempts.set(0); // Reset reconnect attempts on successful message

                switch (messageType) {
                    case HEARTBEAT:
                        // Heartbeat response received, timestamp already updated
                        logger.info("Received heartbeat message from agent: {}", messageNode);
                        break;
                    case STATUS_UPDATE:
                        EmulatorStatus status = objectMapper.treeToValue(
                                messageNode.get("payload"),
                                EmulatorStatus.class
                        );
                        mediator.updateStatus(status);
                        break;
                    case ERROR:
                        JsonNode payload = messageNode.get("payload");
                        EmulatorError error = new EmulatorError(
                                payload.get("code").asText(),
                                payload.get("message").asText(),
                                objectMapper.convertValue(payload.get("details"), Map.class)
                        );
                        handleError(error);
                        break;
                    default:
                        logger.warn("Unknown message type: {}", messageType);
                }
            } catch (JsonProcessingException e) {
                logger.error("Error parsing message: {}", e.getMessage());
                handleError(new EmulatorError(
                        "MESSAGE_PARSE_ERROR",
                        "Failed to parse message from agent",
                        Map.of("error", e.getMessage())
                ));
            }
        }
    }

    private void handleError(EmulatorError error) {
        logger.error("Emulator error: {} - {}", error.getCode(), error.getMessage());
        mediator.notifyError(error);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public EmulatorStatus getCurrentStatus() {
        String url = agentRestUrl + "/status";
        logger.info("Making GET request to agent: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            EmulatorStatus status = objectMapper.convertValue(response.getBody(), EmulatorStatus.class);
            logger.info("Received direct response from agent: {}", response.getBody());
            return status;
        } catch (RestClientException e) {
            logger.error("Error making request to agent: ", e);
            throw new RuntimeException("Failed to communicate with agent service", e);
        }
    }

    public ResponseEntity<Map<String, Object>> postToAgent(String path, Object body) {
        String url = agentRestUrl + path;
        logger.info("Making POST request to agent: {} with body: {}", url, body);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            logger.info("Received response from agent: {}", response.getBody());
            return response;
        } catch (RestClientException e) {
            logger.error("Error making request to agent: ", e);
            throw new RuntimeException("Failed to communicate with agent service", e);
        }
    }

    @PreDestroy
    public void cleanup() {
        logger.info("Cleaning up EmulatorAgentService");
        if (heartbeatFuture != null) {
            heartbeatFuture.cancel(true);
        }
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        closeSession();
    }
}