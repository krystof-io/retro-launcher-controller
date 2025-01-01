package io.krystof.retro_launcher.controller.config;


import io.krystof.retro_launcher.controller.FrontendWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
public class EmulatorAgentWebSocketConfig {

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }
}