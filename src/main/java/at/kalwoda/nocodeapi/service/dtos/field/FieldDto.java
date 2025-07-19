package at.kalwoda.nocodeapi.service.dtos.field;

import at.kalwoda.nocodeapi.domain.Field;
import at.kalwoda.nocodeapi.domain.FieldType;
import at.kalwoda.nocodeapi.domain.RelationshipType;
import at.kalwoda.nocodeapi.service.dtos.constraints.ConstraintsDto;
import at.kalwoda.nocodeapi.service.dtos.entity.EntityMinimalDto;

import java.util.List;

public record FieldDto(
        String apiKey,
        String name,
        FieldType fieldType,
        List<ConstraintsDto> constraints,
        EntityMinimalDto entity
) {
    public FieldDto(Field field) {
        this(
                field.getApiKey().value(),
                field.getName(),
                field.getType(),
                field.getConstraints() == null ?
                        List.of() :
                        field.getConstraints()
                                .stream()
                                .map(ConstraintsDto::new)
                                .toList(),
                new EntityMinimalDto(field.getEntity())
        );
    }
}
