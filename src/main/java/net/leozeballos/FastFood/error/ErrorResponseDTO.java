package net.leozeballos.FastFood.error;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record ErrorResponseDTO(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    Map<String, String> validationErrors
) {}
