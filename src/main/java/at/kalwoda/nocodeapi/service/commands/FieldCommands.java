package at.kalwoda.nocodeapi.service.commands;

import jakarta.validation.constraints.NotBlank;

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
}
