package at.kalwoda.nocodeapi.service.dtos.project;

import at.kalwoda.nocodeapi.domain.Project;
import at.kalwoda.nocodeapi.service.dtos.entity.EntityMinimalDto;
import at.kalwoda.nocodeapi.service.dtos.user.UserMinimalDto;

import java.util.Date;
import java.util.List;

public record ProjectDto(
        String apiKey,
        String name,
        String description,
        Date createdAt,
        UserMinimalDto user,
        List<EntityMinimalDto> entities
) {
    public ProjectDto(Project p) {
        this(
                p.getApiKey().value(),
                p.getName(),
                p.getDescription(),
                p.getCreatedAt(),
                new UserMinimalDto(p.getUser()),
                p.getEntities() == null ? null : p.getEntities().stream()
                        .map(EntityMinimalDto::new)
                        .toList()
        );
    }
}
