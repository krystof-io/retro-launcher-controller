package io.krystof.retro_launcher.controller;

import io.krystof.retro_launcher.model.EmulatorError;
import io.krystof.retro_launcher.model.EmulatorStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
public class EmulatorAgentWebSocketMediator {
    private Consumer<EmulatorStatus> statusListener;
    private Consumer<EmulatorError> errorListener;
    private static final Logger logger = LoggerFactory.getLogger(EmulatorAgentWebSocketMediator.class);

    public void setStatusListener(Consumer<EmulatorStatus> listener) {
        this.statusListener = listener;
        logger.info("Status listener set: {}",listener);
    }

    public void setErrorListener(Consumer<EmulatorError> listener) {
        this.errorListener = listener;
        logger.info("Error listener set: {}", listener);
    }

    public void updateStatus(EmulatorStatus status) {
        if (statusListener != null) {
            logger.info("Notifying status listener: {}",status);
            statusListener.accept(status);
        }
    }

    public void notifyError(EmulatorError error) {
        if (errorListener != null) {
            logger.info("Notifying error listener: {}", error);
            errorListener.accept(error);
        }
    }
}