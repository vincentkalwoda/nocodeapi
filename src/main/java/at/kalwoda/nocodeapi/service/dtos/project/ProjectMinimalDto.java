package at.kalwoda.nocodeapi.service.dtos.project;

import at.kalwoda.nocodeapi.domain.Project;

import java.util.Date;

public record ProjectMinimalDto(
        String apiKey,
        String name,
        String description,
        String promptText,
        Date createdAt
) {
    public ProjectMinimalDto(Project project) {
        this(
                project.getApiKey().value(),
                project.getName(),
                project.getDescription(),
                project.getPromptText(),
                project.getCreatedAt()
        );
    }
}
