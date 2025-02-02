package io.krystof.retro_launcher.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    String status;  // "success" or "error"
    String message; // Optional message
    T data;        // Optional data payload
    List<String> errors;  // Optional list of errors

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .status("error")
                .message(message)
                .build();
    }

    public static ApiResponse<Void> error(String message, List<String> errors) {
        return ApiResponse.<Void>builder()
                .status("error")
                .message(message)
                .errors(errors)
                .build();
    }
}