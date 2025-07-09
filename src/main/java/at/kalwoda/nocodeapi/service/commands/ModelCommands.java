package at.kalwoda.nocodeapi.service.commands;

import java.util.List;

public class ModelCommands {
    public record CreateModelCommand(
            List<CreateEntityModelCommand> entities
    ) {
    }

    public record CreateEntityModelCommand(
            String name,
            List<FieldCommands.CreateFieldCommand> fields
    ) {
    }
}
