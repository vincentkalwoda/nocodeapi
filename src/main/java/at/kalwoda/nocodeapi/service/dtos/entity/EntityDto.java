package at.kalwoda.nocodeapi.service.dtos.entity;

import at.kalwoda.nocodeapi.domain.EntityModel;
import at.kalwoda.nocodeapi.service.dtos.project.ProjectMinimalDto;

public record EntityDto(
        String apiKey,
        String name,
        ProjectMinimalDto project
){
    public EntityDto(EntityModel entityModel) {
        this(
                entityModel.getApiKey().value(),
                entityModel.getName(),
                new ProjectMinimalDto(entityModel.getProject())
        );
    }
}
