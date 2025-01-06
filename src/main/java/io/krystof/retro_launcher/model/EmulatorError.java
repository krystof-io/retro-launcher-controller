package io.krystof.retro_launcher.model;

import java.util.Map;

public class EmulatorError {
    private final String code;
    private final String message;
    private final Map<String, Object> details;

    public EmulatorError(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details != null ? details : Map.of();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
