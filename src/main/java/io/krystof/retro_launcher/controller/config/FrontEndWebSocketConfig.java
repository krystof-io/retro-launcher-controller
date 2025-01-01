package io.krystof.retro_launcher.controller.config;


import io.krystof.retro_launcher.controller.FrontendWebSocketHandler;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class FrontEndWebSocketConfig implements WebSocketConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(FrontEndWebSocketConfig.class);

    private FrontendWebSocketHandler wsHandler;

    public FrontEndWebSocketConfig(FrontendWebSocketHandler wsHandler) {
        this.wsHandler = wsHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        logger.info("Registering WebSocket handler for /ws/status");
        registry.addHandler(wsHandler, "/ws/status")
                .setAllowedOrigins("http://localhost:5173");
    }

}