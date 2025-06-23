package at.kalwoda.nocodeapi.service.dtos.field;

import at.kalwoda.nocodeapi.domain.Field;
import at.kalwoda.nocodeapi.domain.FieldType;
import at.kalwoda.nocodeapi.domain.RelationshipType;
import at.kalwoda.nocodeapi.service.dtos.entity.EntityMinimalDto;

public record FieldDto(
        String apiKey,
        String name,
        FieldType fieldType,
        Boolean isRequired,
        String relationTarget,
        RelationshipType relationshipType,
        EntityMinimalDto entity
) {
    public FieldDto(Field field) {
        this(
                field.getApiKey().value(),
                field.getName(),
                field.getType(),
                field.getIsRequired(),
                field.getRelationTarget() != null ? field.getRelationTarget().value() : null,
                field.getRelationType() != null ? field.getRelationType() : null,
                new EntityMinimalDto(field.getEntity())
        );
    }
}
