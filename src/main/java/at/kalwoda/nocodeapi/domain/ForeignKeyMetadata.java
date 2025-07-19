package at.kalwoda.nocodeapi.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public record ForeignKeyMetadata(
        String targetEntity,
        String targetField,
        @Enumerated(EnumType.STRING)
        RelationshipType relationType
) {}

