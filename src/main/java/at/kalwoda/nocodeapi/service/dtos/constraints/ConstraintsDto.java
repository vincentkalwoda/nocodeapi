package at.kalwoda.nocodeapi.service.dtos.constraints;

import at.kalwoda.nocodeapi.domain.ConstraintDefinition;
import at.kalwoda.nocodeapi.domain.Constraints;
import at.kalwoda.nocodeapi.domain.ForeignKeyMetadata;

public record ConstraintsDto(
        Constraints constraintType,
        String value,
        ForeignKeyMetadata foreignKey
) {
    public ConstraintsDto(ConstraintDefinition constraintDefinition) {
        this(
                constraintDefinition.constraintType(),
                constraintDefinition.value(),
                constraintDefinition.foreignKey() == null ? null : constraintDefinition.foreignKey()
        );
    }
}
