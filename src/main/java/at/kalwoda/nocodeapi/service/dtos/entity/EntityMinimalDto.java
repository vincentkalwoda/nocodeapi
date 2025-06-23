package at.kalwoda.nocodeapi.service.dtos.entity;

import at.kalwoda.nocodeapi.domain.EntityModel;

public record EntityMinimalDto(
        String apiKey,
        String name
) {
    public EntityMinimalDto(EntityModel entityModel) {
        this(
                entityModel.getApiKey().value(),
                entityModel.getName()
        );
    }

}
