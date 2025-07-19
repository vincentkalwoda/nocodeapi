package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record ConstraintDefinition(
        @Enumerated(EnumType.STRING)
        Constraints constraintType,
        String value,
        @Embedded
        ForeignKeyMetadata foreignKey
) {
}
