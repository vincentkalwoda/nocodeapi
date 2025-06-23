package at.kalwoda.nocodeapi.service.commands;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public class EntityCommands {
    public record CreateEntityCommand(
            @NotBlank(message = "Name is required")
            String name
    ) {
    }

    public record UpdateEntityCommand(
            Optional<String> name
    ) {
    }
}
