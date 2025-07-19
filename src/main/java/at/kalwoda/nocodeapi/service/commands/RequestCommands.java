package at.kalwoda.nocodeapi.service.commands;

import at.kalwoda.nocodeapi.domain.MethodTypes;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public class RequestCommands {

    @Builder
    public record CreateRequestCommand(
            @NotBlank(message = "Path cannot be blank")
            String path,

            @NotNull(message = "Method cannot be null")
            MethodTypes method,

            String body,
            String queryParams,
            String headers,
            String response,

            @NotNull(message = "Status code cannot be null")
            int statusCode,
            String errorMessage,

            @NotNull(message = "Response time cannot be null")
            @Min(value = 0, message = "Response time must be non-negative")
            Long responseTime,
            String userAgent,
            String ipAddress
    ) {
    }
}
