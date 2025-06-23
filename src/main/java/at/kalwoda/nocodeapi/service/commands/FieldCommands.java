package at.kalwoda.nocodeapi.service.commands;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public class FieldCommands {
    public record CreateFieldCommand(
            @NotBlank(message = "Name is required")
            String name,
            @NotBlank(message = "Type is required")
            String type,
            Boolean required,
            String relationTargetApiKey,
            String relationType
    ) {
    }

    public record UpdateFieldCommand(
            Optional<String> name,
            Optional<String> type,
            Optional<Boolean> required,
            Optional<String> relationTargetApiKey,
            Optional<String> relationType
    ) {
    }
}
