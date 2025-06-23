package at.kalwoda.nocodeapi.service.dtos.entity;

import at.kalwoda.nocodeapi.domain.EntityModel;
import at.kalwoda.nocodeapi.service.dtos.field.FieldMinimalDto;
import at.kalwoda.nocodeapi.service.dtos.project.ProjectMinimalDto;

import java.util.List;

public record EntityDto(
        String apiKey,
        String name,
        ProjectMinimalDto project,
        List<FieldMinimalDto> fields
) {
    public EntityDto(EntityModel entityModel) {
        this(
                entityModel.getApiKey().value(),
                entityModel.getName(),
                new ProjectMinimalDto(entityModel.getProject()),
                entityModel.getFields()
                        .stream()
                        .map(FieldMinimalDto::new)
                        .toList()
        );
    }
}
