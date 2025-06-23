package at.kalwoda.nocodeapi.service.dtos.project;

import at.kalwoda.nocodeapi.domain.Project;
import at.kalwoda.nocodeapi.service.dtos.user.UserMinimalDto;

import java.util.Date;

public record ProjectDto(
        String apiKey,
        String name,
        String description,
        String promptText,
        Date createdAt,
        UserMinimalDto user
) {
    public ProjectDto(Project p) {
        this(
                p.getApiKey().value(),
                p.getName(),
                p.getDescription(),
                p.getPromptText(),
                p.getCreatedAt(),
                new UserMinimalDto(p.getUser())
        );
    }
}
