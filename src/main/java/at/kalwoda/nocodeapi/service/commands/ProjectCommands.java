package at.kalwoda.nocodeapi.service.commands;

import jakarta.validation.constraints.NotBlank;

public class ProjectCommands {
    public record CreateProjectCommand(
            @NotBlank(message = "Name is required")
            String name,

            String description
    ) {
    }
}
