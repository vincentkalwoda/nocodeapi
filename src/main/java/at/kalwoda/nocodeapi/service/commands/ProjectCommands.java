package at.kalwoda.nocodeapi.service.commands;

import jakarta.validation.constraints.NotBlank;

import java.util.Optional;

public class ProjectCommands {
    public record CreateProjectCommand(
            @NotBlank(message = "Name is required")
            String name,

            String description
    ) {
    }

    public record UpdateProjectCommand(
            Optional<String> name,

            Optional<String> description,
            Optional<String> promptText
    ) {
    }
}
