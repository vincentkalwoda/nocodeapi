package at.kalwoda.nocodeapi.service.commands;

import jakarta.validation.constraints.NotBlank;

public class EntityCommands {
    public record CreateEntityCommand(
            @NotBlank(message = "Name is required")
            String name
    ) {
    }

    public record UpdateEntityCommand(
            String apiKey,
            String name
    ) {
    }
}
