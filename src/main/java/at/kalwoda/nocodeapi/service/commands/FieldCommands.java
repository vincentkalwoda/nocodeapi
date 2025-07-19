package at.kalwoda.nocodeapi.service.commands;

import at.kalwoda.nocodeapi.domain.ConstraintDefinition;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FieldCommands {
    public record CreateFieldCommand(
            @NotBlank(message = "Name is required")
            String name,
            @NotBlank(message = "Type is required")
            String type,
            Map<String, Object> constraints
    ) {
    }

    public record UpdateFieldCommand(
            Optional<String> name,
            Optional<String> type,
            Optional<String> relationTargetApiKey,
            Optional<String> relationType,
            Optional<Map<String, Object>> constraints
    ) {
    }
}
