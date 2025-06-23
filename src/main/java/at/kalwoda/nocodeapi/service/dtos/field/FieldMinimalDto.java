package at.kalwoda.nocodeapi.service.dtos.field;

import at.kalwoda.nocodeapi.domain.Field;
import at.kalwoda.nocodeapi.domain.FieldType;

public record FieldMinimalDto(
        String apiKey,
        String name,
        FieldType type,
        Boolean required
) {
    public FieldMinimalDto(Field field) {
        this(
                field.getApiKey().value(),
                field.getName(),
                field.getType(),
                field.getIsRequired()
        );
    }
}
